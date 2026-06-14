package com.example.datn

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datn.GlobalData.activeRoom
import com.example.datn.GlobalData.refreshDevicePage
import com.example.datn.R
import kotlinx.coroutines.flow.combine

@Composable
fun ControlMain(roomId: Int, roomName: String) {

    // Lắng nghe biến trạng thái làm mới trang. Khi biến này thay đổi,
    // reset lại về false để sẵn sàng cho lần làm mới tiếp theo.
    LaunchedEffect(refreshDevicePage.value) {
        refreshDevicePage.value = false
    }

    // Bố trí các phần tử giao diện theo chiều dọc với độ giãn cách lề 10.dp
    Column(modifier = Modifier.padding(10.dp)) {

        // Tạo khoảng trống đệm phía trên 15.dp
        Spacer(modifier = Modifier.padding(15.dp))

        // Khối hàng ngang chứa tiêu đề phòng, căn giữa các phần tử và dàn đều theo chiều rộng
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Hiển thị tên phòng (Ví dụ: Phòng khách, Phòng ngủ) bằng chữ in đậm, cỡ lớn 30.sp
            Text(
                text = roomName,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }

        //Room
        Column (modifier = Modifier.verticalScroll(rememberScrollState())){
            Spacer(modifier = Modifier.padding(15.dp))

//            Fan("Quạt 1", 1)
//            Light("Đèn 1", 2)
//            AirConditioner("ĐH 1",3, 25)
            for(room in roomList){
                if(room.id==roomId){
                    for(device in room.devices){
                        when(device.type){
                            "fan" -> Fan(device.name, device.id)
                            "light" -> Light(device.name, device.id)
                            "ac" -> AirConditioner(device.name, device.id)
                            "door" -> Door(device.name, device.id)
                        }
                    }
                }
            }

            AddDeviceBox()
        }
    }
}

//điều hoà
@Composable
fun AirConditioner(name:String, id:Int){
    var isDelete = remember { mutableStateOf(false) }
    val device = getDevice(id)
    var isOn = remember { mutableStateOf(device?.state ?: false) }
    var temperature= remember { mutableStateOf("20") }
    // Lắng nghe Firebase và cập nhật state của device
    LaunchedEffect(id) {
        combine(
            listenToFirebase(id),               // Lắng nghe trạng thái "status"
            listenToFirebase(id, "temperature") // Lắng nghe trạng thái "temperature"
        ) { status, temp ->
            Pair(status, temp)
        }.collect { (status, temp) ->
            device?.state = (status == "on")
            isOn.value = device?.state ?: false
            device?.otherState = temp
            temperature.value = temp
        }
    }

    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.pointerInput(Unit){
            detectTapGestures (
                onLongPress = {
                    Log.d("long click vào phòng", "long click vào phòng")
                    //táp dài
                    isDelete.value=!isDelete.value
                },
                onTap = {
                    Log.d("click vào thiết bị điều hoà", "click vào thiết bị điều hoà")
                    // táp ngắn ->  truy cập menu điều hoà
                    GoToAirConditionerControlScreen(id)
                }
            )
        }
    ){
        if(isDelete.value){
            Row(verticalAlignment = Alignment.CenterVertically){
                // nút xoá
                Image(
                    painter = painterResource(id = R.drawable.editgreen),
                    contentDescription = "Icon",
                    modifier = Modifier
                        .size(50.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    Log.d("ấn nút chỉnh sửa thiết bị", "ấn nút chỉnh sửa thiết bị")
                                    // sửa thiết bị ở dưới đây
                                    Edit(id)
                                },

                                )
                        }

                )

                //box
                Box(contentAlignment = Alignment.Center){
                    Image(painter = ColorPainter(Color(0xffd9d9d9)),
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 15.dp, bottom = 15.dp, start = 10.dp)
                            .size(100.dp)
                            .clip(shape = RoundedCornerShape(30))
                    )

                    Row(horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically){
                        //
                        Text(
                            text = name,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                    }
                }
            }
        }else{
            Image(painter = ColorPainter(Color(0xff2196F3)),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, bottom = 15.dp)
                    .size(100.dp)
                    .clip(shape = RoundedCornerShape(30))
            )

            Row(horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically){
                //
                Text(
                    text = name,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(5f).padding(start = 55.dp).fillMaxWidth(),
                )
                Row(horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(4f).padding(end=5.dp)

                ){
                    Box(modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.CenterVertically)
                        .border(3.dp, Color.White, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center){
                        Text(text = temperature.value,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if(isOn.value){
                        Image(
                            painter = painterResource(id = R.drawable.poweron),
                            contentDescription = "Icon",
                            modifier = Modifier.size(40.dp).pointerInput(Unit){
                                detectTapGestures (
                                    onTap = {
                                        Log.d("tắt điều hoà", "tắt điều hoà")
                                        TurnOff(id)
                                        isOn.value = !isOn.value
                                    },
                                )
                            }// Kích thước của icon
                        )
                    }else{
                        Image(
                            painter = painterResource(id = R.drawable.poweroff),
                            contentDescription = "Icon",
                            modifier = Modifier.size(40.dp).pointerInput(Unit){
                                detectTapGestures (
                                    onTap = {
                                        Log.d("bật điều hoà", "bật điều hoà")
                                        TurnOn(id)
                                        isOn.value = !isOn.value

                                    },
                                )
                            }// Kích thước của icon
                        )
                    }

                }

            }
        }
    }
}

//quạt
@Composable
fun Fan(name: String, id: Int) {
    var isDelete = remember { mutableStateOf(false) }
    val device = getDevice(id)
    var isOn = remember { mutableStateOf(device?.state ?: false) }

    // Lắng nghe Firebase và cập nhật state của device
    LaunchedEffect(id) {
        listenToFirebase(id).collect { status ->
            device?.state = (status == "on")  // Cập nhật vào device.state trước
            isOn.value = device?.state ?: false  // Cập nhật vào isOn
        }
    }




    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onLongPress = {
                    Log.d("long click vào thiết bị", "long click vào thiết bị")
                    isDelete.value = !isDelete.value
                }
            )
        }
    ) {
        if (isDelete.value) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Nút sửa
                Image(
                    painter = painterResource(id = R.drawable.editgreen),
                    contentDescription = "Icon",
                    modifier = Modifier
                        .size(50.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    Log.d("ấn nút sửa quạt", "ấn nút sửa quạt")
                                    Edit(id)
                                }
                            )
                        }
                )

                // Box hiển thị tên thiết bị
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = ColorPainter(Color(0xffd9d9d9)),
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 15.dp, bottom = 15.dp, start = 10.dp)
                            .size(100.dp)
                            .clip(shape = RoundedCornerShape(30))
                    )
                    Text(
                        text = name,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        } else {
            // Hiển thị quạt
            Image(
                painter = ColorPainter(Color(0xff2196F3)),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, bottom = 15.dp)
                    .size(100.dp)
                    .clip(shape = RoundedCornerShape(30))
            )

            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .weight(2f)
                        .padding(start = 55.dp)
                        .fillMaxWidth()
                )

                // Hiển thị icon bật/tắt quạt
                Image(
                    painter = painterResource(id = if (isOn.value) R.drawable.son else R.drawable.soff),
                    contentDescription = "Icon",
                    modifier = Modifier
                        .size(40.dp)
                        .weight(1f)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    if (isOn.value) {
                                        Log.d("tắt quạt", "tắt quạt")
                                        TurnOff(id)
                                    } else {
                                        Log.d("bật quạt", "bật quạt")
                                        TurnOn(id)
                                    }
                                    isOn.value = !isOn.value
                                }
                            )
                        }
                )
            }
        }
    }
}


@Composable
fun Light(name: String, id: Int) {
    var isDelete = remember { mutableStateOf(false) }
    val device = getDevice(id)
    var isOn = remember { mutableStateOf(device?.state ?: false) }

    // Lắng nghe Firebase và cập nhật state của device
    LaunchedEffect(id) {
        listenToFirebase(id).collect { status ->
            device?.state = (status == "on")  // Cập nhật vào device.state trước
            isOn.value = device?.state ?: false  // Cập nhật vào isOn
        }
    }



    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onLongPress = {
                    Log.d("long click vào thiết bị", "long click vào thiết bị")
                    isDelete.value = !isDelete.value
                }
            )
        }
    ) {
        if (isDelete.value) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Nút sửa
                Image(
                    painter = painterResource(id = R.drawable.editgreen),
                    contentDescription = "Icon",
                    modifier = Modifier
                        .size(50.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    Log.d("ấn nút sửa đèn", "ấn nút sửa đèn")
                                    Edit(id)
                                }
                            )
                        }
                )

                // Box hiển thị tên thiết bị
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = ColorPainter(Color(0xffd9d9d9)),
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 15.dp, bottom = 15.dp, start = 10.dp)
                            .size(100.dp)
                            .clip(shape = RoundedCornerShape(30))
                    )
                    Text(
                        text = name,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        } else {
            // Hiển thị quạt
            Image(
                painter = ColorPainter(Color(0xff2196F3)),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, bottom = 15.dp)
                    .size(100.dp)
                    .clip(shape = RoundedCornerShape(30))
            )

            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .weight(2f)
                        .padding(start = 55.dp)
                        .fillMaxWidth()
                )

                // Hiển thị icon bật/tắt quạt
                Image(
                    painter = painterResource(id = if (isOn.value) R.drawable.son else R.drawable.soff),
                    contentDescription = "Icon",
                    modifier = Modifier
                        .size(40.dp)
                        .weight(1f)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    if (isOn.value) {
                                        Log.d("tắt quạt", "tắt quạt")
                                        TurnOff(id)
                                    } else {
                                        Log.d("bật quạt", "bật quạt")
                                        TurnOn(id)
                                    }
                                    isOn.value = !isOn.value
                                }
                            )
                        }
                )
            }
        }
    }
}







@Composable
//cửa
fun Door(name:String, id: Int){
    var isDelete = remember { mutableStateOf(false) }
    val device = getDevice(id)
    var isOn = remember { mutableStateOf(device?.state ?: false) }

    // Lắng nghe Firebase và cập nhật state của device
    LaunchedEffect(id) {
        listenToFirebase(id).collect { status ->
            device?.state = (status == "on")  // Cập nhật vào device.state trước
            isOn.value = device?.state ?: false  // Cập nhật vào isOn
        }
    }




    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onLongPress = {
                    Log.d("long click vào thiết bị", "long click vào thiết bị")
                    isDelete.value = !isDelete.value
                },
                onTap = {
                    Log.d("click vào cửa", "click vào cửa")
                    GoToDoorConfig(id)
                }
            )
        }
    ) {
        if (isDelete.value) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Nút sửa
                Image(
                    painter = painterResource(id = R.drawable.editgreen),
                    contentDescription = "Icon",
                    modifier = Modifier
                        .size(50.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    Log.d("ấn nút sửa quạt", "ấn nút sửa quạt")
                                    Edit(id)
                                }
                            )
                        }
                )

                // Box hiển thị tên thiết bị
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = ColorPainter(Color(0xffd9d9d9)),
                        contentDescription = "",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 15.dp, bottom = 15.dp, start = 10.dp)
                            .size(100.dp)
                            .clip(shape = RoundedCornerShape(30))
                    )
                    Text(
                        text = name,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        } else {
            // Hiển thị quạt
            Image(
                painter = ColorPainter(Color(0xff2196F3)),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, bottom = 15.dp)
                    .size(100.dp)
                    .clip(shape = RoundedCornerShape(30))
            )

            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .weight(2f)
                        .padding(start = 55.dp)
                        .fillMaxWidth()
                )

                // Hiển thị icon bật/tắt quạt
                Image(
                    painter = painterResource(id = if (isOn.value) R.drawable.door_open else R.drawable.door_close),
                    contentDescription = "Icon",
                    modifier = Modifier
                        .size(40.dp)
                        .weight(1f)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    if (isOn.value) {
                                        Log.d("đóng cửa", "đóng cửa")
                                        TurnOff(id)
                                    } else {
                                        Log.d("mở cửa", "mở cửa")
                                        TurnOn(id)
                                    }
                                    isOn.value = !isOn.value
                                }
                            )
                        }
                )
            }
        }
    }
}

@Composable
fun AddDeviceBox(){
    Box(contentAlignment = Alignment.Center){
        Image(painter = ColorPainter(Color(0xff2196F3)),
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp, bottom = 15.dp,)
                .size(100.dp)
                .clip(shape = RoundedCornerShape(30)),
        )

        Row(horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            Log.d("ấn nút add device", "ấn nút add device")
                            // xử lý thêm phòng ở đây
                            AddDevice()
                        },
                    )
                },
            verticalAlignment = Alignment.CenterVertically){
            //
            Image(
                painter = painterResource(id = R.drawable.plus),
                contentDescription = "Icon",
                modifier = Modifier.size(40.dp)// Kích thước của icon
            )

        }
    }
}



////////////////////// Menu điều hoà

@Composable
fun AirConditionerControl(id: Int, name: String) {
    val device = getDevice(id)
    var isOn = remember { mutableStateOf(device?.state ?: false) }

    // Lắng nghe Firebase và cập nhật state của device
    LaunchedEffect(id) {
        listenToFirebase(id).collect { status ->
            device?.state = (status == "on")  // Cập nhật vào device.state trước
            isOn.value = device?.state ?: false  // Cập nhật vào isOn
        }
    }

    //////////////////////
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // **Tiêu đề Remote**
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Remote $name",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )

            // **Nút Close ở góc dưới cùng**
            Image(
                painter = painterResource(id = R.drawable.close),
                contentDescription = "Close",
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.TopEnd)
                    .clickable {
                        Log.d("ấn nút Lưu thông tin", "ấn nút lưu thông tin")
                        GoToDeviceList(activeRoom.value)
                    }
            )
        }

        // **Điều khiển Remote**
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // **Nút UP**
            ScalableImage(R.drawable.up) {
                Log.d("bấm lên", "bấm lên")
                ChangeTemperature(id, "up")
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                // **Nút LEFT**
                ScalableImage(R.drawable.leftblack) {
                    Log.d("bấm trái", "bấm trái")
                    ChangeTemperature(id, "left")
                }

                // **Nút POWER**
                ScalableImage(if (isOn.value) R.drawable.poweron else R.drawable.powerblack) {
                    Log.d("bấm nguồn", "bấm nguồn")
                    isOn.value = !isOn.value
                    ChangeTemperature(id, if (isOn.value) "on" else "off")
                }

                // **Nút RIGHT**
                ScalableImage(R.drawable.rightblack) {
                    Log.d("bấm phải", "bấm phải")
                    ChangeTemperature(id, "right")
                }
            }

            // **Nút DOWN**
            ScalableImage(R.drawable.down) {
                Log.d("bấm xuống", "bấm xuống")
                ChangeTemperature(id, "down")
            }
        }
    }
}

@Composable
fun DoorControl(id:Int, name:String){
    val device = getDevice(id)
    val context = LocalContext.current  // Lấy context từ Compose

    val isChangePass = remember { mutableStateOf(false) }
    LaunchedEffect(id) {
        listenToFirebase(id,"password").collect {
            password -> device?.password = password
            isChangePass.value=!isChangePass.value

        }
    }
    LaunchedEffect(isChangePass.value) {}

    //////////////////////
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // **Tiêu đề Remote**
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Door",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )

            // **Nút Close ở góc dưới cùng**
            Image(
                painter = painterResource(id = R.drawable.close),
                contentDescription = "Close",
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.TopEnd)
                    .clickable {
                        Log.d("ấn nút Lưu thông tin", "ấn nút lưu thông tin")
                        GoToDeviceList(activeRoom.value)
                    }
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        var text1 by remember { mutableStateOf("") }//name
        var keyboardController= LocalSoftwareKeyboardController.current// bàn phím
        Text(
            text = "Mật khẩu hiện tại: ${device?.password}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(20.dp))
        ///TF

        TextField(//name
            modifier = Modifier.padding(horizontal = 18.dp),
            value =text1,
            onValueChange = {input->text1=input},
            textStyle = TextStyle(
                color = Color.Black,
                // fontFamily = FontFamily.Cursive,
                fontFamily = FontFamily.SansSerif, // Hỗ trợ tiếng Việt tốt
                fontSize = 30.sp
            ),
            label = { Text(text ="Nhập mật khẩu mới tại đây") },
            //leadingIcon = { Icon(Icons.Default.Done, contentDescription = "") }
            placeholder = { Text(text = "Nhập mật khẩu mới") },
            trailingIcon = {//// nút xoá
                IconButton(
                    onClick = {text1=""}
                ) {
                    Icon(Icons.Default.Close, contentDescription = "")
                }
                // muốn sửa thêm thì thêm colors = TextFieldsDefault()
            },
            colors = TextFieldDefaults.colors( // thêm tuỳ chỉnh màu
                disabledTextColor = Color.Transparent,
                unfocusedTextColor = Color.Transparent,
                errorTextColor = Color.Transparent,
                errorPlaceholderColor = Color.Transparent
            ),
            //bo góc
            shape= RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done, // xuống dòng hay done (send, search,....)
                keyboardType = KeyboardType.Text// bàn phím số
            ),
            keyboardActions = KeyboardActions(
                onDone = {

                    // if(text1!="") roomList[edittingRoom.value].name=text1
                    //if(text1!="") changePassWord(id, text1)
                    keyboardController?.hide()
                }
            ),

            )
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
            contentAlignment = Alignment.Center
        ){
            Button(
                onClick ={
                    //xử lý sự kiện
                    if(text1!="" && text1.all { it.isDigit() }){
                        changePassWord(id, text1)
                        isChangePass.value=!isChangePass.value
                        showToast(context, "Đổi mật khẩu thành công")
                        GoToDeviceList(activeRoom.value)


                    }else{
                        showToast(context, "Mật khẩu phải là chuỗi số")
                    }
                    Log.e("ấn", "click me")
                },

                colors = ButtonDefaults.buttonColors(containerColor = Color(0xff0253af),
                    contentColor = Color.White),
                enabled = true
            )
            {// mac dinh la Row, neu muon thanh Collumn thi them o day
                Icon(Icons.Default.Create, contentDescription = "")
                Text(text="Đổi mật khẩu")
            }
        }


        //ETF
    }


}

@Composable
fun ScalableImage(imageRes: Int, onClick: () -> Unit) {
    var isPressed = remember { mutableStateOf(false) }

    val scale = animateFloatAsState(
        targetValue = if (isPressed.value) 1.2f else 1f,
        animationSpec = tween(100), label = "scaleAnimation"
    )

    Image(
        painter = painterResource(id = imageRes),
        contentDescription = "Remote Button",
        modifier = Modifier
            .size(100.dp)
            .scale(scale.value)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed.value = true
                        tryAwaitRelease() // Đợi thả ra
                        isPressed.value = false
                        onClick()
                    }
                )
            }
    )
}
