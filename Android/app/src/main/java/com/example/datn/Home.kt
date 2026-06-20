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
import com.example.datn.GlobalData.activeRoom
import com.example.datn.GlobalData.refreshHomePage

@Composable
fun Home_screen(){// nhiều phòng + add
    LaunchedEffect (refreshHomePage.value){
        refreshHomePage.value =false
    }
    Column(modifier = Modifier.padding(10.dp)){
        //Text
        Spacer(modifier = Modifier.padding(15.dp))
        Row(
            modifier = Modifier.padding(10.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ){

            Text(
                text = "Trang Chủ",
               // modifier = Modifier.align(Alignment.Center),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }

        //Room
        Column (modifier = Modifier.verticalScroll(rememberScrollState())){
            Spacer(modifier = Modifier.padding(15.dp))

//            RoomBox("Phòng Ngủ", 0)
//            RoomBox("Phòng Khách",1)
//            RoomBox("Phòng Khách",3)
//            RoomBox("Phòng Khách",4)
//            RoomBox("Phòng Khách",5)
//            RoomBox("Phòng Khách",6)
            for(room in roomList){
                Room_box(room.name, room.id)
            }

            if(is_user_logged_in()){
                Add_room_box()
            }
            else{
                Text(
                    text = "Đăng nhập để thêm phòng!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}



@Composable
fun Room_box(name:String, id:Int){// 1 Room
    var isDelete = remember { mutableStateOf(false) }

    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.pointerInput(Unit){
            detectTapGestures (
                onTap = {
                    Log.d("click vào phòng", "click")
                    // tap 1 phát
                    //redirect đến cái phòng
                    Go_to_device_list(id)
                    Log.d("click activeRoom", activeRoom.value.toString())

                },
                onLongPress = {
                    Log.d("long click vào phòng", "long click vào phòng")
                    //táp dài
                    isDelete.value=!isDelete.value
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
                    modifier = Modifier.size(50.dp).pointerInput(Unit){
                        detectTapGestures (
                            onTap = {
                                // sửa ở dưới đây
                                Log.d("ấn nút chỉnh sửa phòng", "ấn nút chỉnh sửa phòng")
                                Edit_room(id)

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
                            .padding(top=15.dp, bottom = 15.dp, start = 10.dp )
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
                    .padding(top=15.dp, bottom = 15.dp )
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
                    color = Color.White
                )

            }
        }
    }
}
 @Composable
fun Add_room_box(){
     Box(contentAlignment = Alignment.Center){
         Image(painter = ColorPainter(Color(0xff2196F3)),
             contentDescription = "",
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(top=15.dp, bottom = 15.dp, )
                 .size(100.dp)
                 .clip(shape = RoundedCornerShape(30)),
         )

         Row(horizontalArrangement = Arrangement.SpaceAround,
             modifier = Modifier.fillMaxWidth().pointerInput(Unit){
                 detectTapGestures (
                     onTap = {
                         Log.d("ấn nút add phòng", "ấn nút add phòng")
                         // xử lý thêm phòng ở đây
                         Add_room()
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