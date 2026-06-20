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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datn.GlobalData.AuthMsg
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Setup_UI() {
    var temp_email = remember { mutableStateOf("") }
    var temp_pw= remember { mutableStateOf("") }
    val context = LocalContext.current  // Lấy context từ Compose

    var AuthState = remember { mutableStateOf(is_user_logged_in())}
    var AuthState2 = remember { mutableStateOf("dk") }// trạng thái đăng ký, đăng nhập, quên mk, đổi mk
    LaunchedEffect(AuthState.value) { }
    LaunchedEffect (AuthState2.value){}
    Column (){
       Box(
           modifier = Modifier.padding(top = 40.dp).fillMaxWidth(),
           contentAlignment = Alignment.Center
       ){
           Text(
               text="ĐỒ ÁN TỐT NGHIỆP\n\nBùi Văn Đức-20213877\n\n",
               fontSize = 26.sp,
              // fontStyle = FontStyle.Italic,
               //fontFamily = FontFamily.Cursive,
               fontWeight = FontWeight.Bold,
               textAlign = TextAlign.Center
           )
       }
        Image(
            painter = painterResource(id= R.drawable.hust),
            contentDescription = "HUST",
            //modifier = Modifier.size(0.dp)
            modifier = Modifier.pointerInput(Unit){
                detectTapGestures (
                    onTap = {
                        Test()

                    },
                    )
            }
        )
        Spacer(modifier = Modifier.height(20.dp))

        // tài khoản
        Column (
            modifier = Modifier.fillMaxWidth()
        ){


            // nếu đã login thì hiện nút đăng xuất

        if(is_user_logged_in()){
            Column (
                modifier = Modifier.padding(top = 40.dp).fillMaxWidth(),
            ){

                Text(text="Email: ${get_active_user_email()}\n",
                    fontSize = 18.sp,)
                Text(text="ID: ${is_session_active()}",
                    fontSize = 18.sp,)
            }

            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier.padding(top = 40.dp).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                //bt
                custom_text_button("Đăng xuất"){
                    logout_User()
                    AuthState.value=is_user_logged_in()
                }
            }

        }else{
            //nếu chưa login thì hiện nút đăng nhập & đăng ký & quên mật khẩu

            // đăng nhập
            Column (
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally // Canh giữa ngang
            ){
                    if(AuthState2.value!="mk"){
                        Custom_text_field("email"){
                            Log.e("click", it)
                            temp_email.value=it.trim().lowercase()

                        }
                        Custom_text_field("password") {
                            Log.e("click", it)
                            temp_pw.value=it
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))
                if(AuthState2.value=="dn"){
                    other_choise("Đăng ký", "Quên mật khẩu",
                        onClick1 = {
                            AuthState2.value="dk"
                        },
                        onClick2 = {
                            AuthState2.value="mk"
                        }
                    )
                    custom_text_button("Đăng Nhập") {
                        if(temp_pw.value=="" || temp_email.value==""){
                            show_toast(context, "email hoặc password đang trống")
                        }
                        else{
                            Log.d("Login Info", "Email: ${temp_email.value}, Password: ${temp_pw.value}")
                            login_User(temp_email.value, temp_pw.value)
                            CoroutineScope(Dispatchers.Main).launch {
                                delay(1000) // Đợi một chút để trạng thái cập nhật
                                show_toast(context, AuthMsg.value)
                                AuthState.value = is_user_logged_in()
                            }
                        }
                    }
                }
                else if(AuthState2.value=="dk"){
                    other_choise(
                        "Đăng nhập", "Quên mật khẩu",
                        onClick1 = {
                            AuthState2.value="dn"
                        },
                        onClick2 = {
                            AuthState2.value="mk"
                        }
                    )
                    custom_text_button("Đăng Ký") {
                        if(temp_pw.value=="" || temp_email.value==""){
                            show_toast(context, "email hoặc password đang trống")
                        }
                        else {
                            Log.d("Signin Info", "Email: ${temp_email.value}, Password: ${temp_pw.value}")
                            register_user(context, temp_email.value, temp_pw.value)
                        }

                    }
                }
                else if(AuthState2.value=="mk"){

                    Custom_text_field("email"){
                        Log.e("click", it)
                        temp_email.value=it.trim().lowercase()

                    }



                    /////////

                    other_choise(
                        "Đăng nhập", "Đăng ký",
                        onClick1 = {
                            AuthState2.value="dn"
                        },
                        onClick2 = {
                            AuthState2.value="dk"
                        }
                    )
                    custom_text_button("Gửi yêu cầu") {

                        request_password_reset(temp_email.value, context)
                        AuthState2.value="dn"
                        ////cái này xử lý quên mật khẩu
                    }
                }

            }
            // đăng ký

            // quên mật khẩu

            // đổi mật khẩu
        }

        }

        //end tài khoản

    }
}


@Composable
fun Custom_text_field(
    label: String,
    onDone: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = text,
        onValueChange = { text = it
            onDone(text)  // Gọi hàm khi nhấn "Done"
                        },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()  // Ẩn bàn phím
                onDone(text)  // Gọi hàm khi nhấn "Done"
            }
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun custom_text_button(
    text: String,
    onClick: () -> Unit
){
    Spacer(modifier = Modifier.height(10.dp))
    Button(
        onClick = {
            Log.e("click", "click button")
            onClick()
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xff0253af),
            contentColor = Color.White),
        enabled = true
    ) {
        Text(text=text,
            fontSize = 28.sp,)
    }
}



@Composable
fun other_choise(
    str1: String,
    str2: String,
    onClick1: () -> Unit,
    onClick2: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween // Đẩy 2 text về 2 bên
        ) {
            Box(
                modifier = Modifier.weight(1f), // Đẩy sang trái
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = str1,
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures(onTap = { onClick1() })
                    }
                )
            }

            Box(
                modifier = Modifier.weight(1f), // Đẩy sang phải
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = str2,
                    modifier = Modifier.pointerInput(Unit) {
                        detectTapGestures(onTap = { onClick2() })
                    }
                )
            }
        }
    }
}

