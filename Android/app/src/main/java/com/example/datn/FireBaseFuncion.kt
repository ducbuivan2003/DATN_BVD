package com.example.datn
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.datn.GlobalData.URLFB
import com.example.datn.GlobalData.newestDevice
import com.example.datn.GlobalData.newestRoom
import com.example.datn.GlobalData.refreshDevicePage
import com.example.datn.GlobalData.refreshHomePage
import com.example.datn.GlobalData.userId
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

//    val userId = getCurrentUserId() ?: "default_user"
fun send_data_to_FB(status: String, deviceId:Int) {
    var  r="room"
    var  d="device"
    for (room in roomList){
        for (device in room.devices) {
            if(device.id==deviceId){
                r=room.id.toString()
                d=device.id.toString()
            }
        }
    }
    val database = FirebaseDatabase.getInstance(URLFB.value)
        .reference.child(userId.value).child(r).child(d).child("status")

    database.setValue(status).addOnSuccessListener {
        println("Gửi dữ liệu thành công!")
    }.addOnFailureListener {
        println("Lỗi khi gửi dữ liệu: ${it.message}")
    }
}



fun listen_to_FB(deviceId: Int, str:String = "status"): Flow<String> = callbackFlow {//Flow + callbackFlow
    var roomPath: String? = null
    var devicePath: String? = null

    // Tìm đường dẫn thiết bị
    for (room in roomList) {
        for (device in room.devices) {
            if (device.id == deviceId) {
                roomPath = "${room.id}"
                devicePath = "${device.id}"
                break
            }
        }
    }

    // Nếu không tìm thấy thiết bị
    if (roomPath == null || devicePath == null) {
        trySend("Không tìm thấy thiết bị!").isSuccess
        close()
        return@callbackFlow
    }

    // Kết nối Firebase
    val database = FirebaseDatabase.getInstance()
        .reference.child(userId.value).child(roomPath).child(devicePath).child(str)

    val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val status = snapshot.getValue(String::class.java) ?: "Lỗi dữ liệu!"
            trySend(status).isSuccess
        }

        override fun onCancelled(error: DatabaseError) {
            close(error.toException())
        }
    }

    database.addValueEventListener(listener)

    // Hủy listener khi không còn cần thiết
    awaitClose { database.removeEventListener(listener) }
}



fun sendDataToFirebase2(path: String, message: String) {
    val database = FirebaseDatabase.getInstance(URLFB.value).reference.child(path)

    database.setValue(message).addOnSuccessListener {
        println("Gửi dữ liệu thành công đến $path!")
    }.addOnFailureListener {
        println("Lỗi khi gửi dữ liệu đến $path: ${it.message}")
    }
}



fun listen_to_FB2(path: String): Flow<String> = callbackFlow {
    val database = FirebaseDatabase.getInstance().reference.child(path)

    val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val data = snapshot.getValue(String::class.java) ?: "Lỗi dữ liệu!"
            trySend(data).isSuccess
        }

        override fun onCancelled(error: DatabaseError) {
            close(error.toException())
        }
    }

    database.addValueEventListener(listener)

    awaitClose { database.removeEventListener(listener) }

    /* cách gọi
    val statusFlow = listenToFirebase("room1/device2/status")
statusFlow.collect { status ->
    println("Trạng thái thiết bị: $status")
}

     */
}


fun Create_user_topic(){
    if(userId.value!="default_user") {
        userId.value = is_session_active() ?: "default_user"
    }
    val database = FirebaseDatabase.getInstance(URLFB.value)
        .reference.child(userId.value).child("main").child("hello world")

    database.setValue(get_active_user_email()).addOnSuccessListener {
        println("Tạo topic thành công!")
    }.addOnFailureListener {
        println("Lỗi khi tạo topic: ${it.message}")
    }
}

fun create_new_room_FB(id:Int, name:String){
    val userId = is_session_active() ?: "default_user"
    val database = FirebaseDatabase.getInstance(URLFB.value)
        .reference.child(userId).child(id.toString()).child("roomname")
    val database2 = FirebaseDatabase.getInstance(URLFB.value)
        .reference.child(userId).child("main").child("newestRoom")
    database.setValue(name).addOnSuccessListener {
        println("Tạo topic thành công!")
    }.addOnFailureListener {
        println("Lỗi khi tạo topic: ${it.message}")
    }
    database2.setValue(id.toString()).addOnSuccessListener {
        println("Tạo topic thành công!")
    }.addOnFailureListener {
        println("Lỗi khi tạo topic: ${it.message}")
    }
}

fun create_new_device_FB(rid:Int, id:Int, name:String, type:String){
    if(userId.value=="default_user") {
         userId.value = is_session_active() ?: "default_user"
    }
    val database = FirebaseDatabase.getInstance(URLFB.value)
        .reference.child(userId.value).child(rid.toString()).child(id.toString()).child("devicename")
    val database2 = FirebaseDatabase.getInstance(URLFB.value)
        .reference.child(userId.value).child(rid.toString()).child(id.toString()).child("devicetype")
    val database3= FirebaseDatabase.getInstance(URLFB.value)
        .reference.child(userId.value).child("main").child("newestDevice")
    database.setValue(name).addOnSuccessListener {
        println("Tạo topic thành công!")
    }.addOnFailureListener {
        println("Lỗi khi tạo topic: ${it.message}")
    }
    database2.setValue(type).addOnSuccessListener {
        println("Tạo topic thành công!")
    }.addOnFailureListener {
        println("Lỗi khi tạo topic: ${it.message}")
    }
    database3.setValue(id.toString()).addOnSuccessListener {
        println("Tạo topic thành công!")
    }.addOnFailureListener {
        println("Lỗi khi tạo topic: ${it.message}")
    }
}


fun delete_room_FB(id:Int){
    val database = FirebaseDatabase.getInstance().reference

// Xóa toàn bộ nhánh "room1"
    val roomRef = database.child(userId.value).child(id.toString())
    val database2 =FirebaseDatabase.getInstance().reference.child(userId.value).child("main").child("newestRoom")

    database2.setValue("-"+id.toString()).addOnSuccessListener {
        println("Tạo topic thành công!")
    }.addOnFailureListener {
        println("Lỗi khi tạo topic: ${it.message}")
    }

    roomRef.removeValue()
        .addOnSuccessListener {
            Log.d("Firebase", "Room successfully deleted")
        }
        .addOnFailureListener { exception ->
            Log.e("Firebase", "Failed to delete room", exception)
        }

}


fun delete_device_FB(rid:Int, id:Int){
    val database = FirebaseDatabase.getInstance().reference
    val database2 =FirebaseDatabase.getInstance().reference.child(userId.value).child("main").child("newestDevice")
// Xóa toàn bộ nhánh "room1"
    val roomRef = database.child(userId.value).child(rid.toString()).child(id.toString())

    database2.setValue("-"+id.toString()).addOnSuccessListener {
        println("Tạo topic thành công!")
    }.addOnFailureListener {
        println("Lỗi khi tạo topic: ${it.message}")
    }

    roomRef.removeValue()
        .addOnSuccessListener {
            Log.d("Firebase", "Room successfully deleted")
        }
        .addOnFailureListener { exception ->
            Log.e("Firebase", "Failed to delete room", exception)
        }

}


fun get_device_update() {
    var userId = mutableStateOf("default_user")
    userId.value = is_session_active() ?: "default_user" // Cập nhật userId nếu có
    // Sử dụng CoroutineScope để chạy bất đồng bộ
    CoroutineScope(Dispatchers.IO).launch {
        listen_to_FB2("${userId.value}/main/newestDevice").collect { status ->

            if (status != newestDevice.value) {
                newestDevice.value = status

                fetch_room() // Cập nhật lại phòng nếu có thay đổi
                Log.d("fetched newestDevice", status)

            }
            Log.d("status", status)
        }

    }
}

fun get_room_update(){
    var userId = mutableStateOf("default_user")
    userId.value = is_session_active() ?: "default_user" // Cập nhật userId nếu có
    // Sử dụng CoroutineScope để chạy bất đồng bộ
    CoroutineScope(Dispatchers.IO).launch {


        listen_to_FB2("${userId.value}/main/newestRoom").collect { status ->
            if (status != newestRoom.value) {
                newestRoom.value = status
                fetch_room() // Cập nhật lại phòng nếu có thay đổi
                Log.d("fetched newestRoom", status)


            }
            Log.d("status", status)
        }
    }
}

/////////////
fun fetchRoom0() {
    if (userId.value == "default_user") {
        userId.value = is_session_active() ?: "default_user"
    }

    val database = FirebaseDatabase.getInstance().reference.child(userId.value)
    database.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            // Duyệt qua tất cả các userId
            for (userSnapshot in snapshot.children) {
                val userId = userSnapshot.key ?: continue

                // Duyệt qua các roomId của user
                for (roomSnapshot in userSnapshot.children) {
                    val roomId = roomSnapshot.key ?: continue

                    // Lấy thông tin roomname của room
                    val roomName = roomSnapshot.child("roomname").getValue(String::class.java) ?: continue

                    // In thông tin roomname
                    println("Room ID: $roomId, Room Name: $roomName")
                    val topic=roomId.toString()
                    roomList.add(Room(topic =topic,id = roomId.toInt(), name = roomName, devices = mutableListOf()))
                    // Kiểm tra nếu trong roomId có device
                    for (deviceSnapshot in roomSnapshot.children) {
                        val deviceId = deviceSnapshot.key ?: continue

                        // Nếu là thông tin về device
                        if (deviceSnapshot.hasChild("devicename")) {
                            val deviceName = deviceSnapshot.child("devicename").getValue(String::class.java) ?: continue
                            val deviceType = deviceSnapshot.child("devicetype").getValue(String::class.java) ?: continue
                            val deviceStatus = deviceSnapshot.child("status").getValue(String::class.java) ?: continue
                            val deviceAction = deviceSnapshot.child("action").getValue(String::class.java)
                            val deviceCount = deviceSnapshot.child("count").getValue(String::class.java)

                            // Lấy roomname của device
                            val deviceRoomName = deviceSnapshot.child("roomname").getValue(String::class.java) ?: continue

                            // In thông tin của device và roomname
                            println("Device ID: $deviceId, Device Name: $deviceName, Device Type: $deviceType, Status: $deviceStatus, Action: $deviceAction, Count: $deviceCount, Device Room Name: $deviceRoomName")
                            roomList.find { it.id == roomId.toInt() }?.devices?.add(Device(id = deviceId.toInt(), name = deviceName, type = deviceType, state = deviceStatus.toBoolean(), topic = topic+"/"+deviceId.toString()))
                        }
                    }

                    ////add
                }

                // Kiểm tra thông tin trong phần 'main' của user
                val mainSnapshot = userSnapshot.child("main")
                if (mainSnapshot.exists()) {
                    val helloWorld = mainSnapshot.child("hello world").getValue(String::class.java) ?: continue
                    val newestDevice = mainSnapshot.child("newestDevice").getValue(Int::class.java) ?: continue
                    val newestNoti = mainSnapshot.child("newestNoti").getValue(Int::class.java) ?: continue
                    val newestRoom = mainSnapshot.child("newestRoom").getValue(Int::class.java) ?: continue

                    // In thông tin 'main'
                    println("Hello World: $helloWorld, Newest Device: $newestDevice, Newest Notification: $newestNoti, Newest Room: $newestRoom")
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "❌ Lỗi khi lấy dữ liệu", error.toException())
        }
    })
}



fun fetch_room() {
    if (userId.value == "default_user") {
        userId.value = is_session_active() ?: "default_user"
    }
    roomList.clear()

    val database = FirebaseDatabase.getInstance().reference.child(userId.value)
    // Sử dụng get() thay vì addValueEventListener để chỉ lấy dữ liệu một lần khi gọi hàm
    database.get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val snapshot= task.result
            //duyet qua cac room
            for(roomSnapshot in snapshot.children){
                val roomId=roomSnapshot.key?:continue
                var roomName:String =""
                var dv:MutableList<Device> = mutableListOf()
                // duyệt cái này có cả main
                if(roomId!="main"){
                    Log.e("roomID", roomId)
                    //duyet qua cac device
                    for(deviceSnapshot in roomSnapshot.children){
                        if(deviceSnapshot.key !="roomname"){
                            val deviceId=deviceSnapshot.key?:continue
                            Log.e("deviceID", deviceId)
                            val deviceName=deviceSnapshot.child("devicename").getValue(String::class.java)?:continue
                            Log.e("deviceName", deviceName)
                            val deviceType=deviceSnapshot.child("devicetype").getValue(String::class.java)?:continue
                            Log.e("deviceType", deviceType)
                            if(!is_existed(deviceId,"dv")){
                                dv.add(Device(id = deviceId.toInt(), name = deviceName, type = deviceType, state = false, topic = roomId+"/"+deviceId))
                                roomList.find { it.id == roomId.toInt() }?.devices?.add(Device(id = deviceId.toInt(), name = deviceName, type = deviceType, state = false, topic = roomId+"/"+deviceId))
                            }

                        }else{
                            roomName=deviceSnapshot.getValue(String::class.java)?:continue
                            Log.e("roomName", roomName)
                        }
                    }
                    if(!is_existed(roomId, "r")) {
                        roomList.add(Room(id = roomId.toInt(), name = roomName, devices = dv))
                    }
                    refreshHomePage.value=true
                    refreshDevicePage.value=true
                }

            }




        } else {
            Log.e("Firebase", "❌ Lỗi khi lấy dữ liệu", task.exception)
        }
    }
}

fun is_existed(id:String, type:String):Boolean{//dv. r
    if(type=="dv"){
        for(i in roomList){
            for(j in i.devices){
                if(j.id==id.toInt()){
                    return true
                }
            }
        }
    }else{
        for (i in roomList){
            if (i.id==id.toInt()){
                return true
            }
        }
    }
    return false
}


/*
fun listenToFirebase(onDataChange: (String) -> Unit, deviceId:Int) {
    var  r="room"
    var  d="device"
    for (room in roomList){
        for (device in room.devices) {
            if(device.id==deviceId){
                r=r+room.id.toString()
                d=d+device.id.toString()
            }
        }
    }

    val database = FirebaseDatabase.getInstance("https://datn-8f7b7-default-rtdb.asia-southeast1.firebasedatabase.app/")
        .reference.child(r).child(d).child("status")

    database.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val status = snapshot.getValue(String::class.java) ?: "Lỗi dữ liệu!"
            onDataChange(status)  // Gửi dữ liệu về UI
        }

        override fun onCancelled(error: DatabaseError) {
            println("Lỗi khi đọc dữ liệu: ${error.message}")
        }
    })
}
private var firebaseListener: ValueEventListener? = null

fun stopListeningToFirebase() {
    val database = FirebaseDatabase.getInstance()
        .reference.child("devices").child("device1").child("status")

    firebaseListener?.let {
        database.removeEventListener(it)
        firebaseListener = null  // Xóa listener sau khi hủy
    }
}
*/

//sendDataToFirebase("on") // Gửi trạng thái bật
//sendDataToFirebase("off") // Gửi trạng thái tắt

//listenToFirebase { newStatus ->
//    println("Trạng thái mới: $newStatus") // Cập nhật UI dựa trên dữ liệu Firebase
//}

//stopListeningToFirebase()