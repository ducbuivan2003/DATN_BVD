package com.example.datn

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.datn.GlobalData.AuthMsg
import com.example.datn.GlobalData.AuthState
import com.example.datn.GlobalData.activeTaskbar
import com.example.datn.GlobalData.userId
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


var auth = FirebaseAuth.getInstance()

fun register_user(context: Context, email: String?, password: String?) {  //hàm đăng ký tài khoản
    email?.let { mail ->
        password?.let { pass ->
            auth.createUserWithEmailAndPassword(mail, pass)   //Gọi hàm tạo tài khoản của Firebase
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        AuthMsg.value = "Đăng ký thành công"
                        show_toast(context, "Đăng ký thành công: ${user?.email}")

                        // Cập nhật userId sau khi đăng ký thành công
                        userId.value = is_session_active() ?: "default_user"

                        // Chạy đăng nhập & tạo topic trong Coroutine để tránh block UI
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(1000) // Đợi một chút để đảm bảo Firebase cập nhật
                            login_User(mail, pass)  //Tự động đăng nhập luôn tài khoản vừa tạo
                            AuthState.value = is_user_logged_in()

                            delay(1000) // Đợi cập nhật trạng thái
                            Create_user_topic()    //tạo ra một node mới trên Realtime Database trùng với UID
                            show_toast(context, "Create Topic Success")
                            fetch_room()     //Lấy danh sách phòng về
                            activeTaskbar.value="home"

                        }
                    } else {
                        AuthMsg.value = "Lỗi khi đăng ký"
                        show_toast(context, "Lỗi: ${task.exception?.message}")
                    }
                }
        } ?: show_toast(context, "Password không hợp lệ")
    } ?: show_toast(context, "Email không hợp lệ")
}


fun login_User(email: String?, password: String?) {           //Hàm Đăng nhập
    auth.signInWithEmailAndPassword(email!!, password!!)
        .addOnCompleteListener { task: Task<AuthResult?> ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                AuthMsg.value = "Đăng nhập thành công"
                Log.d("Login", "Đăng nhập thành công: " + user!!.email)

                // Chạy coroutine để cập nhật userId liên tục cho đến khi khác "default_user"
                CoroutineScope(Dispatchers.IO).launch {
                    while (userId.value == "default_user") {
                        userId.value = is_session_active() ?: "default_user"
                        delay(500) // Chờ 500ms trước khi kiểm tra lại
                    }
                    fetch_room() // Gọi hàm sau khi userId đã cập nhật
                }
            } else {
                AuthMsg.value = "Đăng nhập thất bại, xem lại email/mật khẩu"
                Log.e("Login", "Lỗi: " + task.exception!!.message)
            }
        }
}

fun logout_User() {
    // 1. Ra lệnh cho Firebase Auth hủy phiên đăng nhập (Sign Out) của tài khoản hiện tại
    auth.signOut()
    Log.d("Logout", "Người dùng đã đăng xuất thành công")
    // 2. Đưa biến trạng thái userId về giá trị mặc định để chặn quyền truy cập dữ liệu cũ
    userId.value = "default_user"
    // 3. Xóa sạch danh sách phòng ốc đang lưu trong bộ nhớ máy để bảo mật dữ liệu khách cũ
    roomList.clear()
}
fun is_user_logged_in(): Boolean {
    // Nếu auth.currentUser khác null nghĩa là đang có một tài khoản đang active
    return auth.currentUser != null
}
fun is_session_active(): String? {
    // Lấy đối tượng kết nối tới dịch vụ Firebase Auth
    val auth = FirebaseAuth.getInstance()
    // Trả về chuỗi UID định danh duy nhất của tài khoản
    return auth.currentUser?.uid
}
fun get_active_user_email(): String? {
    // Lấy đối tượng kết nối tới dịch vụ Firebase Auth
    val auth = FirebaseAuth.getInstance()
    // Trả về chuỗi Email đăng ký của tài khoản
    return auth.currentUser?.email
}

fun request_password_reset(email: String, context: Context) {
    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Kiểm tra email để đặt lại mật khẩu!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Lỗi: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
}
