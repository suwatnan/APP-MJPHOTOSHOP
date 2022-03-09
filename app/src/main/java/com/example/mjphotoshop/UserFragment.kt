package com.example.mjphotoshop

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class UserFragment : Fragment() {
    var imgback:ImageView? = null
    var imguser:ImageView? = null
    var imgde:ImageView? = null
    var imgre:ImageView? = null
    var imgre2:ImageView? = null
    var imghis:ImageView? = null
    var txt22:TextView? = null
    var txtsetting:TextView? = null
   // var imglogout:ImageView? = null
    var userID:String?=null;
    var txtusername:TextView? = null
    var txtemail:TextView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_user, container, false)

        val sharedPrefer = requireContext().getSharedPreferences(
                LoginActivity().appPreference, Context.MODE_PRIVATE)
        userID = sharedPrefer?.getString(LoginActivity().userIdPreference, null)

        //imageView21 = root.findViewById(R.id.imageView21)
        imgre2 = root.findViewById(R.id.imgre2)
        imgre2?.setOnClickListener {
            val fm = Receive2Fragment()
            val fragmentTransaction = requireActivity().
            supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, fm)
            fragmentTransaction.commit()
        }
        imgre = root.findViewById(R.id.imgre)
        imgre?.setOnClickListener {
            val fm = ReceiveFragment()
            val fragmentTransaction = requireActivity().
            supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, fm)
            fragmentTransaction.commit()
        }
        imgde = root.findViewById(R.id.imgde)
        imgde?.setOnClickListener {
            val fm = DeliveryFragment()
            val fragmentTransaction = requireActivity().
            supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, fm)
            fragmentTransaction.commit()
        }
        imguser = root.findViewById(R.id.imguser)
        imgback = root.findViewById(R.id.imgback)
        imgback?.setOnClickListener {
            val fm = History2Fragment()
            val fragmentTransaction = requireActivity().
            supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, fm)
            fragmentTransaction.commit()
        }
        //imglogout = root.findViewById(R.id.imglogout)
        txt22 = root.findViewById(R.id.textView22)
        imghis = root.findViewById(R.id.imghis)
        imghis?.setOnClickListener {
            val fm = HistoryFragment()
            val fragmentTransaction = requireActivity().
            supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, fm)
            fragmentTransaction.commit()
        }
        txtsetting = root.findViewById(R.id.txtsetting)
        txtusername = root.findViewById(R.id.txtusername)
        txtemail = root.findViewById(R.id.txtemail)

        /*imglogout?.setOnClickListener {
            val sharePrefer = requireContext().getSharedPreferences(
                    LoginActivity().appPreference,
                    Context.MODE_PRIVATE
            )
            val editor = sharePrefer.edit()
            editor.clear() // ทำการลบข้อมูลทั้งหมดจาก preferences

            editor.commit() // ยืนยันการแก้ไข preferences

            //return to login page
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)

        }*/

        txt22?.setOnClickListener {
            val sharePrefer = requireContext().getSharedPreferences(
                LoginActivity().appPreference,
                Context.MODE_PRIVATE
            )
            val editor = sharePrefer.edit()
            editor.clear() // ทำการลบข้อมูลทั้งหมดจาก preferences

            editor.commit() // ยืนยันการแก้ไข preferences

            //return to login page
            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)

        }

        /*imageView21?.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("userID", userID)
            val fm = SettingUserFragment()
            fm.arguments = bundle;
            val fragmentTransaction = requireActivity().
            supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, fm)
            fragmentTransaction.commit()

        }*/

        txtsetting?.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("userID", userID)
            val fm = SettingUserFragment()
            fm.arguments = bundle;
            val fragmentTransaction = requireActivity().
            supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.replace(R.id.nav_host_fragment, fm)
            fragmentTransaction.commit()
        }

        viewUser(userID)

        return root
    }

    private fun viewUser(userID: String?) {
        var url: String = getString(R.string.root_url) + getString(R.string.user_url) + userID
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
                .url(url)
                .get()
                .build()

        try {
            Log.d("log", "x1")
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val data = JSONObject(response.body!!.string())
                    if (data.length() > 0) {

                        var imgUrl = getString(R.string.root_url) +
                                getString(R.string.user_image_url) +
                                data.getString("imageFileName")

                        Picasso.get().load(imgUrl).into(imguser)
                        txtusername?.text = data.getString("username")
                        txtemail?.text = data.getString("email")



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
//test
}