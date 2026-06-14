package com.example.datn

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Room")
data class RoomEntity(
    @PrimaryKey(autoGenerate = true) var uid:Int =0,
    val name:String// tên phòng,
)


@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String?,// tên thiết bị
    val type:String,// loại thiết bị
    val roomId: Int // Liên kết với phòng
)

