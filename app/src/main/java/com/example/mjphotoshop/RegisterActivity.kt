package com.example.mjphotoshop

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class RegisterActivity : AppCompatActivity() {

    var editTextName: EditText? = null
    var editTextPassword: EditText? = null
    var editTextFname: EditText? = null
    var editTextLname: EditText? = null
    var editTextEmail: EditText? = null
    var editTextAddress: EditText? = null
    var editTextPhone: EditText? = null
    var btnok: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val textViewRegister = findViewById<TextView>(R.id.textView5)
        textViewRegister.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        //find to widgets on a layout
        editTextName = findViewById(R.id.editTextName)
        editTextPassword = findViewById(R.id.txtPassword1)
        editTextFname = findViewById(R.id.editTextFname)
        editTextLname = findViewById(R.id.editTextLname)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextAddress = findViewById(R.id.editTextAddress)
        editTextPhone = findViewById(R.id.editTextPhone)
        btnok = findViewById(R.id.btnok)

        btnok!!.setOnClickListener {
            register()
        }

    }

    private fun register()
    {
        var url: String = getString(R.string.root_url) + getString(R.string.user_url)
        val okHttpClient = OkHttpClient()
        val formBody: RequestBody = FormBody.Builder()
                .add("username", editTextName?.text.toString())
                .add("password", editTextPassword?.text.toString())
                .add("firstname", editTextFname?.text.toString())
                .add("lastname", editTextLname?.text.toString())
                .add("address",  editTextAddress?.text.toString())
                .add("email", editTextEmail?.text.toString())
                .add("phone", editTextPhone?.text.toString())
                .add("usertypeID", "1")
                .build()

        val request: Request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val data = JSONObject(response.body!!.string())
                    if (data.length() > 0) {
                        Toast.makeText(this, "สมัครสมาชิกเรียบร้อยแล้ว", Toast.LENGTH_LONG).show()
                        val intent = Intent(applicationContext, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else {
                response.code
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}