package com.laomuji1999.compose.feature.chat.chat

import app.cash.turbine.test
import com.laomuji1999.compose.core.logic.common.test.MainDispatcherRule
import com.laomuji1999.compose.core.logic.database.dao.ContactDao
import com.laomuji1999.compose.core.logic.model.entity.ContactInfoEntity
import com.laomuji1999.compose.core.logic.model.entity.MessageInfoEntity
import com.laomuji1999.compose.core.logic.notification.NotificationHelper
import com.laomuji1999.compose.core.logic.repository.chat.ChatRepository
import com.laomuji1999.compose.feature.chat.ChatGraph
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val account = 123L
    private val contactDao: ContactDao = mockk()
    private val chatRepository: ChatRepository = mockk()
    private val notificationHelper: NotificationHelper = mockk()

    private lateinit var viewModel: ChatScreenViewModel

    private val fakeContact = ContactInfoEntity(
        account = account,
        nickname = "Test User",
        category = "Friend",
        avatar = "avatar_url"
    )

    private val fakeMessages = listOf(
        MessageInfoEntity(account = account, text = "Hello", isSend = true),
        MessageInfoEntity(account = account, text = "Hi", isSend = false)
    )

    @Before
    fun setup() {
        coEvery { contactDao.getByAccount(account) } returns fakeContact
        every { chatRepository.getMessageList(account) } returns flowOf(fakeMessages)
    }

    private fun TestScope.createViewModel() = ChatScreenViewModel(
        account = account,
        contactDao = contactDao,
        chatRepository = chatRepository,
        notificationHelper = notificationHelper,
        ioCoroutineScope = backgroundScope
    )

    @Test
    fun `init should load contact info and message list`() = runTest {
        viewModel = createViewModel()
        
        viewModel.uiState.test {
            // Skip initial state
            assertEquals("", awaitItem().nickname)
            
            // Allow init block to run
            runCurrent()
            
            val state = awaitItem()
            assertEquals("Test User", state.nickname)
            assertEquals("avatar_url", state.receiveAvatar)
            assertEquals(fakeMessages, state.messageList)
        }
    }

    @Test
    fun `init should handle error when contact loading fails`() = runTest {
        coEvery { contactDao.getByAccount(account) } throws Exception("DB Error")
        
        viewModel = createViewModel()
        
        viewModel.uiState.test {
            assertEquals("", awaitItem().nickname) // Initial
            runCurrent()
            // In this case, it might not emit again if the error happens and state doesn't change from default
            // But getMessageList collection should still trigger an update to messageList
            val state = awaitItem()
            assertEquals("", state.nickname)
            assertEquals(fakeMessages, state.messageList)
        }
    }

    @Test
    fun `onAction SetInputText should update inputText`() = runTest {
        viewModel = createViewModel()
        
        viewModel.uiState.test {
            awaitItem() // Initial
            runCurrent() // After init
            awaitItem() // State after init (Test User)
            
            val newText = "New Message"
            viewModel.onAction(ChatScreenAction.SetInputText(newText))
            
            assertEquals(newText, awaitItem().inputText)
        }
    }

    @Test
    fun `onAction SendInputText should call repository and clear input immediately`() = runTest {
        viewModel = createViewModel()
        
        viewModel.uiState.test {
            awaitItem() // Initial
            runCurrent() // After init
            awaitItem() // State after init
            
            val textToSend = "Hello World"
            viewModel.onAction(ChatScreenAction.SetInputText(textToSend))
            awaitItem() // Input text update
            
            coEvery { chatRepository.sendMessage(account, textToSend, "Test User") } returns Unit

            viewModel.onAction(ChatScreenAction.SendInputText)
            
            // Advance to trigger ioCoroutineScope.launch
            runCurrent()

            coVerify { chatRepository.sendMessage(account, textToSend, "Test User") }
            assertEquals("", awaitItem().inputText)
        }
    }

    @Test
    fun `onAction DismissNotification should call notificationHelper`() = runTest {
        viewModel = createViewModel()
        runCurrent()
        
        every { notificationHelper.dismissNotification(fakeContact) } returns Unit

        viewModel.onAction(ChatScreenAction.DismissNotification)

        verify { notificationHelper.dismissNotification(fakeContact) }
    }

    @Test
    fun `onAction OnClickBack should emit Back to graph`() = runTest {
        viewModel = createViewModel()
        viewModel.graph.test {
            viewModel.onAction(ChatScreenAction.OnClickBack)
            assertEquals(ChatGraph.Back, awaitItem())
        }
    }
}
