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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datn.GlobalData.edittingRoom

@Composable
fun Room_edit(roomId:Int, nameOfRoom:String){// nhiều phòng + add
   // roomList.find{it.id==roomId}?.apply { name=nameOfRoom }


    var text1 by remember { mutableStateOf("") }//name
    var text2 by remember { mutableStateOf("") }//topic
    Spacer(modifier = Modifier.padding(15.dp))
    Box(modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
        contentAlignment = Alignment.Center){
        Text(
            text = "Sửa "+nameOfRoom,
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
                            Back_to_home_screen()
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
//                            text1.takeIf { it.isNotEmpty() }?.let {
//                                roomList[edittingRoom.value].name = it
//                                SaveRoom(it, roomList[edittingRoom.value].id)
//                            }
                            if(text1!="") set_room_name_ID(edittingRoom.value, text1)
                            Save_room(text1, roomId)

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
                text = "ID phòng: ${roomList.getOrNull(edittingRoom.value)?.id}\nTopic: ${roomList.getOrNull(edittingRoom.value)?.topic}"
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
        label = { Text(text ="${roomList.getOrNull(edittingRoom.value)?.name ?: ""}") },
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
               // if(text1!="") roomList[edittingRoom.value].name=text1
                if(text1!="") set_room_name_ID(edittingRoom.value, text1)
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
                Delete_room(roomId)
                Log.e("ấn", "click me")
            },

            colors = ButtonDefaults.buttonColors(containerColor = Color(0xff0253af),
                contentColor = Color.White),
            enabled = true
        )
        {// mac dinh la Row, neu muon thanh Collumn thi them o day
            Icon(Icons.Default.Delete, contentDescription = "")
            Text(text=" Xoá phòng")
        }
    }

    //End Delete
}


@Composable
fun Add_room_UI(){
    var text1 by remember { mutableStateOf("") }//name
    var text2 by remember { mutableStateOf("") }//topic

    var temp_id by remember { mutableStateOf(create_random_ID(9, "room")) }
    var temp_topic by remember { mutableStateOf(temp_id.toString()) }
    // temp_name="Room"+temp_id.toString()
    var temp_name by remember { mutableStateOf("Room"+temp_id.toString()) }
    Spacer(modifier = Modifier.padding(15.dp))
    Box(modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
        contentAlignment = Alignment.Center){
        Text(
            text = "Thêm Phòng",
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
                        Back_to_home_screen()
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
                        Save_room(temp_name, temp_id)
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
                text = "ID phòng: ${temp_id}\nTopic: ${temp_topic}",
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

}