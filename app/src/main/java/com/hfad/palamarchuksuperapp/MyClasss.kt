package com.hfad.palamarchuksuperapp

import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.ContactsContract.Data
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.multibindings.IntoMap
import dagger.multibindings.StringKey
import java.math.BigDecimal
import java.util.ArrayDeque
import java.util.Deque
import javax.inject.Inject
import javax.inject.Singleton


class Automobile @Inject constructor(val pinocio: Pinocio) {
    @Inject
    lateinit var repair: Repair
}

class Pinocio @Inject constructor() {

    var count = 0
}

class AppRepository @AssistedInject constructor(
    val automobile: Lazy<Automobile>,
    @Assisted id: Int
) {
    @AssistedFactory
    interface AppRepositoryFactory {
        fun create(id: Int): AppRepository
    }
}

class NewClass @Inject constructor(
    val automobile: Automobile,
    val repair: Repair,
    val skillsViewModel: SkillsViewModel
) {
    private val id: Int = 5

    @Inject
    lateinit var appRepository: AppRepository.AppRepositoryFactory
    fun setupRepository(id: Int = 5): AppRepository {
        val maClass = appRepository.create(5)
        Log.d("$maClass", "${maClass.automobile}")
        return maClass
    }

    fun repair() {
        repair.repair()
    }
}

abstract class AutoMove {
    open fun moveForward() {
        Log.d("Do", "Something")
    }
}

interface Repair {
    fun repair()
}

class Workers @Inject constructor() : Repair {
    override fun repair() {
        Log.d("Workers", "Repairing")
    }
}


@Component(modules = [AppModule::class])
interface AppComponent {
    fun provideSkillsViewModel(): SkillsViewModel
    val newClass: NewClass
    fun inject(skillsFragment: SkillsFragment)
    fun inject(mainActivity: MainActivity)
    fun inject(fragmentActivity: FragmentActivity)

}

@Module(includes = [RepairModule::class])
object AppModule {
    @Provides
    fun getVibrator(context: Context): Vibrator {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            return vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            return context.getSystemService(AppCompatActivity.VIBRATOR_SERVICE) as Vibrator
        }
    }

    fun interface Vibro {
        fun vibro(millisecond: Long, amplitude: Int)
    }

    @Provides
    fun getVooModel(): SkillsViewModel {
        return SkillsViewModel()
    }
}

@Module
interface RepairModule {
    @Binds
    fun bindNewClass(workers: Workers): Repair
}















interface Command {
    fun handleInput(input: List<String>): Result
    enum class Status { INVALID, HANDLED, INPUT_COMPLETED }
    data class Result(val status: Status, val nestedCommandRouter: CommandRouter? = null) {
        companion object {
            fun invalid(): Result {
                return Result(Status.INVALID) }

            fun handled(): Result {
                return Result(Status.HANDLED) }

            fun enterNestedCommandSet(nestedCommandRouter: CommandRouter): Result {
                return Result(Status.HANDLED, nestedCommandRouter) }
        }
    }
}

fun interface Outputter {
    fun output(output: String?)
}

class HelloWorldCommand @Inject constructor(val outputter: Outputter) : Command {
    override fun handleInput(input: List<String>): Command.Result {
        if (!input.isEmpty()) {
            return Command.Result.invalid() }
        outputter.output("world")
        return Command.Result.handled()
    }
}

abstract class BigDecimalCommand (open val outputter: Outputter) : SingleArgCommand() {
    override fun handleArg(amount: String?): Command.Result {
        val amount = tryParse(amount)
        if (amount == null) {
            outputter.output("$amount is not a valid number")
        } else if (amount.signum() <= 0) {
            outputter.output("amount must be positive")
        } else {
            handleAmount(amount)
        }
        return Command.Result.handled()
    }

    abstract fun handleAmount(amount: BigDecimal?)

    companion object {
        private fun tryParse(arg: String?): BigDecimal? {
            return try {
                BigDecimal(arg)
            } catch (e: NumberFormatException) {
                null
            }
        }
    }
}

class DepositCommand @Inject constructor (private val account: Database.Account, outputter: Outputter) :
    BigDecimalCommand(outputter) {

    override fun handleAmount(amount: BigDecimal?) {
        account.deposit(amount)
        outputter.output(account.username + " now has: " + account.balance())
    }
}

abstract class SingleArgCommand : Command {
    override fun handleInput(input: List<String>): Command.Result {
        return if (input.size == 1) handleArg(input[0]) else Command.Result.invalid()
    }
    abstract fun handleArg(username: String?): Command.Result
}

class LoginCommand @Inject constructor(
    val database: Database,
    val outputter: Outputter,
    val userCommandRouterFactory: UserCommandRouter.Factory
) : SingleArgCommand() {

    override fun handleArg(username: String?): Command.Result {
        val account: Database.Account = database.getAccount(username!!)
        outputter.output(username + " is logged in with balance: " + account.balance())
        return Command.Result.enterNestedCommandSet(userCommandRouterFactory.create(account).commandRouter)
    }
}

@Singleton
class CommandProcessor @Inject constructor(command: CommandRouter) {
    val commandRouterStack: Deque<CommandRouter> = ArrayDeque()
    init {
        commandRouterStack.push(command)
    }
    fun process(input: String?): Command.Status {
        val result = commandRouterStack.peek()?.route(input!!)
        if (result == Command.Result.handled()) {
            commandRouterStack.pop()
            return if (commandRouterStack.isEmpty()) Command.Status.HANDLED else Command.Status.INVALID
        }
        result?.nestedCommandRouter?.let { commandRouterStack.push(it) }
        return result?.status!!
    }
}

class CommandRouter @Inject constructor(var commands: Map<String, @JvmSuppressWildcards Command>) {

    fun route(input: String): Command.Result {
        val splitInput = input.trim().split(Regex("\\W+"))

        if (splitInput.isEmpty()) {
            return invalidCommand(input)
        }
        val commandKey = splitInput.first()
        val command = commands[commandKey]
        if (command == null) {
            return invalidCommand(input) }
        val args = splitInput.drop(1)
        val result = command.handleInput(args)
        return if (result == Command.Result.invalid()) {
            invalidCommand(input)
        } else {
            result
        }
    }

    private fun invalidCommand(input: String): Command.Result {
        println("Couldn't understand \"$input\". Please try again.")
        return Command.Result.invalid()
    }
}

@Singleton
@Component(modules = [InterfModule::class, SystemOutModule::class, HelloWorldModule::class, UserCommandRouter.InstallatoinModule::class])
interface atmComponent {
    val router: CommandProcessor?
    val account: Database.Account
}

@Subcomponent (modules = [InterfModule::class])
interface UserCommandRouter {
    val commandRouter: CommandRouter

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance account: Database.Account): UserCommandRouter
    }

    @Module(subcomponents = [UserCommandRouter::class])
    interface InstallatoinModule
}


@Module
object SystemOutModule {
    @Provides
    fun textOutputter(): Outputter {
        return Outputter { x: String? -> println(x) }
    }
    @Provides
    fun getAccount(): Database.Account {
        val a = Database.Account("")
        return a
    }


}

@Module
interface HelloWorldModule {
    @Binds
    @IntoMap
    @StringKey("hello")
    fun helloWorldCommand(command: HelloWorldCommand?): Command?
}

@Module
interface InterfModule {
    @Binds
    @IntoMap
    @StringKey("login")
    fun loginCommand(command: LoginCommand?): Command?

    @Binds
    @IntoMap
    @StringKey("deposit")
    fun depositCommand(command: DepositCommand): Command?
}

@Singleton
class Database @Inject constructor() {
    private val accounts: MutableMap<String, Account> = HashMap()

    fun getAccount(username: String): Account {
        return accounts.computeIfAbsent(username) {
            Account(it)
        }
    }

    class Account @Inject constructor (var username: String) {

        private var balance: BigDecimal = BigDecimal.TEN

        fun balance(): BigDecimal {
            return balance
        }

        fun deposit(amount: BigDecimal?) {
            balance = balance.add(amount)
        }
    }
}

