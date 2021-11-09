package com.example.mjphotoshop

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    val appPreference:String = "appPrefer"
    val userIdPreference:String = "userIdPref"
    val usernamePreference:String = "usernamePref"
    val userTypePreference:String = "userTypePref"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)


        val textViewRegister = findViewById<TextView>(R.id.textView)
        textViewRegister.setOnClickListener{
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        val textView2 = findViewById<TextView>(R.id.textView2)
        textView2.setOnClickListener{
            val intent = Intent(applicationContext, NeedhelpActivity::class.java)
            startActivity(intent)
            finish()
        }

        val editUsername = findViewById<EditText>(R.id.editTextName)
        val editPassword = findViewById<EditText>(R.id.txtPassword1)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {

            val url = getString(R.string.root_url) + getString(R.string.login_url)
            val okHttpClient = OkHttpClient()

            val formBody: RequestBody = FormBody.Builder()
                    .add("username", editUsername.text.toString())
                    .add("password", editPassword.text.toString())
                    .build()
            val request: Request = Request.Builder()
                    .url(url)
                    .post(formBody)
                    .build()
            try {
                val response = okHttpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    try {
                        val obj = JSONObject(response.body!!.string())
                        val userID = obj["userID"].toString()
                        val username = obj["username"].toString()
                        val userTypeID = obj["usertypeID"].toString()


                        //Create shared preference to store user data
                        val sharedPrefer: SharedPreferences =
                                getSharedPreferences(appPreference, Context.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sharedPrefer.edit()

                        editor.putString(userIdPreference, userID)
                        editor.putString(usernamePreference, username)
                        editor.putString(userTypePreference, userTypeID)
                        editor.commit()

                        //return to login page
                        if (userTypeID == "1")
                        {
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else if (userTypeID == "2")
                        {
                            val intent = Intent(applicationContext, AdminActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else if (userTypeID == "3")
                        {
                            val intent = Intent(applicationContext, Admin2Activity::class.java)
                            startActivity(intent)
                            finish()
                        }

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    response.code
                    Toast.makeText(applicationContext, "ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง", Toast.LENGTH_LONG).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        val sharedPrefer: SharedPreferences =
                getSharedPreferences(appPreference, Context.MODE_PRIVATE)
        val usertype = sharedPrefer?.getString(userTypePreference, null)

        //if (sharedPrefer.contains(usernamePreference))
        if (usertype=="0") {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            finish()
        }
        else if(usertype=="1")
        {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            finish()
        }
        super.onResume()
    }

}