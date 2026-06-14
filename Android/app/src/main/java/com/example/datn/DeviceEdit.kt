
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@Composable
fun DeviceEdit(deviceId:Int, nameOfDevice:String){
    var text1 by remember { mutableStateOf("") }//name
    var room_id= activeRoom.value

    Spacer(modifier = Modifier.padding(15.dp))
    Box(modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
        contentAlignment = Alignment.Center){
        Text(
            text = "Sửa thiết bị",
            // modifier = Modifier.align(Alignment.Center),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.matchParentSize(),


            )
        Row(modifier = Modifier.size(100.dp).align(Alignment.BottomEnd),
            horizontalArrangement = Arrangement.SpaceAround,

            ){
            Image(
                painter = painterResource(id = R.drawable.close), contentDescription = "Icon",
                modifier = Modifier.size(36.dp).pointerInput(Unit){
                    detectTapGestures (
                        onTap = {
                            Log.d("ấn nút close", "ấn nút close")
                            //close
                            BackToHomeScreen()
                        },

                        )
                }
            )

            Image(
                painter = painterResource(id = R.drawable.save), contentDescription = "Icon",
                modifier = Modifier.size(36.dp).padding(end=0.dp).pointerInput(Unit){
                    detectTapGestures (
                        onTap = {
                            Log.d("ấn nút Lưu thông tin", "ấn nút lưu thông tin")
                            //close
                            if(text1!="") roomList.flatMap { it.devices }.find { it.id == deviceId }?.name=text1
                            //SaveRoom(temp_name, temp_id)
                            SaveDevice(roomList.flatMap { it.devices }.find { it.id == deviceId }?.name ?:"Null Name",
                                deviceId, roomList.flatMap { it.devices }.find { it.id == deviceId }?.topic ?: "Null Topic",
                                room_id)
                        },

                        )
                }
            )
        }
    }
    //Noti
    Spacer(modifier = Modifier.padding(15.dp))
    Column (modifier = Modifier.verticalScroll(rememberScrollState()).fillMaxWidth()
    ){
        Spacer(modifier = Modifier.padding(15.dp))
        // thêm các ô dữ liệu vào đây
        Box(
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)
        ){
            Text(// ô id phòng
                text = "ID phòng: ${activeRoom.value}\n" +
                        "ID thiết bị: ${roomList.flatMap { it.devices }.find { it.id == deviceId }?.id}\n" +
                        "Topic: ${roomList.flatMap { it.devices }.find { it.id == deviceId }?.topic}\n" +
                        "Loại thiết bị: ${roomList.flatMap { it.devices }.find { it.id == deviceId }?.type}",
            )


        }


    }
    var keyboardController= LocalSoftwareKeyboardController.current// bàn phím
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
        label = { Text(text ="${roomList.flatMap { it.devices }.find { it.id == deviceId }?.name}") },
        //leadingIcon = { Icon(Icons.Default.Done, contentDescription = "") }
        placeholder = { Text(text = "Nhập tên mới") },
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

                if(text1!="") roomList.flatMap { it.devices }.find { it.id == deviceId }?.name=text1
                keyboardController?.hide()
            }
        ),

        )

    //Delete
    Spacer(modifier = Modifier.height(30.dp))
Box(
    modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
    contentAlignment = Alignment.Center
){
    Button(
        onClick ={
            //xử lý sự kiệns
            DeleteDevice(deviceId)
            Log.e("ấn", "click me")
        },

        colors = ButtonDefaults.buttonColors(containerColor = Color(0xff0253af),
            contentColor = Color.White),
        enabled = true
    )
    {// mac dinh la Row, neu muon thanh Collumn thi them o day
        Icon(Icons.Default.Delete, contentDescription = "")
        Text(text=" Xoá thiết bị")
    }
}

    //End Delete
}

@Composable
fun AddDeviceUI(){
    val context = LocalContext.current  // Lấy context từ Compose

    var typeDV  by remember { mutableStateOf("") }
    var text1 by remember { mutableStateOf("") }//name
    var room_id= activeRoom.value
    var temp_id by remember { mutableStateOf(createRandomID(9, "room")) }

    var temp_topic by remember { mutableStateOf("null topic") }
    // temp_name="Room"+temp_id.toString()
    var temp_name by remember { mutableStateOf("Room"+temp_id.toString()) }
    Spacer(modifier = Modifier.padding(15.dp))
    Box(modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
        contentAlignment = Alignment.Center){
        Text(
            text = "Thêm thiết bị",
            // modifier = Modifier.align(Alignment.Center),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.matchParentSize(),


            )
        Row(modifier = Modifier.size(100.dp).align(Alignment.BottomEnd),
            horizontalArrangement = Arrangement.SpaceAround,

            ){
            Image(
                painter = painterResource(id = R.drawable.close), contentDescription = "Icon",
                modifier = Modifier.size(36.dp).pointerInput(Unit){
                    detectTapGestures (
                        onTap = {
                            Log.d("ấn nút close", "ấn nút close")
                            //close
                            BackToHomeScreen()
                        },

                        )
                }
            )

            Image(
                painter = painterResource(id = R.drawable.save), contentDescription = "Icon",
                modifier = Modifier.size(36.dp).padding(end=0.dp).pointerInput(Unit){
                    detectTapGestures (
                        onTap = {
                            Log.d("ấn nút Lưu thông tin", "ấn nút lưu thông tin")
                            //close

                            if(text1!="") temp_name=text1
                            //SaveRoom(temp_name, temp_id)
                            if(typeDV!=""){
                                SaveDevice(temp_name, temp_id, temp_topic, room_id, typeDV)
                            }else{
                                showToast(context, "Vui lòng chọn thiết bị")

                            }

                        },

                        )
                }
            )
        }
    }
    //Noti
    Spacer(modifier = Modifier.padding(15.dp))
    Column (modifier = Modifier.verticalScroll(rememberScrollState()).fillMaxWidth()
    ){
        Spacer(modifier = Modifier.padding(15.dp))
        // thêm các ô dữ liệu vào đây
        Box(
            modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)
        ){
            Text(// ô id phòng
                text = "ID phòng: ${room_id}\nID thiết bị: ${temp_id}\nTopic: ${temp_topic}",
            )


        }


    }
    var keyboardController= LocalSoftwareKeyboardController.current// bàn phím
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
        label = { Text(text ="${temp_name}") },
        //leadingIcon = { Icon(Icons.Default.Done, contentDescription = "") }
        placeholder = { Text(text = "Nhập tên mới") },
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
                // khi ok thì làm gì, định nghĩa vào đây
                if(text1!="") temp_name=text1
                keyboardController?.hide()
            }
        ),

        )

    //menu

    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf("Chọn loại thiết bị") }

    val items = listOf("Quạt", "Đèn", "Điều hoà", "Cửa")
    Spacer(modifier = Modifier.padding(10.dp))
    Box(modifier = Modifier.wrapContentSize().padding(horizontal = 18.dp)) {
        Button(onClick = { expanded = true }) {
            Text(selectedItem)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        selectedItem = item
                        if(item=="Quạt") typeDV="fan"
                        if(item=="Đèn") typeDV="light"
                        if(item=="Điều hoà") typeDV="ac"
                        if(item=="Cửa") typeDV="door"
                        expanded = false
                    }
                )
            }
        }
    }

    //end menu
}
