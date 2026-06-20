package com.example.datn

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datn.GlobalData.haveNotis
import com.example.datn.GlobalData.newestNoti
import com.example.datn.GlobalData.userId
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.content.Context
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.Manifest
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.datn.GlobalData.newestNotiData
import com.example.datn.GlobalData.notiCount

@Composable
fun Noti_main() {
    val notiListState = remember { mutableStateOf(notiList.toList()) } // State lưu danh sách thông báo
    val currentNoti2 = remember { mutableStateOf("null") } // State lưu thông báo hiện tại
    val currentNoti1 = remember { mutableStateOf("null") } // State lưu thông báo hiện tại

    LaunchedEffect(newestNoti.value) {
        currentNoti1.value = newestNoti.value

        if (currentNoti1.value != currentNoti2.value) {
            Log.d("currentNoti1", currentNoti1.value)
            Log.d("currentNoti2", currentNoti2.value)

            currentNoti2.value = currentNoti1.value
            newestNotiData.value = currentNoti2.value

            // Cập nhật danh sách thông báo khi có thông báo mới
            notiListState.value = notiList.toList()
        }
    }

    Spacer(modifier = Modifier.padding(15.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Thông Báo",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.matchParentSize()
        )

        Image(
            painter = painterResource(id = R.drawable.trashblack),
            contentDescription = "Icon",
            modifier = Modifier
                .size(36.dp)
                .align(Alignment.BottomEnd)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            Log.d("ấn nút xoá thông báo", "ấn nút xoá thông báo")

                            // Gọi hàm xoá
                            Delete_noti()

                            // Cập nhật lại UI
                            notiList.clear()
                            notiListState.value = emptyList()
                        }
                    )
                }
        )
    }

    // Danh sách thông báo
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Spacer(modifier = Modifier.padding(15.dp))
        for (noti in notiListState.value.asReversed()) {
            Noti_box(noti.text, "${noti.hour}:${noti.minute}")
        }

    }
}

@Composable
fun Noti_box(text: String, time: String, isRead: Boolean=false){
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.height(90.dp)){
        Image(painter = ColorPainter(Color(0xeed9d9d9)),
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .size(68.dp)
                .clip(shape = RoundedCornerShape(40))
        )

        Row(horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically){
            Text(
                text=text,
                modifier = Modifier.padding(start = 40.dp).weight(1f),
                fontWeight = FontWeight.Medium,
                fontSize = 28.sp,
                color = Color.White
            )
            Text(
                text=time,
                modifier = Modifier.padding(start=30.dp, end = 30.dp),
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp
            )
        }
    }
}


fun Clear_noti_list(){
    notiList.clear()
    notiCount.value = notiList.size
}

fun is_noti_empty():Boolean{
    Log.d("Firebase", "checking empty")
    return   notiList.isEmpty()
}

fun delete_allNotifications_fromDTB() {
    if(userId.value=="default_user") {
        userId.value = is_session_active() ?: "default_user"
    }
    val database = FirebaseDatabase.getInstance().reference
    val notiRef = database.child(userId.value).child("main").child("noti")

    notiRef.removeValue()
        .addOnSuccessListener {
            Log.d("Firebase", "✅ Đã xóa tất cả thông báo thành công!")
        }
        .addOnFailureListener { e ->
            Log.e("Firebase", "❌ Lỗi khi xóa tất cả thông báo: ${e.message}")
        }
}




var notiList = mutableListOf<NotiData>()

fun get_update_noti(context: Context) {
    var userId = mutableStateOf("default_user")
    userId.value = is_session_active() ?: "default_user" // Cập nhật userId nếu có
    CoroutineScope(Dispatchers.IO).launch {
        listen_to_FB2("${userId.value}/main/newestNoti").collect { status ->
            if (status != newestNoti.value) {
                newestNoti.value = status // Cập nhật giá trị mới nhất
                fetch_noti_from_FB(context) // Fetch lại noti khi có cập nhật
            }
            Log.d("status", status)
        }
    }
}


fun fetch_noti_from_FB(context: Context) {
    if(userId.value=="default_user") {
        userId.value = is_session_active() ?: "default_user"
    }
    val database = FirebaseDatabase.getInstance().reference
    val notiRef = database.child(userId.value).child("main").child("noti")
    var lastNotiId: String? = null
    //var notiCount:Int = notiList.size

    notiRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            notiList.clear() // Clear old list before updating it with new data
            haveNotis.value= !is_noti_empty()

            Log.d("clear", "Cleared noti list")

            for (notiSnapshot in snapshot.children) {
                val notiId = notiSnapshot.key ?: continue
                val text = notiSnapshot.child("text").getValue(String::class.java) ?: "null"
                val timeRaw = notiSnapshot.child("time").getValue(String::class.java) ?: "null"
                Log.d("Firebase", "🔔 Thông báo từ $timeRaw")
                // Split the timeRaw and parse the data
                val parsedData = parse_time_data(timeRaw)
                val deviceId = parsedData[0] as String
                val hour = parsedData[1] as String
                val minute = parsedData[2] as String
                val day = parsedData[3] as String
                val month = parsedData[4] as String
                val year = parsedData[5] as String

                // Create a NotiData object and add it to the list
                val noti = NotiData(
                    deviceId = deviceId,
                    notiId = notiId,
                    hour = hour,
                    minute = minute,
                    day = day,
                    month = month,
                    year = year,
                    text = text
                )
/*
                if (notiId != lastNotiId) {
                    lastNotiId = notiId
                    showLocalNotification(context, text, timeRaw)
                }
 */


                notiList.add(noti)
                haveNotis.value= !is_noti_empty()

                Log.d("Firebase", "🔔 Thông báo từ $deviceId lúc $hour:$minute ngày $day/$month/$year - $text")
            }


            // Log the updated list
            Log.d("Firebase", "🔔 Danh sách thông báo cập nhật: $notiList")

        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "❌ Lỗi khi lấy dữ liệu", error.toException())
        }
    })

    //sortNotiListByTime()  // Thêm dòng này

    if(notiCount.value < notiList.size){
        notiList.forEach { noti ->
            Log.d("Notilist", "🔔 ${noti.text} lúc ${noti.hour}:${noti.minute}")
        }
       // val latestNoti = notiList.get(notiList.size-1)
        val latestNoti = get_latest_noti()

        if (latestNoti != null) {
            val title = "${latestNoti.text}"
            val message = "${latestNoti.text} lúc ${latestNoti.hour}:${latestNoti.minute}"
            show_local_notification(context, title, message)
        }
        notiCount.value = notiList.size
        notiCount.value= notiList.size
        // lấy trạng thái lastest trong notilist và dùng hàm showLocalNotification
    }
}

// Parsing the time data
fun parse_time_data(timeRaw: String): List<Any> {
    val parts = timeRaw.split(".")
    Log.d("parseTimeData", "Parts: $parts")

    return if (parts.size == 7) {
        try {
            // Parse all parts into their correct types
            val deviceId = parts[0]
            val hour = parts[1]
            val minute = parts[2]
            val second=parts[3]
            val day = parts[4]
            val month = parts[5]
            val year = parts[6]

            listOf(deviceId, hour, minute, day, month, year)
        } catch (e: Exception) {
            // In case parsing fails, provide default values
            Log.e("parseTimeData", "Error parsing time data: $timeRaw", e)
            listOf("Unknown", "00", "00", "01", "01", "2000")
        }
    } else {
        // If the timeRaw format doesn't have 6 parts, return default values
        Log.e("parseTimeData", "Invalid time format: $timeRaw")
        listOf("Unknown", "00", "00", "01", "01", "2000")
    }
}


fun show_local_notification(context: Context, title: String, message: String) {
    // Android 13+ cần kiểm tra quyền
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            return
        }
    }

    val builder = NotificationCompat.Builder(context, "noti_channel_id")
        .setSmallIcon(R.drawable.logo) // Hoặc drawable bạn tạo
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt(), builder.build())
}

fun get_latest_noti(): NotiData? {
    return notiList.maxByOrNull { noti ->
        val year = noti.year.toIntOrNull() ?: 0
        val month = noti.month.toIntOrNull() ?: 0
        val day = noti.day.toIntOrNull() ?: 0
        val hour = noti.hour.toIntOrNull() ?: 0
        val minute = noti.minute.toIntOrNull() ?: 0

        // Tính "điểm thời gian" kiểu thủ công
        year * 365 * 24 * 60 + month * 31 * 24 * 60 + day * 24 * 60 + hour * 60 + minute
    }
}
