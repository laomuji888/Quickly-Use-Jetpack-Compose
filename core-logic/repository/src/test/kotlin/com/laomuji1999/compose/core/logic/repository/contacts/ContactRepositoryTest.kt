package com.laomuji1999.compose.core.logic.repository.contacts

import app.cash.turbine.test
import com.laomuji1999.compose.core.logic.database.dao.ContactDao
import com.laomuji1999.compose.core.logic.model.entity.ContactInfoEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ContactRepositoryTest {

    private val contactDao: ContactDao = mockk()
    private lateinit var repository: ContactRepository

    @Before
    fun setup() {
        repository = ContactRepository(contactDao)
    }

    @Test
    fun `contactsList should emit cached contacts then new contacts`() = runTest {
        val cached = listOf(
            ContactInfoEntity(1L, "Cached", "Cat", "url")
        )
        coEvery { contactDao.getAll() } returns cached
        coEvery { contactDao.insertAll(any()) } returns Unit

        repository.contactsList().test {
            // First emission: cached data
            val firstItem = awaitItem()
            assertEquals(cached, firstItem)

            // Second emission: new data (fakeRequestContacts + fakeRandomContacts)
            val secondItem = awaitItem()
            val expectedFakeSize = 15 // Based on fakeRequestContacts() implementation
            val expectedRandomSize = 999 // Based on fakeRandomContacts() implementation
            assertEquals(expectedFakeSize + expectedRandomSize, secondItem.size)
            assertTrue(secondItem.any { it.nickname == "Ragdoll cat" })
            
            coVerify { contactDao.insertAll(any()) }
            awaitComplete()
        }
    }

    @Test
    fun `contactsList should emit empty list when dao throws exception`() = runTest {
        coEvery { contactDao.getAll() } throws Exception("DB Error")

        repository.contactsList().test {
            val item = awaitItem()
            assertTrue(item.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `contactsList should emit sorted contacts`() = runTest {
        coEvery { contactDao.getAll() } returns emptyList()
        coEvery { contactDao.insertAll(any()) } returns Unit

        repository.contactsList().test {
            awaitItem() // Empty cache
            val secondItem = awaitItem()
            
            // Verify sorting: category ASC, then account ASC
            for (i in 0 until secondItem.size - 1) {
                val current = secondItem[i]
                val next = secondItem[i+1]
                if (current.category == next.category) {
                    assertTrue("Account should be sorted ASC when category is same", current.account <= next.account)
                } else {
                    assertTrue("Category should be sorted ASC", current.category < next.category)
                }
            }
            awaitComplete()
        }
    }
}
