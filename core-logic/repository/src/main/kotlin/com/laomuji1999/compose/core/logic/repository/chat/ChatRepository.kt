package com.laomuji1999.compose.core.logic.repository.chat

import com.laomuji1999.compose.core.logic.model.entity.MessageInfoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
interface ChatRepository {
    suspend fun sendMessage(
        account: Long,
        text: String,
        nickname: String
    )

    fun getMessageList(account: Long): Flow<List<MessageInfoEntity>>
}