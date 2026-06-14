package com.example.datn

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datn.R

@Composable
fun TestUI(modifier: Modifier=Modifier){
    Column (modifier = modifier,
        // verticalArrangement = Arrangement.Center
    ) {
        GreetingText()
        Spacer(modifier=Modifier.height(10.dp))// thêm khoảng trắng

        Banner()
        Spacer(modifier=Modifier.height(10.dp))// thêm khoảng trắng

        Painter()
        Spacer(modifier=Modifier.height(10.dp))// thêm khoảng trắng

        SimpleButton()
        Spacer(modifier=Modifier.height(10.dp))// thêm khoảng trắng

        PressText()
        Spacer(modifier=Modifier.height(10.dp))// thêm khoảng trắng

        RadioBT()
        Spacer(modifier=Modifier.height(10.dp))// thêm khoảng trắng

        TestCheckBox()
        Spacer(modifier=Modifier.height(10.dp))// thêm khoảng trắng

        DemoTextField()
        Spacer(modifier=Modifier.height(10.dp))// thêm khoảng trắng
        Account()
        Spacer(modifier=Modifier.height(10.dp))// thêm khoảng trắng
        /////BOX
        Box (modifier = Modifier.fillMaxSize()){
            BoxItem(color = Color.Green, size = 200.dp, modifier=Modifier.matchParentSize())
            BoxItem(color = Color.Red, size = 150.dp, modifier = Modifier.align(Alignment.BottomEnd))

            BoxItem(color = Color.Blue, modifier = Modifier.align(Alignment.BottomEnd))
        }
        Spacer(modifier=Modifier.height(10.dp))// thêm khoảng trắng

        //Box-Row
        Row(modifier=Modifier.fillMaxWidth().
        height(200.dp).
            //size(300.dp, 400.dp)
        background(color = Color.Green),
            //https://developer.android.com/reference/kotlin/androidx/compose/foundation/layout/package-summary?hl=en#Row(androidx.compose.ui.Modifier,androidx.compose.foundation.layout.Arrangement.Horizontal,androidx.compose.ui.Alignment.Vertical,kotlin.Function1)
            horizontalArrangement = Arrangement.SpaceEvenly, // khoảng cách đều
            verticalAlignment = Alignment.CenterVertically //
        ){
            BoxItem(color = Color.Cyan)
            BoxItem(color = Color.Yellow)
            BoxItem(color = Color.Black)

        }
        Spacer(modifier=Modifier.height(10.dp))// thêm khoảng trắng

        /// BOX Column
        Column (modifier=Modifier
            .size(400.dp, 400.dp)
            .background(color = Color.Red)
            , verticalArrangement = Arrangement.SpaceEvenly
            , horizontalAlignment = Alignment.CenterHorizontally
        ){
            BoxItem(color = Color.Cyan)
            BoxItem(color = Color.Yellow)
            BoxItem(color = Color.Black)
        }

        //Search Bar
        SearchBarDemo()
        Spacer(modifier=Modifier.height(10.dp))// thêm khoảng trắng

    }
}


///// Text
@Composable
fun GreetingText(){
    Text(text= "Hello World",
        color = Color.Black,
        textAlign = TextAlign.Center,
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        fontFamily = FontFamily.Cursive

    )


}
// ảnh
@Composable
fun Banner(){
    Image(
        painterResource(id= R.drawable.car),
        contentDescription = "Porsche",
        // modifier = Modifier.fillMaxHeight(0.6f),
        modifier = Modifier.height(300.dp).
        border(BorderStroke(2.dp, Color.Black)).
        shadow(elevation = 8.dp, shape = RoundedCornerShape(size = 8.dp)),

        contentScale = ContentScale.Crop
    )

}

// màu
@Composable
fun Painter(){
    Image(
        ColorPainter(Color.DarkGray),
        contentDescription = "painter",
        modifier = Modifier.height(40.dp).
        fillMaxWidth().
        clip(shape = CircleShape),// tạo tròn
        contentScale = ContentScale.Crop
    )

}
////Button
@Composable
fun SimpleButton(){
    var count= remember { mutableStateOf(0) }
    Text(text="value = ${count.value}")
    Button(onClick ={
        //xử lý sự kiệns
        Log.e("ấn", "click me")
        count.value++
    },

        colors = ButtonDefaults.buttonColors(containerColor = Color(0xff0253af),
            contentColor = Color.White),
        enabled = true
    )
    {// mac dinh la Row, neu muon thanh Collumn thi them o day
        Icon(Icons.Default.Call, contentDescription = "")
        Text(text=" Click me")
    }

}
// double tap, press
@Composable
fun PressText(){
    var hehe= remember { mutableStateOf("") }
    Text(text = hehe.value)
    Text(text = "test",
        modifier = Modifier.pointerInput(Unit){
            detectTapGestures (
                onTap = {hehe.value="on tap"},
                onDoubleTap = {hehe.value= "double tap"},
                onLongPress = {hehe.value= "long press"}
            )
        })

}
//radio button -> chọn 1 trong nhiều lựa chọn
@Composable
fun RadioBT(){
    // chọn 1 trong nhiều lựa chọn
    var selectedNumber by remember { mutableStateOf(0) }
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.selectable(
            selected = selectedNumber==1,
            onClick = {selectedNumber=1},
            role = Role.RadioButton
        )){
        RadioButton( selected = selectedNumber==1, onClick = {})
        Text(text = "Option 1")
    }
    Row (verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.selectable(
            selected = selectedNumber==2,
            onClick = {selectedNumber=2},
            role = Role.RadioButton
        )
    ){
        RadioButton( selected = selectedNumber==2, onClick = {})
        Text(text = "Option 2")
    }

    // có thể chọn nhiều lựa chọn
    var isSelected by remember { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.selectable(
            selected = isSelected,
            onClick = {isSelected=!isSelected},
            role = Role.RadioButton
        )
    ){
        RadioButton(selected = isSelected, onClick = {})
        Text(text = "Additional Option")
    }

}

//check box
@Composable
fun TestCheckBox(){
    var isCheck1 by remember { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically,// modifier ở Row
        modifier = Modifier.selectable(
            selected = true,
            onClick = {isCheck1=!isCheck1},
            role = Role.Checkbox

        )
    ){
        Checkbox(checked = isCheck1, onCheckedChange = {})
        Text(text = "check 1")
    }
}

//Text Field
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoTextField(){
    var text1 by remember { mutableStateOf("") }
    var keyboardController= LocalSoftwareKeyboardController.current// bàn phím
    TextField(
        value =text1,
        onValueChange = {input->text1=input},
        textStyle = TextStyle(
            color = Color.Black,
            fontFamily = FontFamily.Cursive,
            fontSize = 30.sp
        ),
        label = { Text(text = "text here !!!!!!") },
        leadingIcon = { Icon(Icons.Default.Done, contentDescription = "") },
        placeholder = { Text(text = "nhập vào đây") },
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
            keyboardType = KeyboardType.Phone// bàn phím số
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                // khi ok thì làm gì, định nghĩa vào đây
                keyboardController?.hide()
            }
        ),


        )
}
///// Account
@Composable
fun Account(){
    var email1 by remember { mutableStateOf("") }
    var pass1 by remember { mutableStateOf("") }
    var passHide by remember { mutableStateOf(false) }
    var keyBoardControler= LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    Column (){
        OutlinedTextField(
            value=email1,
            onValueChange = {input-> email1=input},
            label = { Text(text="email/username") },
            leadingIcon ={ Icon(Icons.Default.Email, contentDescription = "") },
            trailingIcon = { IconButton(onClick = {email1=""}){
                Icon(Icons.Default.Close, contentDescription = "clear")
            } },
            keyboardOptions= KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = pass1,
            onValueChange = {input->pass1=input},
            label = { Text(text = "password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "") },
            trailingIcon = {
                IconButton(onClick = {
                    passHide=!passHide

                }) {
                    if(!passHide){
                        Icon(Icons.Default.CheckCircle, contentDescription = "hide password")
                    }else{
                        Icon(Icons.Default.Check, contentDescription = "show password")
                    }
                }
            },
            visualTransformation = if (passHide) PasswordVisualTransformation() else VisualTransformation.None,

            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done, // xuống dòng hay done (send, search,....)
                // keyboardType = KeyboardType.Phone// bàn phím số
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    // khi ok thì làm gì, định nghĩa vào đây
                    keyBoardControler?.hide()
                }
            ),
        )

    }
}

////////////////////////LAYOUT
///box
@Composable
fun BoxItem(color: Color, size: Dp =100.dp, modifier: Modifier = Modifier ){
    Box(modifier = modifier.
    size(size).
    background(color=color))
}


//Search Bar ROW
@Composable
fun SearchBarDemo(){
    var keyBoardController = LocalSoftwareKeyboardController.current
    var search1 by remember { mutableStateOf("") }
    Row(verticalAlignment = Alignment.CenterVertically){
        OutlinedTextField(
            value = search1,
            label = { if (search1.isEmpty()) Text("Search here") },
            onValueChange = { input ->
                search1 = input
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    keyBoardController?.hide()
                    // send request
                }
            ),
            modifier = Modifier.weight(5f), // chiếm 5 phần
            shape = RoundedCornerShape(size = 30.dp)
        )
        IconButton(onClick = {
            keyBoardController?.hide()
            // send request
        },
            modifier = Modifier.weight(1f)) { // chiếm 1 phần
            Icon(Icons.Default.Search, contentDescription = "", modifier = Modifier.size(40.dp))
        }
    }
}
