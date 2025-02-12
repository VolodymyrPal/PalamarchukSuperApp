package com.hfad.palamarchuksuperapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hfad.palamarchuksuperapp.data.entities.MessageGroupEntity
import com.hfad.palamarchuksuperapp.data.entities.MessageGroupWithMessagesEntity

@Dao
interface MessageGroupDao {
    // region Операции с группами сообщений

    /**
     * Создание новой группы сообщений.
     * @param messageGroup Сущность группы
     * @return ID созданной группы
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessageGroup(messageGroup: MessageGroupEntity): Long

    /**
     * Обновление группы сообщений.
     * @param messageGroup Обновленная сущность группы
     */
    @Update
    suspend fun updateMessageGroup(messageGroup: MessageGroupEntity)

    /**
     * Удаление группы сообщений по ID.
     * @param groupId ID группы для удаления
     */
    @Query("DELETE FROM MessageGroup WHERE id = :groupId")
    suspend fun deleteMessageGroup(groupId: Int)

    /**
     * Получение группы сообщений по ID со всеми сообщениями.
     * @param groupId ID группы
     * @return Группа с связанными сообщениями
     */
    @Query("SELECT * FROM MessageGroup WHERE id = :groupId")
    suspend fun getMessageGroup(groupId: Int): MessageGroupWithMessagesEntity

}