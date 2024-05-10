package com.hfad.palamarchuksuperapp.di

import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.hfad.palamarchuksuperapp.view.screens.MainActivity
import com.hfad.palamarchuksuperapp.view.screens.SkillsFragment
import com.hfad.palamarchuksuperapp.view.screens.SkillsViewModel
import dagger.Binds
import dagger.BindsInstance
import dagger.BindsOptionalOf
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
import java.util.Optional
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Scope
import javax.inject.Singleton


class Automobile @Inject constructor() {
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

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun provideSkillsViewModel(): SkillsViewModel
    val newClass: NewClass
    fun inject(skillsFragment: SkillsFragment)
    //fun inject(mainActivity: MainActivity)
    fun inject(fragmentActivity: FragmentActivity)

    fun subAppComponentProvide(): SubAppComponent.Builder
}

@Subcomponent
interface SubAppComponent {
    val pinocio: Pinocio

    @Subcomponent.Builder
    interface Builder {
        fun build(): SubAppComponent
    }
}

@Module(includes = [RepairModule::class], subcomponents = [SubAppComponent::class])
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
                return Result(Status.INVALID)
            }

            fun handled(): Result {
                return Result(Status.HANDLED)
            }

            fun enterNestedCommandSet(nestedCommandRouter: CommandRouter): Result {
                return Result(Status.INPUT_COMPLETED, nestedCommandRouter)
            }
        }
    }
}

fun interface Outputter {
    fun output(output: String?)
}

class HelloWorldCommand @Inject constructor(val outputter: Outputter) : Command {
    override fun handleInput(input: List<String>): Command.Result {
        outputter.output("world")
        return Command.Result.handled()
    }
}


abstract class BigDecimalCommand(open val outputter: Outputter) : SingleArgCommand() {
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

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class PerSession


class DepositCommand @Inject constructor(
    private val account: Database.Account,
    outputter: Outputter,
    val withdrawal: WithdrawalLimiter
) :
    BigDecimalCommand(outputter) {

    override fun handleAmount(amount: BigDecimal?) {
        withdrawal.recordDeposit(amount)
        account.deposit(amount)
        outputter.output(account.username + " now has: " + account.balance())
    }
}

class WithdrawCommand @Inject constructor(
    private val account: Database.Account,
    outputter: Outputter,
    @MinimumBalance val minimumBalance: BigDecimal,
    @MaximumWithdrawal val maximumWithdrawal: BigDecimal,
    val withdrawalLimiter: WithdrawalLimiter
) : BigDecimalCommand(outputter) {

    fun errorMessage(text: String) {
        outputter.output(text)
    }

    override fun handleAmount(amount: BigDecimal?) {
        val remainingWithdrawalLimit = withdrawalLimiter.remainingWithdrawalLimit

        when {
            amount!! > maximumWithdrawal || amount > remainingWithdrawalLimit -> {
                errorMessage(
                    String.format(
                        "you may not withdraw %s; you may withdraw %s more in this session",
                        amount, remainingWithdrawalLimit
                    )
                )
                return
            }

            amount > account.balance() -> {
                errorMessage(
                    String.format(
                        "you may not withdraw %s; you may withdraw at least %s",
                        amount, minimumBalance
                    )
                )
                return
            }

            amount < minimumBalance -> {
                errorMessage(
                    String.format(
                        "you may not withdraw %s; you may withdraw at least %s",
                        amount, minimumBalance
                    )
                )
                return
            }

            else -> {
                withdrawalLimiter.recordWithdrawal(amount)
                account.withdraw(amount)
                outputter.output(account.username + " withdraw $amount and now has: " + account.balance())
            }
        }

    }
}

@PerSession
class WithdrawalLimiter @Inject constructor(@MaximumWithdrawal val maximumWithdrawal: BigDecimal) {
    var remainingWithdrawalLimit: BigDecimal = maximumWithdrawal

    fun recordDeposit(amount: BigDecimal?) {
        remainingWithdrawalLimit += amount!!
    }

    fun recordWithdrawal(amount: BigDecimal?) {
        remainingWithdrawalLimit -= amount!!
        Log.d("Amount left", "$remainingWithdrawalLimit")
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
    val userCommandRouterFactory: UserCommandRouter.Factory,
    val account: Optional<Database.Account?>
) : SingleArgCommand() {

    override fun handleArg(username: String?): Command.Result {
        if (!account.isPresent) {
            val account: Database.Account = database.getAccount(username!!)
            outputter.output(username + " is logged in with balance: " + account.balance())
            val a = Command.Result.enterNestedCommandSet(
                userCommandRouterFactory.create(
                    account,
                    5
                ).commandRouter
            )
            return a
        }
        return Command.Result.handled()
    }
}


class CommandProcessor @Inject constructor(commands: CommandRouter) {
    val commandRouterStack: Deque<CommandRouter> = ArrayDeque()

    init {
        commandRouterStack.push(commands)
    }

    fun process(input: String?): Command.Status {
        val result = commandRouterStack.peek()?.route(input!!)
        if (result == Command.Result.handled()) {

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
        Log.d("Command my: ", "$commandKey")
        val command = commands[commandKey]
        if (command == null) {
            return invalidCommand(input)
        }
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

class A

@Singleton
@Component(
    modules = [AppModulion::class, InterfModule::class, SystemOutModule::class, HelloWorldModule::class, UserCommandRouter.InstallationModule::class],
    dependencies = [AppDeps::class]
)
interface atmComponent: DatabaseDependencies {

    override fun getDatabase(): Database

    val router: CommandProcessor?
    val factoryShitt: Shitt.Factory
    val userCommandRouter: UserCommandRouter.Factory

//    @Component.Builder
//    interface Builder {
//        fun provideCon (appModule: AppModulion): Builder
//        // @BindsInstance fun providePin (pinocio: Pinocio): CompBuilder
//        fun build () : atmComponent
//    }

    @Component.Factory
    interface CompFactory {
        fun create(appModulion: AppModulion, appDeps: AppDeps): atmComponent
    }

    //Параметры из фабрики по типу: Int, String, Class и тд. @Named("...") обозначает их названия
//    @Component.Factory
//    interface CommandProvider {
//        fun commandProcessorProvide (@BindsInstance @Named("id") id: Int): atmComponent
//    }
}

interface AppDeps {
    val context: Context
}

@Module
class AppModulion(private val context: Context) {
}


class Shitt @AssistedInject constructor(
    @Assisted("id") private val id: Int,
    private val database: Database
) {

    fun doSome() {
        println("$id some")
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("id") user: Int): Shitt
    }
//    class Factory @Inject constructor() {
//        fun create (user: Int): Shitt {
//            return Shitt(user)
//        }
//    }
}

//class Shit @AssistedInject constructor (
//    @Assisted("id") fd: Int
//) {
//    init { Log.d("id", "${fd.toString()}") }
//    @AssistedFactory
//    interface FactoryShit {
//        fun create (@Assisted("id") user: Int): Shit
//    }
//}


@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class NewComponent

@NewComponent
@Component(
    modules = [InterfModule::class, BigDecimalModule::class, DepositModule::class, WithdrawModule::class],
    dependencies = [atmComponent::class]
)
//interface UserCommandRouter1 {
//    val commandRouter: CommandRouter
//    val commandProcessor: CommandProcessor
//
//    @Component.Factory
//    interface Factory {
//        fun create(
//            @BindsInstance account: Database.Account,
//            @BindsInstance id: Int,
//        ) : UserCommandRouter1
//    }
//}

interface DatabaseDependencies {
    fun getDatabase() : Database
}


@PerSession
@Subcomponent(modules = [InterfModule::class, BigDecimalModule::class, DepositModule::class, WithdrawModule::class])
interface UserCommandRouter {
    val commandRouter: CommandRouter
    val commandProcessor: CommandProcessor

    @Subcomponent.Factory
    interface Factory {
        fun create(
            @BindsInstance account: Database.Account,
            @BindsInstance id: Int
        ): UserCommandRouter
    }

    @Module(subcomponents = [UserCommandRouter::class])
    interface InstallationModule
}

@Module
class AppModulation {

}

@Qualifier
annotation class MinimumBalance

@Qualifier
annotation class MaximumWithdrawal


@Module
object BigDecimalModule {
    @Provides
    @MinimumBalance
    fun minimumBalance(): BigDecimal {
        return BigDecimal.ZERO
    }

    @Provides
    @MaximumWithdrawal
    fun maximumWithdrawal(): BigDecimal {
        return BigDecimal(1000)
    }

}

@Module
interface DepositModule {
    @Binds
    @IntoMap
    @StringKey("deposit")
    fun getDeposit(command: DepositCommand): Command
}

@Module
interface WithdrawModule {
    @Binds
    @IntoMap
    @StringKey("withdraw")
    fun getWithdraw(command: WithdrawCommand): Command
}


@Module
object SystemOutModule {
    @Provides
    fun textOutputter(): Outputter {
        return Outputter { x: String? -> println(x) }
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

    @BindsOptionalOf
    fun optionalAccount(): Database.Account?

}

@Singleton
class Database @Inject constructor() {
    val accounts: MutableMap<String, Account> = HashMap()


    fun getAccount(username: String): Account {
        return accounts.computeIfAbsent(username) {
            Account(it)
        }
    }


    class Account(var username: String) {
        private var balance: BigDecimal = BigDecimal(5000)
        fun balance(): BigDecimal {
            return balance
        }

        fun deposit(amount: BigDecimal?) {
            balance = balance.add(amount)
        }

        fun withdraw(amount: BigDecimal?) {
            balance = balance().minus(amount!!)
        }
    }
}



class UserLocalDataSource
class UserRemoteDataSource
class UserRepository @Inject constructor(
    private val localDataSource: UserLocalDataSource,
    private val remoteDataSource: UserRemoteDataSource
)

@App2
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
)



@Module
object myModuleDep {
    @Provides
    fun getUserRemoteDataSource () : UserRemoteDataSource {
        return UserRemoteDataSource()
    }
    @Provides
    fun getUserLocalDataSource () : UserLocalDataSource {
        return UserLocalDataSource()
    }
}

@Component (modules = [myModuleDep::class])
interface AppComponent2 {
    val userRepository: UserRepository
}



@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class App2




@App2
@Component(dependencies = [AppComponent2::class])
interface LoginComponent {

    @Component.Factory
    interface Factory {
        fun create(appComponent: AppComponent2): LoginComponent
    }

    fun inject(activity: MainActivity)
}