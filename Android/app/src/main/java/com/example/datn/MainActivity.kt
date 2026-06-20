package com.example.datn

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.datn.ui.theme.DATNTheme
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.Manifest
import androidx.compose.ui.platform.LocalContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        create_notification_channel(this) // Tạo kênh thông báo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }


        enableEdgeToEdge()
        setContent {
            DATNTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )

                   // geUpdateNoti()
                   // getDeviceUpdate()
                    if(is_user_logged_in()){
                        val context = LocalContext.current

                        LaunchedEffect(Unit) {
                            get_update_noti(context)
                            get_device_update()
                            get_room_update()
                        }
                    }
                    Main()

                }
            }
        }
    }

///


}

object GlobalData {
    var activeTaskbar = mutableStateOf("home")// xem thanh taskbar đang ở phần nào
    var activeRoom= mutableStateOf(-1)// mỗi phòng có 1 id
    var haveNotis = mutableStateOf(true)// có thông báo thì hiện chấm đỏ
    var controlAriconditioner = mutableStateOf( -1)// vào menu điều hiển điều hoà
    var edittingRoom = mutableStateOf(-1)// chỉnh sửa phòng
    var edittingDevice = mutableStateOf(-1)// chỉnh sửa thiết bị
    //var editDevice = mutableStateOf(false)// chỉnh sửa thiết bị
    var getUpdateDelay = mutableStateOf(1000)// 1000ms
    var deviceStateChange = mutableStateOf(false)
    var URLFB = mutableStateOf("https://datn-8f7b7-default-rtdb.asia-southeast1.firebasedatabase.app/")
    var newestNoti= mutableStateOf("null")
    var AuthMsg = mutableStateOf("")
    var userId = mutableStateOf("default_user")
    var newestDevice = mutableStateOf("null")
    var newestRoom = mutableStateOf("null")
    var recentUpdate = mutableStateOf(false)
    var refreshHomePage = mutableStateOf(false)
    var refreshDevicePage = mutableStateOf(false)
    var AuthState = mutableStateOf(false) // Lưu trạng thái đăng nhập
    var notiCount = mutableStateOf(notiList.size)
    var newestNotiData = mutableStateOf("null")

}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {

}



@Preview(showBackground = true)
@Composable
fun Greeting_preview() {
    DATNTheme {
        Greeting("Android")
    }
}


private fun create_notification_channel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "noti_channel_id",
            "Thông báo",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Kênh thông báo chính"
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}
