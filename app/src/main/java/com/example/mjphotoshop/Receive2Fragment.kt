package com.example.mjphotoshop

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.DecimalFormat
import java.text.NumberFormat


class Receive2Fragment : Fragment() {

    var userID: String? = null
    var recyclerView: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root =  inflater.inflate(R.layout.fragment_receive2, container, false)

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
        val url: String = getString(R.string.root_url) + getString(R.string.viewhistory5_url) +
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
                        for (i in 0 until res.length()) {
                            val item: JSONObject = res.getJSONObject(i)
                            data.add(Data(
                                item.getString("printphotoID"),
                                item.getString("productname"),
                                item.getString("size"),
                                item.getString("papername"),
                                item.getString("totalprice"),
                                item.getString("formatprint")


                            )
                            )
                        }
                        recyclerView!!.adapter = DataAdapter(data)
                        Toast.makeText(context, "รับสินค้าแล้ว", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "รับสินค้าแล้ว", Toast.LENGTH_LONG).show()
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
        var productname: String,
        var size: String,
        var papername: String,
        var totalprice: String,
        var formatprint: String


    )

    internal inner class DataAdapter(private val list: List<Data>) :
        RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            Log.d("txt", "x2")
            val view: View = LayoutInflater.from(parent.context).inflate(
                R.layout.item_receive2,
                parent, false
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Log.d("txt", "x3")
            val data = list[position]
            holder.data = data
            holder.txt001.text = data.productname
            holder.txt002.text = data.size
            holder.txt003.text = data.papername
            holder.txt004.text = data.totalprice
            holder.txt005.text = data.formatprint


        }

        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {

            var data: Data? = null
            var txt001: TextView = itemView.findViewById(R.id.txt001)
            var txt002: TextView = itemView.findViewById(R.id.txt002)
            var txt003: TextView = itemView.findViewById(R.id.txt003)
            var txt004: TextView = itemView.findViewById(R.id.txt004)
            var txt005: TextView = itemView.findViewById(R.id.txt005)




        }
    }

}