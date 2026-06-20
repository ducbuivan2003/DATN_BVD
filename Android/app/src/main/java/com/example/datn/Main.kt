package com.example.datn

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.datn.GlobalData.activeRoom
import com.example.datn.GlobalData.activeTaskbar
import com.example.datn.GlobalData.controlAriconditioner
import com.example.datn.GlobalData.edittingDevice
import com.example.datn.GlobalData.edittingRoom


@Composable
fun Main(){   //taskbar khai báo rồi ở chỗ Main activity
    Column (modifier = Modifier.padding(10.dp)){
        Column (modifier = Modifier.weight(1f)){
            //home, noti, setting, editroom, editdevice, addroom, adddevicem, airconditioner, devicelist
            if(activeTaskbar.value=="home") Home_screen()
            if(activeTaskbar.value=="noti") Noti_main()
            if(activeTaskbar.value=="setting") Setup_UI()

            //if(activeTaskbar.value=="devicelist") ControlMain(activeRoom.value,roomList[activeRoom.value].name)
            //if(activeTaskbar.value=="editroom") RoomEdit(edittingRoom.value,roomList[edittingRoom.value].name)
            if(activeTaskbar.value=="devicelist") ControlMain(activeRoom.value,get_room_name(activeRoom.value))
            if(activeTaskbar.value=="editroom") Room_edit(edittingRoom.value,get_room_name(activeRoom.value))
            if(activeTaskbar.value=="addroom") Add_room_UI()
            if(activeTaskbar.value=="adddevice") Add_device_UI()
            if(activeTaskbar.value=="airconditioner") AirConditioner_control(controlAriconditioner.value,Find_name(edittingDevice.value))
            if(activeTaskbar.value=="door") Door_control(controlAriconditioner.value,Find_name(edittingDevice.value))
            if(activeTaskbar.value=="editdevice") Device_edit(edittingDevice.value, Find_name(edittingDevice.value))

        }
        Taskbar()
    }
}
fun Find_name(id:Int):String{
    for(room in roomList){
        for(device in room.devices){
            if (device.id==id) return device.name
        }
    }
    return ""
}