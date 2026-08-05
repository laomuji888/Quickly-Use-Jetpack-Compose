package com.laomuji1999.compose.core.logic.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.laomuji1999.compose.core.logic.model.entity.MessageInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM MessageInfoEntity WHERE account = :account ORDER BY timestamp DESC, messageId DESC")
    fun getMessageList(account:Long): Flow<List<MessageInfoEntity>>

    @Insert
    suspend fun insert(messageInfoEntity: MessageInfoEntity)
}