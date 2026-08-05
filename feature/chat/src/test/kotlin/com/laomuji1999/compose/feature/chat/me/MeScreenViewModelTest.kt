package com.laomuji1999.compose.feature.chat.me

import app.cash.turbine.test
import com.laomuji1999.compose.core.logic.common.cache.Cache
import com.laomuji1999.compose.core.logic.common.cache.CacheUtil
import com.laomuji1999.compose.core.logic.common.test.MainDispatcherRule
import com.laomuji1999.compose.core.logic.notification.NotificationHelper
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MeScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val cache: Cache = mockk()
    private lateinit var cacheUtil: CacheUtil
    private lateinit var viewModel: MeScreenViewModel

    @Before
    fun setup() {
        cacheUtil = CacheUtil(cache)
        // Initial value from cache
        every { cache.getBoolean(NotificationHelper.ENABLE_NOTIFICATION, false) } returns false
        
        viewModel = MeScreenViewModel(cacheUtil)
    }

    @Test
    fun `init should load enableNotification from cache`() = runTest(mainDispatcherRule.testDispatcher) {
        viewModel.uiState.test {
            assertEquals(false, awaitItem().enableNotification)
        }
    }

    @Test
    fun `onAction SwitchEnableNotification should toggle value and update cache`() = runTest(mainDispatcherRule.testDispatcher) {
        every { cache.putBoolean(NotificationHelper.ENABLE_NOTIFICATION, true) } returns Unit

        viewModel.uiState.test {
            awaitItem() // Initial false
            viewModel.onAction(MeScreenAction.SwitchEnableNotification)
            assertEquals(true, awaitItem().enableNotification)
        }

        verify { cache.putBoolean(NotificationHelper.ENABLE_NOTIFICATION, true) }
    }
}
