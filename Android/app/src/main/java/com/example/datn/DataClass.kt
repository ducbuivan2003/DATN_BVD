package com.example.datn


data class Room(
    val topic:String="",
    val id: Int,         // ID duy nhất của phòng
    var name: String,       // Tên phòng ("Phòng khách", "Phòng ngủ")
    var devices: MutableList<Device> = mutableListOf() // Danh sách thiết bị trong phòng
)
data class Device(
    val id: Int,     // ID duy nhất của thiết bị
    var name: String,   // Tên thiết bị ("Đèn trần", "Quạt")
    val type: String,   // "light", "fan","ac","door"
    val topic: String,  // MQTT Topic dùng để gửi nhận dữ liệu
    var state: Boolean,  // (Bật/Tắt)
    var otherState:String?="",// trạng thái khác(nhiệt độ điều hoà)
    var password:String?="0000"
)


/////////////////////////////////////DATA///
data class NotiData(
    val deviceId: String,
    val notiId:String,
    val hour: String,
    val minute: String,
    val day:String,
    val month:String,
    val year:String,
    val text: String,
    val isRead: Boolean = false
)

var roomList: MutableList<Room> = mutableListOf(
//    Room(
//        topic = "home/livingroom",
//        id = 10,
//        name = "Phòng khách",
//        devices = mutableListOf(
//            Device(id = 1, name = "Đèn trần", type = "light", topic = "home/livingroom/light1", state = false),
//            Device(id = 2, name = "Quạt 1", type = "fan", topic = "home/livingroom/fan1", state = false),
//            Device(id = 3, name = "Điều hoà", type = "ac", topic = "home/livingroom/ac1", state = false)
//        )
//    ),
//    Room(
//        topic = "home/bedroom",
//        id = 1,
//        name = "Phòng ngủ",
//        devices = mutableListOf(
//        )
//    ),

)

fun getDevice(deviceId: Int): Device? {
    for (room in roomList) {
        for (device in room.devices) {
            if (device.id == deviceId) {
                return device
            }
        }
    }
    return null // Trả về null nếu không tìm thấy thiết bị
}



