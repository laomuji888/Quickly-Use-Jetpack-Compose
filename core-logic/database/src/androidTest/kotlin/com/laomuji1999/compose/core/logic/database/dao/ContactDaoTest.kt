package com.laomuji1999.compose.core.logic.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.laomuji1999.compose.core.logic.database.Database
import com.laomuji1999.compose.core.logic.model.entity.ContactInfoEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ContactDaoTest {
    private lateinit var contactDao: ContactDao
    private lateinit var db: Database

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, Database::class.java).build()
        contactDao = db.contactDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetByAccount() = runTest {
        val contact = ContactInfoEntity(
            account = 123L,
            nickname = "Test",
            category = "Friend",
            avatar = "url"
        )
        contactDao.insert(contact)
        val byAccount = contactDao.getByAccount(123L)
        assertEquals(contact, byAccount)
    }

    @Test
    fun getByAccount_returnsNull_whenAccountDoesNotExist() = runTest {
        val result = contactDao.getByAccount(999L)
        assertEquals(null, result)
    }

    @Test
    fun getAll_returnsEmptyList_whenDatabaseIsEmpty() = runTest {
        val all = contactDao.getAll()
        assertEquals(0, all.size)
    }

    @Test
    fun insertAll_returnsAllContacts() = runTest {
        val list = listOf(
            ContactInfoEntity(
                account = 1L,
                nickname = "User1",
                category = "Cat1",
                avatar = "Avatar1"
            ),
            ContactInfoEntity(
                account = 2L,
                nickname = "User2",
                category = "Cat2",
                avatar = "Avatar2"
            )
        )
        contactDao.insertAll(list)
        val all = contactDao.getAll()
        assertEquals(2, all.size)
        assertEquals("User1", all.find { it.account == 1L }?.nickname)
        assertEquals("User2", all.find { it.account == 2L }?.nickname)
    }

    @Test
    fun getAll_sortsByCategoryThenAccount() = runTest {
        val list = listOf(
            ContactInfoEntity(
                account = 1L,
                nickname = "User1",
                category = "B_Category",
                avatar = "Avatar1"
            ),
            ContactInfoEntity(
                account = 2L,
                nickname = "User2",
                category = "A_Category",
                avatar = "Avatar2"
            ),
            ContactInfoEntity(
                account = 3L,
                nickname = "User3",
                category = "C_Category",
                avatar = "Avatar3"
            )
        )
        contactDao.insertAll(list)
        val all = contactDao.getAll()
        assertEquals(3, all.size)
        assertEquals("A_Category", all[0].category)
        assertEquals("B_Category", all[1].category)
        assertEquals("C_Category", all[2].category)
    }

    @Test
    fun getAll_sortsByAccount_whenCategoryIsSame() = runTest {
        val list = listOf(
            ContactInfoEntity(
                account = 200L,
                nickname = "User2",
                category = "Same_Cat",
                avatar = "Avatar2"
            ),
            ContactInfoEntity(
                account = 100L,
                nickname = "User1",
                category = "Same_Cat",
                avatar = "Avatar1"
            )
        )
        contactDao.insertAll(list)
        val all = contactDao.getAll()
        assertEquals(2, all.size)
        // Primary sort: category (same), Secondary sort: account ASC
        assertEquals(100L, all[0].account)
        assertEquals(200L, all[1].account)
    }

    @Test
    fun insert_onConflictReplace() = runTest {
        val contact1 = ContactInfoEntity(
            account = 123L,
            nickname = "Old Name",
            category = "Friend",
            avatar = "url1"
        )
        contactDao.insert(contact1)

        val contact2 = ContactInfoEntity(
            account = 123L,
            nickname = "New Name",
            category = "Family",
            avatar = "url2"
        )
        contactDao.insert(contact2)

        val result = contactDao.getByAccount(123L)
        assertEquals("New Name", result?.nickname)
        assertEquals("Family", result?.category)
    }
}
