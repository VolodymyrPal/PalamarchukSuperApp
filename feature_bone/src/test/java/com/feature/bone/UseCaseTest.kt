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
        every { authRepository.shouldRefreshToken(session) } returns false

        val result = useCase.invoke().toList()

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
    fun `determineLoginStatus should return REQUIRE_WEAK_LOGIN when past session duration but within refresh threshold`() =
        runTest {
            // Given
            val session = createUserSession(LogStatus.LOGGED_IN, Date(baseTime))

            every { detector.isFirstAccess("isFirstAccess") } returns false
            every { authRepository.currentSession } returns flowOf(session)
            every { authRepository.shouldRefreshToken(session) } returns false


            // When
            val result = useCase.invoke().toList()

            // Then
            assertEquals(LogStatus.REQUIRE_WEAK_LOGIN, result.first())
        }

    @Test
    fun `determineLoginStatus should return TOKEN_REFRESH_REQUIRED when shouldRefresh is true and autoRefresh disabled`() =
        runTest {
            // Given
            val session = createUserSession(LogStatus.LOGGED_IN, Date(baseTime))

            every { detector.isFirstAccess("isFirstAccess") } returns false
            every { detector.isFirstAccess("Extend_Session_On_Start") } returns false
            every { authRepository.currentSession } returns flowOf(session)
            every { authRepository.shouldRefreshToken(session) } returns true
            every { sessionConfig.autoRefreshEnabled } returns false

            // When
            val result = useCase.invoke().toList()

            // Then
            assertEquals(LogStatus.TOKEN_REFRESH_REQUIRED, result.first())
        }

    @Test
    fun `determineLoginStatus should return TOKEN_AUTO_REFRESH when shouldRefresh is true and autoRefresh enabled`() =
        runTest {
            // Given
            val session = createUserSession(LogStatus.LOGGED_IN, Date(baseTime))

            every { detector.isFirstAccess("isFirstAccess") } returns false
            every { detector.isFirstAccess("Extend_Session_On_Start") } returns false
            every { authRepository.currentSession } returns flowOf(session)
            every { authRepository.shouldRefreshToken(session) } returns true
            every { sessionConfig.autoRefreshEnabled } returns true


            // When
            val result = useCase.invoke().toList()

            // Then
            assertEquals(LogStatus.TOKEN_AUTO_REFRESH, result.first())
        }

    @Test
    fun `determineLoginStatus should return NOT_LOGGED when past refresh threshold and shouldRefresh is false`() =
        runTest {
            // Given
            val session = createUserSession(LogStatus.LOGGED_IN, Date(baseTime))

            every { detector.isFirstAccess("isFirstAccess") } returns false
            every { authRepository.currentSession } returns flowOf(session)
            every { authRepository.shouldRefreshToken(session) } returns false


            // When
            val result = useCase.invoke().toList()

            // Then
            assertEquals(LogStatus.NOT_LOGGED, result.first())
        }

    @Test
    fun `handleActiveSession should extend session on first access with sufficient time delta`() =
        runTest {
            // Given
            val currentTime = Date(baseTime - 5000L) // Старое время логина на 5 секунд
            val session = createUserSession(LogStatus.LOGGED_IN, Date(baseTime))

            every { detector.isFirstAccess("isFirstAccess") } returns false
            every { detector.isFirstAccess("Extend_Session_On_Start") } returns true
            every { authRepository.currentSession } returns flowOf(session)
            every { authRepository.shouldRefreshToken(session) } returns false
            coEvery { authRepository.saveSession(any()) } returns AppResult.Success(Unit)


            // When
            val result = useCase.invoke().toList()

            // Then
            coVerify { authRepository.saveSession(any()) }
            assertEquals(LogStatus.LOGGED_IN, result.first())
        }

    @Test
    fun `handleActiveSession should not extend session when time delta is insufficient`() =
        runTest {
            // Given
            val currentTime = Date(baseTime - 500L) // Разница 500ms < 1000ms threshold
            val session = createUserSession(LogStatus.LOGGED_IN, Date(baseTime))

            every { detector.isFirstAccess("isFirstAccess") } returns false
            every { detector.isFirstAccess("Extend_Session_On_Start") } returns true
            every { authRepository.currentSession } returns flowOf(session)
            every { authRepository.shouldRefreshToken(session) } returns false


            // When
            val result = useCase.invoke().toList()

            // Then
            coVerify(exactly = 0) { authRepository.saveSession(any()) }
            assertEquals(LogStatus.LOGGED_IN, result.first())
        }

    @Test
    fun `handleActiveSession should return NOT_LOGGED when saveSession fails`() = runTest {
        // Given
        val currentTime = Date(baseTime - 5000L)
        val session = createUserSession(LogStatus.LOGGED_IN, Date(baseTime))

        every { detector.isFirstAccess("isFirstAccess") } returns false
        every { detector.isFirstAccess("Extend_Session_On_Start") } returns true
        every { authRepository.currentSession } returns flowOf(session)
        every { authRepository.shouldRefreshToken(session) } returns false
        coEvery { authRepository.saveSession(any()) } returns AppResult.Error(AppError.CustomError("Save failed"))


        // When
        val result = useCase.invoke().toList()

        // Then
        assertEquals(LogStatus.NOT_LOGGED, result.first())
    }

    @Test
    fun `handleActiveSession should not extend session when not first access`() = runTest {
        // Given
        val session = createUserSession(LogStatus.LOGGED_IN, Date(baseTime))

        every { detector.isFirstAccess("isFirstAccess") } returns false
        every { detector.isFirstAccess("Extend_Session_On_Start") } returns false
        every { authRepository.currentSession } returns flowOf(session)
        every { authRepository.shouldRefreshToken(session) } returns false


        // When
        val result = useCase.invoke().toList()

        // Then
        coVerify(exactly = 0) { authRepository.saveSession(any()) }
        assertEquals(LogStatus.LOGGED_IN, result.first())
    }

    @Test
    fun `invoke should emit distinct values only`() = runTest {
        // Given
        val session1 = createUserSession(LogStatus.LOGGED_IN, Date(baseTime))
        val session2 = createUserSession(LogStatus.LOGGED_IN, Date(baseTime)) // Same status
        val session3 = createUserSession(LogStatus.NOT_LOGGED, Date(baseTime)) // Different status

        every { detector.isFirstAccess("isFirstAccess") } returns false
        every { detector.isFirstAccess("Extend_Session_On_Start") } returns false
        every { authRepository.currentSession } returns flowOf(session1, session2, session3)
        every { authRepository.shouldRefreshToken(session1) } returns false
        every { authRepository.shouldRefreshToken(session2) } returns false
        every { authRepository.shouldRefreshToken(session3) } returns false


        // When
        val result = useCase.invoke().toList()

        // Then
        assertEquals(2, result.size) // Should skip duplicate LOGGED_IN
        assertEquals(LogStatus.LOGGED_IN, result[0])
        assertEquals(LogStatus.NOT_LOGGED, result[1])
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `should handle different autoRefreshEnabled configurations`(autoRefreshEnabled: Boolean) =
        runTest {
            // Given
            val session = createUserSession(LogStatus.LOGGED_IN, Date(baseTime))

            every { detector.isFirstAccess(any()) } returns false
            every { authRepository.currentSession } returns flowOf(session)
            every { authRepository.shouldRefreshToken(session) } returns true
            every { sessionConfig.autoRefreshEnabled } returns autoRefreshEnabled


            // When
            val result = useCase.invoke().toList()

            // Then
            val expectedStatus = if (autoRefreshEnabled) {
                LogStatus.TOKEN_AUTO_REFRESH
            } else {
                LogStatus.TOKEN_REFRESH_REQUIRED
            }
            assertEquals(expectedStatus, result.first())
        }
}