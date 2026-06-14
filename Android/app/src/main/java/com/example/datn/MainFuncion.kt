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

fun Logdata(){
    Log.d("L - activeTaskbar", GlobalData.activeTaskbar.value)
    Log.d("L - activeRoom", GlobalData.activeRoom.value.toString())
    Log.d("L - haveNotis", GlobalData.haveNotis.value.toString())
    Log.d("L - controlAriconditioner", GlobalData.controlAriconditioner.value.toString())
    Log.d("L - edittingRoom", GlobalData.edittingRoom.value.toString())
    Log.d("L - edittingDevice", GlobalData.edittingDevice.value.toString())
}

fun DataReset(){
    controlAriconditioner.value = -1
    GlobalData.activeRoom.value = -1
    edittingRoom.value=-1
    edittingDevice.value=-1
    activeRoom.value=-1
    //GlobalData.haveNotis.value = true
}

fun BackToHomeScreen(){
    GlobalData.activeTaskbar.value = "home"
    DataReset()
    //GlobalData.haveNotis.value = true
}

fun BackToNotiScreen(){
    GlobalData.activeTaskbar.value = "noti"
    DataReset()

    //GlobalData.haveNotis.value = false
}

fun BackToSettingScreen(){
    GlobalData.activeTaskbar.value = "setting"
    DataReset()

    //GlobalData.haveNotis.value = true
}
fun GoToDeviceList(idRoom:Int){
    DataReset()
    activeRoom.value=idRoom
    activeTaskbar.value = "devicelist"
    Logdata()
}

fun GoToAirConditionerControlScreen(id:Int) {//UI
    val temp = activeRoom.value
    DataReset()
    activeRoom.value = temp
    activeTaskbar.value = "airconditioner"
    controlAriconditioner.value = id
    Logdata()
}

fun GoToDoorConfig(id: Int){
    val temp = activeRoom.value
    DataReset()
    activeRoom.value = temp
    activeTaskbar.value = "door"
    controlAriconditioner.value = id
    Logdata()
}

fun TurnOn(id: Int){
    Log.d("On", "On")
    SwitchStatePower(id)
}


fun TurnOff(id: Int){
    Log.d("Off", "Off")
    SwitchStatePower(id)

}

fun ChangeTemperature(int: Int, action:String){//up down left right on off
    Log.d("ChangeTemperature", "ChangeTemperature")
    if(action=="up") {
        ControlData(int, "up")
    }else if(action=="down"){
        ControlData(int, "down")
    }else if(action == "right"){
        ControlData(int, "right")

    }else if(action == "left"){
        ControlData(int, "left")

    }
    else if(action == "on"){
        SwitchStatePower(int)
    }else if (action=="off"){
        SwitchStatePower(int)
    }
}

fun changePassWord(deviceId: Int, pass: String){
    val device = getDevice(deviceId)


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

fun DeleteDevice(id: Int){
    Log.d("DeleteDevice", "DeleteDevice")
    activeTaskbar.value = "devicelist"
    deleteDeviceFB( activeRoom.value, id)
    roomList.find { it.id == activeRoom.value }?.devices?.removeIf { it.id == id }
}


fun DeleteRoom(id: Int){
    Log.d("DeleteRoom", "DeleteRoom")
    DataReset()

    activeTaskbar= mutableStateOf("home")
    deleteRoomFB(id)
    roomList.removeIf { it.id == id }
}


fun AddDevice(){//UI
    Log.d("AddDevice", "AddDevice")
    activeTaskbar.value="adddevice"
}
fun SaveDevice(name: String, id: Int, topic: String, room_id: Int, type: String="Fan"){// chưa xong

    if(activeTaskbar.value=="adddevice")   createNewDevice(name, id, topic, room_id, type)
    if(activeTaskbar.value=="editdevice") saveEditedDevice(name, id, topic, room_id, type)
    ///////////////
    GoToDeviceList(activeRoom.value)
    ////
}
fun UpdateDevice(deviceId:Int){// nút save cho update
    GoToDeviceList(activeRoom.value)
}

fun AddRoom(){// xử lý nút bấm + thôi
    Log.d("AddRoom", "AddRoom")
    DataReset()
    activeTaskbar.value="addroom"
}
fun SaveRoom(name:String, id:Int){// chưa xong

    if(activeTaskbar.value=="addroom")   createNewRoom(name, id)
    if(activeTaskbar.value=="editroom") saveEditedRoom(name, id)
    BackToHomeScreen()
    //xử lý data
}

/// xử lý chính cho add room và add device
fun createNewRoom(name:String, id:Int){
    val nr="name: "+name+" id: "+id.toString()
    Log.d("create Room", nr)
    //xử lý data
    val topic =id.toString()
    val room = Room(topic =topic,id = id, name = name, devices = mutableListOf())
    roomList.add(room)
    newestRoom.value=id.toString()
    recentUpdate.value=true
    createNewRoomFB(id, name)
    Log.d("roomList", room.toString())
}
fun createNewDevice(name: String, id: Int, topic: String, room_id: Int, type: String="fan"){
    val nr="name: "+name+" id: "+id.toString() + "roomId:"+ room_id.toString()+"type: "+type
    Log.d("create Room", nr)
    val topic =room_id.toString()+"/"+id.toString()
    val device = Device(id = id, name = name, type = type, topic = topic, state = false)
    val room = roomList.find { it.id == room_id }
    room?.devices?.add(device)
    newestDevice.value=id.toString()
    recentUpdate.value=true
    createNewDeviceFB(room_id, id, name, type)
}

//xử lý chính cho edit room và device
 fun saveEditedRoom(name:String, id:Int){
     val nr="name: "+name+" id: "+id.toString()
    createNewRoomFB(id, name)
    Log.d("edited Room", nr)

 }
 fun saveEditedDevice(name: String, id: Int, topic: String, room_id: Int, type: String="fan"){
     val nr="name: "+name+" id: "+id.toString() + "roomId:"+ room_id.toString()+"type: "+type
     Log.d("edited Room", nr)
     createNewDeviceFB(room_id, id, name, type)

 }


///////////////////////////////////////////////////



fun Edit(idDevice:Int){//deit device
    Log.d("Edit", "Edit")
    val temp= activeRoom.value
    Logdata()
    DataReset()
    activeRoom.value=temp
    activeTaskbar.value="editdevice"
    edittingDevice.value=idDevice
}

fun EditRoom(id:Int){
    Log.d("ấn nút chỉnh sửa phòng", "ấn nút chỉnh sửa phòng ${id}")
    DataReset()
    activeTaskbar.value="editroom"
    edittingRoom.value=id



}

fun DeleteNoti(){
    Log.d("DeleteNoti", "DeleteNoti")
    deleteAllNotificationsFromDTB()
    ClearNotiList()
    haveNotis.value= !isNotiEmpty()
}

//////TEST

fun Test() {
    Log.d("Test", "Test")
    //loginUser("ducbuivan2003@gmail.com", "0347495054")
   // roomList.add(Room(id=1234, name="Phong test", devices=mutableListOf()))
    getDeviceUpdate()
    haveNotis.value= !isNotiEmpty()
   // sendNotification(MainActivity.appContext, "Thông báo mới", "Nội dung thông báo!")

}

fun SwitchStatePower(deviceId: Int) {
    Log.d("SwitchState", "${deviceId.toString()}")
    // Lấy thiết bị 1 lần
    val device = getDevice(deviceId)

    if (device != null) {
        device.state = !device.state  // Đảo trạng thái của thiết bị
        Log.d("DeviceState", "Device ID: $deviceId, State: ${device.state}")

        if (device.state) {
            sendDataToFirebase("on", deviceId)
        } else {
            sendDataToFirebase("off", deviceId)
        }
    } else {
        Log.d("DeviceState", "Không tìm thấy thiết bị với ID: $deviceId")
    }
}


var count_=0
fun ControlData(deviceId: Int, action: String){
    Log.d("Control Action", "${deviceId.toString()}")
    // Lấy thiết bị 1 lần
    val device = getDevice(deviceId)


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

fun createRandomID(length: Int, type: String = "room"): Int {
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




fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
 fun setRoomNameWhenKnowID(id: Int, nn:String){
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
fun getRoomName(id:Int):String{
    for(room in roomList){
        if(room.id==id){
            return room.name
        }
    }
    return "null"
}