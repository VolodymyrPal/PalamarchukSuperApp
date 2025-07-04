package com.feature.bone

import com.hfad.palamarchuksuperapp.core.domain.AppResult
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.AuthRepositoryImpl
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.LogStatus
import com.hfad.palamarchuksuperapp.feature.bone.data.repository.SessionConfig
import com.hfad.palamarchuksuperapp.feature.bone.domain.repository.AuthRepository
import com.hfad.palamarchuksuperapp.feature.bone.domain.useCaseImpl.ObserveLoginStatusUseCaseImpl
import com.hfad.palamarchuksuperapp.feature.bone.domain.usecases.LogoutUseCase
import io.mockk.Runs
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import java.util.Date
import kotlin.test.Test
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
class ObserveLoginStatusUseCaseImplTest {

    private val authRepository = mockk<AuthRepository>()
    private val sessionConfig = SessionConfig()
    private val logoutUseCase = mockk<LogoutUseCase>()
    private val dateNow = Date()


    private lateinit var useCaseNonRefresh: ObserveLoginStatusUseCaseImpl
    private lateinit var useCaseWithRefresh: ObserveLoginStatusUseCaseImpl


    @BeforeEach
    fun setup() {
        clearAllMocks()
        useCaseNonRefresh =
            ObserveLoginStatusUseCaseImpl(authRepository, sessionConfig, logoutUseCase)

        useCaseWithRefresh = ObserveLoginStatusUseCaseImpl(
            authRepository, sessionConfig.copy(
                tokenRefreshEnable = true
            ), logoutUseCase
        )
        coEvery { logoutUseCase.invoke() } just Runs
        coEvery { authRepository.saveSession(any()) } returns AppResult.Success(Unit)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    fun generateSession(
        rememberSession: Boolean,
        loginTimestamp: Date,
        userStatus: LogStatus,
    ): AuthRepositoryImpl.UserSession {
        return AuthRepositoryImpl.UserSession(
            username = "",
            accessToken = "",
            refreshToken = "",
            rememberSession = rememberSession,
            userStatus = userStatus,
            loginTimestamp = loginTimestamp
        )
    }

    @Test
    fun `Unlogged session returns NOT_LOGGED`() = runTest {
        val session = generateSession(
            rememberSession = false,
            loginTimestamp = Date(),
            userStatus = LogStatus.NOT_LOGGED
        )

        every { authRepository.currentSession } returns flowOf(session)

        val result = useCaseNonRefresh.invoke().toList()

        assertEquals(LogStatus.NOT_LOGGED, result.first())
    }

    @Test
    fun `unremembered expired session returns NOT_LOGGED and triggers logout`() =
        runTest {
            val session = generateSession(
                rememberSession = false,
                loginTimestamp = Date(dateNow.time - sessionConfig.sessionTimeout - 1),
                userStatus = LogStatus.LOGGED_IN
            )

            every { authRepository.currentSession } returns flowOf(session)

            val result = useCaseNonRefresh.invoke().toList()

            coVerify(exactly = 1) { logoutUseCase.invoke() }
            assertEquals(LogStatus.NOT_LOGGED, result.first())
        }

    @Test
    fun `unremembered active session returns LOGGED_IN`() = runTest {
        val session = AuthRepositoryImpl.UserSession(
            username = "",
            accessToken = "",
            refreshToken = "",
            rememberSession = false,
            userStatus = LogStatus.LOGGED_IN,
            loginTimestamp = Date(dateNow.time - 1)
        )

        every { authRepository.currentSession } returns flowOf(session)

        val result = useCaseNonRefresh.invoke().toList()

        assertEquals(LogStatus.LOGGED_IN, result.first())
    }

    @Test
    fun `should be LOGGED_IN when remembered session is past refresh threshold`() = runTest {
        val session = generateSession(
            rememberSession = true,
            loginTimestamp = Date(dateNow.time - sessionConfig.refreshThreshold / 2),
            userStatus = LogStatus.LOGGED_IN
        )

        every { authRepository.currentSession } returns flowOf(session)

        val result = useCaseNonRefresh.invoke().toList()
        assertEquals(LogStatus.LOGGED_IN, result.first())

    }

    @Test
    fun `should be REQUIRE_WEAK_LOGIN when remembered session is past session duration but within refresh threshold`() =
        runTest {
            val session = generateSession(
                rememberSession = true,
                loginTimestamp = Date(dateNow.time - sessionConfig.refreshThreshold),
                userStatus = LogStatus.LOGGED_IN
            )

            every { authRepository.currentSession } returns flowOf(session)
            val result = useCaseNonRefresh.invoke().toList()

            assertEquals(LogStatus.REQUIRE_WEAK_LOGIN, result.first())
        }

    @Test
    fun `should be LOGOUT when remembered session is expired`() = runTest {
        val session = generateSession(
            rememberSession = true,
            loginTimestamp = Date(dateNow.time - sessionConfig.sessionTokenDuration),
            userStatus = LogStatus.LOGGED_IN
        )

        every { authRepository.currentSession } returns flowOf(session)
        val result = useCaseNonRefresh.invoke().toList()

        assertEquals(LogStatus.NOT_LOGGED, result.first())
    }

    @Test
    fun `should be LOGGED_IN when remembered refreshable session within refresh threshold`() = runTest {
        val session = generateSession(
            rememberSession = true,
            loginTimestamp = Date(dateNow.time-sessionConfig.refreshThreshold+1000),
            userStatus = LogStatus.LOGGED_IN
        )
        every { authRepository.currentSession } returns flowOf(session)

        val result = useCaseWithRefresh.invoke().toList()
        assertEquals(LogStatus.LOGGED_IN, result.first())
    }

    @Test
    fun `should be TOKEN_REFRESH_REQUIRED when remembered refreshable session within active session`() = runTest {
        val session = generateSession(
            rememberSession = true,
            loginTimestamp = Date(dateNow.time-sessionConfig.refreshThreshold-1),
            userStatus = LogStatus.LOGGED_IN
        )
        every { authRepository.currentSession } returns flowOf(session)

        val result = useCaseWithRefresh.invoke().toList()
        assertEquals(LogStatus.TOKEN_REFRESH_REQUIRED, result.first())
    }

    @Test
    fun `should be NOT_LOGGED when remembered refreshable session expired`() = runTest {
        val session = generateSession(
            rememberSession = true,
            loginTimestamp = Date(dateNow.time-sessionConfig.sessionTokenDuration-1),
            userStatus = LogStatus.LOGGED_IN
        )
        every { authRepository.currentSession } returns flowOf(session)

        val result = useCaseWithRefresh.invoke().toList()
        assertEquals(LogStatus.NOT_LOGGED, result.first())
    }
}