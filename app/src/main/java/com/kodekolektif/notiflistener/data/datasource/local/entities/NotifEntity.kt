package com.kodekolektif.notiflistener.data.datasource.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "notif")
class NotifEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "uuid")
    val uuid: UUID,
    @ColumnInfo(name = "package_name")
    val packageName: String,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "body")
    val body: String,
    @ColumnInfo(name = "name")
    val name: String?,
    @ColumnInfo(name = "nominal")
    val nominal: Int?,
    @ColumnInfo(name = "created_at")
    val createAt: Date = Date(),
    @ColumnInfo(name = "validated_at")
    val validatedAt: Date? = null,
    @ColumnInfo(name = "status")
    val status: Int = 1
)
