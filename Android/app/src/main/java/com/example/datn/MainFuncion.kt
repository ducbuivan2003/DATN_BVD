package com.example.datn

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import com.example.datn.GlobalData.URLFB
import com.example.datn.GlobalData.activeRoom
import com.example.datn.GlobalData.activeTaskbar
import com.example.datn.GlobalData.controlAriconditioner
import com.example.datn.GlobalData.edittingDevice
import com.example.datn.GlobalData.edittingRoom
import com.example.datn.GlobalData.haveNotis
import com.example.datn.GlobalData.newestDevice
import com.example.datn.GlobalData.newestRoom
import com.example.datn.GlobalData.recentUpdate
import com.example.datn.GlobalData.userId
import com.google.firebase.database.FirebaseDatabase

fun Log_data(){
    Log.d("L - activeTaskbar", GlobalData.activeTaskbar.value)
    Log.d("L - activeRoom", GlobalData.activeRoom.value.toString())
    Log.d("L - haveNotis", GlobalData.haveNotis.value.toString())
    Log.d("L - controlAriconditioner", GlobalData.controlAriconditioner.value.toString())
    Log.d("L - edittingRoom", GlobalData.edittingRoom.value.toString())
    Log.d("L - edittingDevice", GlobalData.edittingDevice.value.toString())
}

fun Data_reset(){
    controlAriconditioner.value = -1
    GlobalData.activeRoom.value = -1
    edittingRoom.value=-1
    edittingDevice.value=-1
    activeRoom.value=-1
    //GlobalData.haveNotis.value = true
}

fun Back_to_home_screen(){
    GlobalData.activeTaskbar.value = "home"
    Data_reset()
    //GlobalData.haveNotis.value = true
}

fun Back_to_noti_screen(){
    GlobalData.activeTaskbar.value = "noti"
    Data_reset()

    //GlobalData.haveNotis.value = false
}

fun Back_to_setting_screen(){
    GlobalData.activeTaskbar.value = "setting"
    Data_reset()

    //GlobalData.haveNotis.value = true
}
fun Go_to_device_list(idRoom:Int){
    Data_reset()
    activeRoom.value=idRoom
    activeTaskbar.value = "devicelist"
    Log_data()
}

fun Go_to_AirConditioner_control_screen(id:Int) {//UI
    val temp = activeRoom.value
    Data_reset()
    activeRoom.value = temp
    activeTaskbar.value = "airconditioner"
    controlAriconditioner.value = id
    Log_data()
}

fun Go_to_door_config(id: Int){
    val temp = activeRoom.value
    Data_reset()
    activeRoom.value = temp
    activeTaskbar.value = "door"
    controlAriconditioner.value = id
    Log_data()
}

fun Turn_on(id: Int){
    Log.d("On", "On")
    Switch_state_power(id)
}


fun Turn_off(id: Int){
    Log.d("Off", "Off")
    Switch_state_power(id)

}

fun Change_temperature(int: Int, action:String){//up down left right on off
    Log.d("ChangeTemperature", "ChangeTemperature")
    if(action=="up") {
        Control_data(int, "up")
    }else if(action=="down"){
        Control_data(int, "down")
    }else if(action == "right"){
        Control_data(int, "right")

    }else if(action == "left"){
        Control_data(int, "left")

    }
    else if(action == "on"){
        Switch_state_power(int)
    }else if (action=="off"){
        Switch_state_power(int)
    }
}

fun change_password(deviceId: Int, pass: String){
    val device = get_device(deviceId)


    if (device != null) {
        //
        var  r="room"
        var  d="device"
        for (room in roomList) {
            for (device in room.devices) {
                if (device.id == deviceId) {
                    r =  room.id.toString()
                    d = device.id.toString()
                }
            }
        }
        device.password=pass
        val database = FirebaseDatabase.getInstance(URLFB.value)
            .reference.child(userId.value).child(r).child(d).child("password")
        database.setValue(pass).addOnSuccessListener {
            println("Gửi dữ liệu thành công!")
        }.addOnFailureListener {
            println("Lỗi khi gửi dữ liệu: ${it.message}")
        }


    } else {
        Log.d("DeviceState", "Không tìm thấy thiết bị với ID: $deviceId")
    }
}

fun Delete_device(id: Int){
    Log.d("DeleteDevice", "DeleteDevice")
    activeTaskbar.value = "devicelist"
    delete_device_FB( activeRoom.value, id)
    roomList.find { it.id == activeRoom.value }?.devices?.removeIf { it.id == id }
}


fun Delete_room(id: Int){
    Log.d("DeleteRoom", "DeleteRoom")
    Data_reset()

    activeTaskbar= mutableStateOf("home")
    delete_room_FB(id)
    roomList.removeIf { it.id == id }
}


fun Add_device(){//UI
    Log.d("AddDevice", "AddDevice")
    activeTaskbar.value="adddevice"
}
fun Save_device(name: String, id: Int, topic: String, room_id: Int, type: String="Fan"){// chưa xong

    if(activeTaskbar.value=="adddevice")   create_new_device(name, id, topic, room_id, type)
    if(activeTaskbar.value=="editdevice") save_edited_device(name, id, topic, room_id, type)
    ///////////////
    Go_to_device_list(activeRoom.value)
    ////
}
fun UpdateDevice(deviceId:Int){// nút save cho update
    Go_to_device_list(activeRoom.value)
}

fun Add_room(){// xử lý nút bấm + thôi
    Log.d("AddRoom", "AddRoom")
    Data_reset()
    activeTaskbar.value="addroom"
}
fun Save_room(name:String, id:Int){// chưa xong

    if(activeTaskbar.value=="addroom")   create_new_room(name, id)
    if(activeTaskbar.value=="editroom") save_edited_room(name, id)
    Back_to_home_screen()
    //xử lý data
}

/// xử lý chính cho add room và add device
fun create_new_room(name:String, id:Int){
    val nr="name: "+name+" id: "+id.toString()
    Log.d("create Room", nr)
    //xử lý data
    val topic =id.toString()
    val room = Room(topic =topic,id = id, name = name, devices = mutableListOf())
    roomList.add(room)
    newestRoom.value=id.toString()
    recentUpdate.value=true
    create_new_room_FB(id, name)
    Log.d("roomList", room.toString())
}
fun create_new_device(name: String, id: Int, topic: String, room_id: Int, type: String="fan"){
    val nr="name: "+name+" id: "+id.toString() + "roomId:"+ room_id.toString()+"type: "+type
    Log.d("create Room", nr)
    val topic =room_id.toString()+"/"+id.toString()
    val device = Device(id = id, name = name, type = type, topic = topic, state = false)
    val room = roomList.find { it.id == room_id }
    room?.devices?.add(device)
    newestDevice.value=id.toString()
    recentUpdate.value=true
    create_new_device_FB(room_id, id, name, type)
}

//xử lý chính cho edit room và device
 fun save_edited_room(name:String, id:Int){
     val nr="name: "+name+" id: "+id.toString()
    create_new_room_FB(id, name)
    Log.d("edited Room", nr)

 }
 fun save_edited_device(name: String, id: Int, topic: String, room_id: Int, type: String="fan"){
     val nr="name: "+name+" id: "+id.toString() + "roomId:"+ room_id.toString()+"type: "+type
     Log.d("edited Room", nr)
     create_new_device_FB(room_id, id, name, type)

 }


///////////////////////////////////////////////////



fun Edit(idDevice:Int){//deit device
    Log.d("Edit", "Edit")
    val temp= activeRoom.value
    Log_data()
    Data_reset()
    activeRoom.value=temp
    activeTaskbar.value="editdevice"
    edittingDevice.value=idDevice
}

fun Edit_room(id:Int){
    Log.d("ấn nút chỉnh sửa phòng", "ấn nút chỉnh sửa phòng ${id}")
    Data_reset()
    activeTaskbar.value="editroom"
    edittingRoom.value=id



}

fun Delete_noti(){
    Log.d("DeleteNoti", "DeleteNoti")
    delete_allNotifications_fromDTB()
    Clear_noti_list()
    haveNotis.value= !is_noti_empty()
}

//////TEST

fun Test() {
    Log.d("Test", "Test")
    //login_User("ducbuivan2003@gmail.com", "ducbuivan372003")
   // roomList.add(Room(id=1234, name="Phong test", devices=mutableListOf()))
    get_device_update()
    haveNotis.value= !is_noti_empty()
   // sendNotification(MainActivity.appContext, "Thông báo mới", "Nội dung thông báo!")

}

fun Switch_state_power(deviceId: Int) {
    Log.d("SwitchState", "${deviceId.toString()}")
    // Lấy thiết bị 1 lần
    val device = get_device(deviceId)

    if (device != null) {
        device.state = !device.state  // Đảo trạng thái của thiết bị
        Log.d("DeviceState", "Device ID: $deviceId, State: ${device.state}")

        if (device.state) {
            send_data_to_FB("on", deviceId)
        } else {
            send_data_to_FB("off", deviceId)
        }
    } else {
        Log.d("DeviceState", "Không tìm thấy thiết bị với ID: $deviceId")
    }
}


var count_=0
fun Control_data(deviceId: Int, action: String){
    Log.d("Control Action", "${deviceId.toString()}")
    // Lấy thiết bị 1 lần
    val device = get_device(deviceId)


    if(action=="up" || action=="down"||action=="left"|| action=="right"){count_=count_+1}
    if (device != null) {
       //
        var  r="room"
        var  d="device"
        for (room in roomList) {
            for (device in room.devices) {
                if (device.id == deviceId) {
                    r =  room.id.toString()
                    d = device.id.toString()
                }
            }
        }

        val database = FirebaseDatabase.getInstance(URLFB.value)
            .reference.child(userId.value).child(r).child(d).child("action")
        database.setValue(action).addOnSuccessListener {
            println("Gửi dữ liệu thành công!")
        }.addOnFailureListener {
            println("Lỗi khi gửi dữ liệu: ${it.message}")
        }


        val database2 = FirebaseDatabase.getInstance(URLFB.value)
            .reference.child(userId.value).child(r).child(d).child("count")
        database2.setValue(count_.toString()).addOnSuccessListener {
            println("Gửi dữ liệu thành công!")
        }.addOnFailureListener {
            println("Lỗi khi gửi dữ liệu: ${it.message}")
        }

    } else {
        Log.d("DeviceState", "Không tìm thấy thiết bị với ID: $deviceId")
    }
}

fun create_random_ID(length: Int, type: String = "room"): Int {
    val allowedChars = ('0'..'9')

    while (true) { // Lặp cho đến khi tìm được ID hợp lệ
        val rt = (1..length)
            .map { allowedChars.random() }
            .joinToString("")
            .toInt()

        // Kiểm tra ID có trùng không
        val existingIds = if (type == "room") roomList.map { it.id } else if (type == "device") roomList.flatMap { it.devices }.map { it.id } else emptyList() // Nếu có thêm loại khác, cần thay đổi điều kiện này
        if (rt !in existingIds) {
            return rt
        }
    }
}




fun show_toast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
 fun set_room_name_ID(id: Int, nn:String){ //dat ten phong khi biet ID
     // Tìm phòng có id tương ứng và sửa tên
     for (room in roomList) {
         if (room.id == id) {
             room.name = nn // Cập nhật tên của phòng
             Log.d("Update", "Tên phòng ${id} đã được cập nhật thành $nn")
             return
         }
     }
     Log.d("Update", "Không tìm thấy phòng với ID $id")
 }
fun get_room_name(id:Int):String{
    for(room in roomList){
        if(room.id==id){
            return room.name
        }
    }
    return "null"
}
