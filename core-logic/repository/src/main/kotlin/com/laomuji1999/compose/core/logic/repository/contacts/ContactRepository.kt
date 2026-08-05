package com.laomuji1999.compose.core.logic.repository.contacts

import com.laomuji1999.compose.core.logic.database.dao.ContactDao
import com.laomuji1999.compose.core.logic.model.entity.ContactInfoEntity
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepository @Inject constructor(
    val contactDao: ContactDao
) {
    fun contactsList() = flow {
        //先使用缓存的列表
        val cacheContact = contactDao.getAll()
        emit(cacheContact)

        val contacts = (fakeRequestContacts() + fakeRandomContacts()).sortedWith(
            compareBy<ContactInfoEntity> { it.category }.thenBy { it.account }
        )
        contactDao.insertAll(contacts)
        emit(contacts)
    }.catch {
        it.printStackTrace()
        emit(ArrayList())
    }

    private fun fakeRequestContacts(): List<ContactInfoEntity> {
        return listOf(
            ContactInfoEntity(
                account = 10000,
                avatar = "content://compose.laomuji1999.contact.assert/avatar/avatar_ragdoll_cat.png",
                category = "*",
                nickname = "Ragdoll cat"
            ), ContactInfoEntity(
                account = 10001,
                avatar = "content://compose.laomuji1999.contact.assert/avatar/avatar_golden_dog.png",
                category = "*",
                nickname = "Golden retriever"
            ), ContactInfoEntity(
                account = 10002,
                avatar = "content://compose.laomuji1999.contact.assert/avatar/avatar_parrot.png",
                category = "*",
                nickname = "Parrot"
            ), ContactInfoEntity(
                account = 10003,
                avatar = "content://compose.laomuji1999.contact.assert/avatar/avatar_siberian_husky.png",
                category = "*",
                nickname = "Siberian Husky"
            ), ContactInfoEntity(
                account = 10004,
                avatar = "content://compose.laomuji1999.contact.assert/avatar/avatar_british_shorthair.png",
                category = "*",
                nickname = "British Shorthair"
            ), ContactInfoEntity(
                account = 10005,
                avatar = "content://compose.laomuji1999.contact.assert/avatar/avatar_sheep.png",
                category = "*",
                nickname = "Sheep"
            ), ContactInfoEntity(
                account = 10006,
                avatar = "content://compose.laomuji1999.contact.assert/avatar/avatar_tiger.png",
                category = "*",
                nickname = "Tiger"
            ), ContactInfoEntity(
                account = 10007,
                avatar = "content://compose.laomuji1999.contact.assert/avatar/avatar_mouse.png",
                category = "*",
                nickname = "Mouse"
            ), ContactInfoEntity(
                account = 10008,
                avatar = "content://compose.laomuji1999.contact.assert/avatar/avatar_giraffe.png",
                category = "*",
                nickname = "Giraffe"
            ), ContactInfoEntity(
                account = 1009,
                avatar = "content://compose.laomuji1999.contact.assert/avatar/avatar_eagle.png",
                category = "*",
                nickname = "Eagle"
            ), ContactInfoEntity(
                account = 10010,
                avatar = "content://compose.laomuji1999.contact.assert/avatar/avatar_wolf.png",
                category = "*",
                nickname = "Wolf"
            ), ContactInfoEntity(
                account = 10011,
                avatar = "content://compose.laomuji1999.contact.assert/avatar/avatar_leopard.png",
                category = "*",
                nickname = "Leopard"
            ), ContactInfoEntity(
                account = 10012,
                avatar = "content://compose.laomuji1999.contact.assert/avatar/avatar_bee.png",
                category = "*",
                nickname = "Bee"
            ), ContactInfoEntity(
                account = 10013,
                avatar = "content://compose.laomuji1999.contact.assert/avatar/avatar_butterfly.png",
                category = "*",
                nickname = "Butterfly"
            ), ContactInfoEntity(
                account = 10014,
                avatar = "content://compose.laomuji1999.contact.assert/avatar/avatar_mantis.png",
                category = "*",
                nickname = "Mantis"
            )
        )
    }

    private fun fakeRandomContacts(): List<ContactInfoEntity> {
        val result = mutableListOf<ContactInfoEntity>()
        for (i in 1..999) {
            val category = randomCategory()
            val name = category + randomName()
            result.add(
                ContactInfoEntity(
                    account = (20000 + i).toLong(),
                    avatar = "https://picsum.photos/seed/lmj$i/200.webp",
                    category = category,
                    nickname = name
                )
            )
        }
        return result
    }

    private fun randomCategory(): String {
        return ('A'..'Z').random().toString()
    }

    private fun randomName(): String {
        return randomCategory() + randomCategory() + randomCategory()
    }
}