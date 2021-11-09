package com.example.mjphotoshop

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.DecimalFormat
import java.text.NumberFormat

class ReceiveFragment : Fragment() {
    var userID: String? = null
    var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root =  inflater.inflate(R.layout.fragment_receive, container, false)

        val sharedPrefer = requireContext().getSharedPreferences(
            LoginActivity().appPreference, Context.MODE_PRIVATE)
        userID = sharedPrefer?.getString(LoginActivity().userIdPreference, null)
        //List data
        recyclerView = root.findViewById(R.id.recyclerView)
        showDataList()

        return root
    }

    private fun showDataList() {
        Log.d("txt", "x1")
        val data = ArrayList<Data>()
        val url: String = getString(R.string.root_url) + getString(R.string.viewhistory4_url) +
                userID.toString()

        Log.d("txt", url)
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder().url(url).get().build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val res = JSONArray(response.body!!.string())
                    if (res.length() > 0) {
                        val formatter: NumberFormat = DecimalFormat("#,###")
                        val status = arrayOf(
                            "", "","","ชำระเงินแล้ว")
                        for (i in 0 until res.length()) {
                            val item: JSONObject = res.getJSONObject(i)
                            data.add(Data(
                                item.getString("printphotoID"),
                                item.getString("firstname"),
                                item.getString("lastname"),
                                item.getString("productname"),
                                item.getString("size"),
                                item.getString("papername"),
                                item.getString("totalprice"),
                                item.getString("formatprint"),
                                status[item.getInt("status")]


                            )
                            )
                        }
                        recyclerView!!.adapter = DataAdapter(data)
                    } else {
                        Toast.makeText(context, "ไม่สามารถแสดงข้อมูลได้", Toast.LENGTH_LONG).show()
                        recyclerView!!.adapter = DataAdapter(data)
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

    internal class Data(
        var printphotoID: String,
        var firstname: String,
        var lastname: String,
        var productname: String,
        var size: String,
        var papername: String,
        var totalprice: String,
        var formatprint: String,
        var status: String


    )

    internal inner class DataAdapter(private val list: List<Data>) :
        RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            Log.d("txt", "x2")
            val view: View = LayoutInflater.from(parent.context).inflate(
                R.layout.item_receive,
                parent, false
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Log.d("txt", "x3")
            val data = list[position]
            holder.data = data
            holder.txt01.text = data.firstname
            holder.txt02.text = data.lastname
            holder.txt03.text = data.productname
            holder.txt04.text = data.size
            holder.txt05.text = data.papername
            holder.txt06.text = data.totalprice
            holder.txt07.text = data.formatprint
            holder.txt08.text = data.status
            holder.btnup2.setOnClickListener {
                up2(data.printphotoID)
                val fm = Receive2Fragment()
                val fragmentTransaction = requireActivity().
                supportFragmentManager.beginTransaction()
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.replace(R.id.nav_host_fragment, fm)
                fragmentTransaction.commit()
            }

        }

        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {

            var data: Data? = null
            var txt01: TextView = itemView.findViewById(R.id.txt01)
            var txt02: TextView = itemView.findViewById(R.id.txt02)
            var txt03: TextView = itemView.findViewById(R.id.txt03)
            var txt04: TextView = itemView.findViewById(R.id.txt04)
            var txt05: TextView = itemView.findViewById(R.id.txt05)
            var txt06: TextView = itemView.findViewById(R.id.txt06)
            var txt07: TextView = itemView.findViewById(R.id.txt07)
            var txt08: TextView = itemView.findViewById(R.id.txt08)
            var btnup2: Button = itemView.findViewById(R.id.btnup2)



        }
    }

    private fun up2(printphotoID: String)
    {
        var url: String = getString(R.string.root_url) + getString(R.string.up2_url)+printphotoID
        val okHttpClient = OkHttpClient()
        val formBody: RequestBody = FormBody.Builder()
                .build()
        val request: Request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    showDataList()
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