package com.example.datn

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.datn.GlobalData.activeTaskbar
import com.example.datn.GlobalData.haveNotis
import com.example.datn.R

@Composable
fun Taskbar(){


        Box(contentAlignment = Alignment.Center){
            Image(painter = ColorPainter(Color(0xfff2f2f7)),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .size(80.dp)
                    .clip(shape = RoundedCornerShape(30))
            )

            Row(horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically){
//                IconTaskbar("home", activeTaskbar.value == "home")
//                IconTaskbar("noti", activeTaskbar.value == "noti", haveNoti = true)
//                IconTaskbar("setting", activeTaskbar.value == "setting")
                Button(//home
                    onClick = {
                      BackToHomeScreen()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    elevation = null // Loại bỏ hiệu ứng nổi
                ) {
                    IconTaskbar("home", activeTaskbar.value == "home")
                }

                Button(//noti
                    onClick = {
                        BackToNotiScreen()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    elevation = null
                ) {
                    IconTaskbar("noti", activeTaskbar.value == "noti", haveNoti = haveNotis.value)
                }


                Button(//setting
                    onClick = {
                        BackToSettingScreen()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ),
                    elevation = null
                ) {
                    IconTaskbar("setting", activeTaskbar.value == "setting")
                }

            }
        }


}

@Composable
// lấy dạng icon và trạng thái hoạt động -> Image
fun IconTaskbar(iconType: String, activeType:Boolean, haveNoti:Boolean=false, size: Dp =36.dp){
    LaunchedEffect (haveNotis.value){
        haveNotis.value= !isNotiEmpty()
    }
    if(iconType=="home"){
        if(activeType){
            Image(
                painter = painterResource(id = R.drawable.home2),
                contentDescription = "Icon",
                modifier = Modifier.size(size) // Kích thước của icon
            )
        }else{
            Image(
                painter = painterResource(id = R.drawable.home1),
                contentDescription = "Icon",
                modifier = Modifier.size(size) // Kích thước của icon
            )
        }
    }else if(iconType=="noti"){
        if(activeType){
            Image(
                painter = painterResource(id = R.drawable.bellblack),
                contentDescription = "Icon",
                modifier = Modifier.size(size-4.dp) // Kích thước của icon
            )
        }else{
            if(haveNotis.value){
                Image(
                    painter = painterResource(id = R.drawable.bellred),
                    contentDescription = "Icon",
                    modifier = Modifier.size(size) // Kích thước của icon
                )
            }else{
                Image(
                    painter = painterResource(id = R.drawable.bell),
                    contentDescription = "Icon",
                    modifier = Modifier.size(size) // Kích thước của icon
                )
            }
        }
    }else if(iconType=="setting"){
        if(activeType){
            Image(
                painter = painterResource(id = R.drawable.setting2),
                contentDescription = "Icon",
                modifier = Modifier.size(size) // Kích thước của icon
            )
        }else{
            Image(
                painter = painterResource(id = R.drawable.setting),
                contentDescription = "Icon",
                modifier = Modifier.size(size) // Kích thước của icon
            )
        }
    }
}
