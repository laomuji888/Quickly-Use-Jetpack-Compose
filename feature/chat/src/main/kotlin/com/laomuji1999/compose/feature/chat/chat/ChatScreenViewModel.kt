package com.laomuji1999.compose.feature.chat.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laomuji1999.compose.core.logic.common.dispatchers.IoCoroutineScope
import com.laomuji1999.compose.core.logic.database.dao.ContactDao
import com.laomuji1999.compose.core.logic.model.entity.ContactInfoEntity
import com.laomuji1999.compose.core.logic.model.entity.MessageInfoEntity
import com.laomuji1999.compose.core.logic.notification.NotificationHelper
import com.laomuji1999.compose.core.logic.repository.chat.ChatRepository
import com.laomuji1999.compose.core.ui.extension.emitGraph
import com.laomuji1999.compose.core.ui.extension.stateInTimeout
import com.laomuji1999.compose.feature.chat.ChatGraph
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ChatScreenViewModel.Factory::class)
class ChatScreenViewModel @AssistedInject constructor(
    @Assisted private val account: Long,
    contactDao: ContactDao,
    private val chatRepository: ChatRepository,
    private val notificationHelper: NotificationHelper,
    @param:IoCoroutineScope val ioCoroutineScope: CoroutineScope
) : ViewModel() {
    @AssistedFactory
    interface Factory {
        fun create(account: Long): ChatScreenViewModel
    }

    private val _graph = MutableSharedFlow<ChatGraph>()
    val graph = _graph.asSharedFlow()

    private val _contactInfo = MutableStateFlow<ContactInfoEntity?>(null)
    private val _messageList = MutableStateFlow<List<MessageInfoEntity>>(emptyList())

    init {
        viewModelScope.launch {
            try {
                _contactInfo.value = contactDao.getByAccount(account)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            chatRepository.getMessageList(account).collect {
                _messageList.value = it
            }
        }
    }

    private val _inputText = MutableStateFlow("")

    val uiState =
        combine(_contactInfo, _messageList, _inputText) { contactInfo, messageList, inputText ->
            ChatScreenUiState(
                receiveAvatar = contactInfo?.avatar ?: "",
                nickname = contactInfo?.nickname ?: "",
                messageList = messageList,
                inputText = inputText
            )
        }.stateInTimeout(viewModelScope, ChatScreenUiState())

    fun onAction(action: ChatScreenAction) {
        when (action) {
            is ChatScreenAction.SetInputText -> setInputText(action.text)
            ChatScreenAction.SendInputText -> sendInputText()
            ChatScreenAction.DismissNotification -> dismissNotification()
            ChatScreenAction.OnClickBack -> _graph.emitGraph(ChatGraph.Back)
        }
    }

    private fun setInputText(text: String) {
        _inputText.value = text
    }

    private fun sendInputText() {
        val contactInfo = _contactInfo.value ?: return
        val text = _inputText.value
        if (text.isEmpty()) {
            return
        }
        _inputText.value = ""
        ioCoroutineScope.launch {
            try {
                chatRepository.sendMessage(contactInfo.account, text, contactInfo.nickname)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun dismissNotification() {
        val contactInfo = _contactInfo.value ?: return
        notificationHelper.dismissNotification(contactInfo)
    }
}