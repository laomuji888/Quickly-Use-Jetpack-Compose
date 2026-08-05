package com.laomuji1999.compose.core.logic.repository.chat.impl

import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.HarmBlockThreshold
import com.google.firebase.ai.type.HarmCategory
import com.google.firebase.ai.type.SafetySetting
import com.google.firebase.ai.type.content
import com.laomuji1999.compose.core.logic.database.dao.ContactDao
import com.laomuji1999.compose.core.logic.database.dao.MessageDao
import com.laomuji1999.compose.core.logic.model.entity.MessageInfoEntity
import com.laomuji1999.compose.core.logic.notification.NotificationHelper
import com.laomuji1999.compose.core.logic.repository.chat.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

internal class GoogleAiChat(
    private val contactDao: ContactDao,
    private val messageDao: MessageDao,
    private val notificationHelper: NotificationHelper
) : ChatRepository {

    private suspend fun getHistory(account: Long) =
        messageDao.getMessageList(account = account).first().map { message ->
            content(
                role = if (message.isSend) {
                    "user"
                } else {
                    "model"
                }
            ) {
                text(message.text)
            }
        }

    override suspend fun sendMessage(
        account: Long,
        text: String,
        nickname: String
    ) {
        messageDao.insert(
            MessageInfoEntity(
                account = account,
                text = text,
                isSend = true
            )
        )
        val generativeModel = Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
            modelName = "gemini-2.5-flash",
            systemInstruction = content {
                text("请像友好的${nickname}一样回复此聊天对话")
            },
            safetySettings = listOf(
                SafetySetting(HarmCategory.HARASSMENT, HarmBlockThreshold.NONE),
                SafetySetting(HarmCategory.HATE_SPEECH, HarmBlockThreshold.NONE),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, HarmBlockThreshold.NONE),
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, HarmBlockThreshold.NONE)
            )
        )
        try {
            generativeModel.startChat(history = getHistory(account = account))
                .sendMessage(text).text
        } catch (e: Exception) {
            e.message
        }?.let {
            val filterText = if (it.endsWith("\n")) {
                it.substring(0, it.length - 1)
            } else {
                it
            }
            val messageInfoEntity = MessageInfoEntity(
                account = account,
                text = filterText,
                isSend = false
            )
            messageDao.insert(messageInfoEntity)
            contactDao.getByAccount(account)?.let { contact ->
                notificationHelper.showNotification(
                    contactInfoEntity = contact,
                    messageInfoEntity = messageInfoEntity
                )
            }
        }
    }

    override fun getMessageList(account: Long): Flow<List<MessageInfoEntity>> =
        messageDao.getMessageList(account)
}