package com.laomuji1999.compose.core.logic.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.laomuji1999.compose.core.logic.database.Database
import com.laomuji1999.compose.core.logic.model.entity.MessageInfoEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class MessageDaoTest {
    private lateinit var messageDao: MessageDao
    private lateinit var db: Database

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, Database::class.java).build()
        messageDao = db.messageDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun getMessageList_returnsEmpty_whenNoMessages() = runTest {
        messageDao.getMessageList(123L).test {
            assertEquals(0, awaitItem().size)
        }
    }

    @Test
    fun insertAndGetMessageList() = runTest {
        val account = 123L
        val message = MessageInfoEntity(
            account = account,
            text = "Hello World",
            isSend = true
        )
        
        messageDao.getMessageList(account).test {
            assertEquals(0, awaitItem().size)
            messageDao.insert(message)
            val list = awaitItem()
            assertEquals(1, list.size)
            assertEquals("Hello World", list[0].text)
            assertEquals(account, list[0].account)
        }
    }

    @Test
    fun getMessageList_filtersByAccount() = runTest {
        val account1 = 1L
        val account2 = 2L
        messageDao.insert(MessageInfoEntity(account = account1, text = "Msg 1", isSend = true))
        messageDao.insert(MessageInfoEntity(account = account2, text = "Msg 2", isSend = true))

        messageDao.getMessageList(account1).test {
            val list = awaitItem()
            assertEquals(1, list.size)
            assertEquals("Msg 1", list[0].text)
        }
    }

    @Test
    fun getMessageList_sortsByTimestampDescending() = runTest {
        val account = 123L
        val m1 = MessageInfoEntity(account = account, text = "Oldest", isSend = true, timestamp = 100L)
        val m2 = MessageInfoEntity(account = account, text = "Newest", isSend = true, timestamp = 300L)
        val m3 = MessageInfoEntity(account = account, text = "Middle", isSend = true, timestamp = 200L)

        messageDao.insert(m1)
        messageDao.insert(m2)
        messageDao.insert(m3)

        messageDao.getMessageList(account).test {
            val list = awaitItem()
            assertEquals(3, list.size)
            assertEquals("Newest", list[0].text)
            assertEquals("Middle", list[1].text)
            assertEquals("Oldest", list[2].text)
        }
    }

    @Test
    fun getMessageList_sortsByMessageIdDescending_whenTimestampIsSame() = runTest {
        val account = 123L
        val m1 = MessageInfoEntity(account = account, text = "First Insert", isSend = true, timestamp = 100L)
        val m2 = MessageInfoEntity(account = account, text = "Second Insert", isSend = true, timestamp = 100L)

        messageDao.insert(m1)
        messageDao.insert(m2)

        messageDao.getMessageList(account).test {
            val list = awaitItem()
            assertEquals(2, list.size)
            // m2 was inserted later, so it should have a larger messageId
            // Sorting is timestamp DESC, messageId DESC
            assertEquals("Second Insert", list[0].text)
            assertEquals("First Insert", list[1].text)
        }
    }
}
