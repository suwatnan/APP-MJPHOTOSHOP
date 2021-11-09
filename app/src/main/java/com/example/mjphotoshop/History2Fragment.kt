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

class History2Fragment : Fragment() {
    var userID: String? = null
    var recyclerView: RecyclerView? = null

    //var imgde: ImageButton? = null
    private val client = OkHttpClient()
    private val data = ArrayList<Data>()
    var printphotoID = ArrayList<String>()
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_history2, container, false)

        val sharedPrefer = requireContext().getSharedPreferences(
                LoginActivity().appPreference, Context.MODE_PRIVATE)
        userID = sharedPrefer?.getString(LoginActivity().userIdPreference, null)
        //List data
        recyclerView = root.findViewById(R.id.recyclerView)
        showDataList()

        //imgde = root.findViewById(R.id.imgde)

        return root
    }

    private fun showDataList() {
        Log.d("txt", "x1")
        val data = ArrayList<Data>()
        val url: String = getString(R.string.root_url) + getString(R.string.viewhistory2_url) +
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
                                "", "", "กำลังจัดส่ง", "สินค้าถึงแล้ว", "รับสินค้าแล้ว","กำลังจัดส่ง")
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
                    R.layout.item_history2,
                    parent, false
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Log.d("txt", "x3")
            val data = list[position]
            holder.data = data
            holder.txtName.text = data.firstname
            holder.txtlastname.text = data.lastname
            holder.txtproduct.text = data.productname
            holder.txtsize.text = data.size
            holder.txtpaper.text = data.papername
            holder.txtprice.text = data.totalprice
            holder.txtformatprint.text = data.formatprint
            holder.txtStatus.text = data.status
        }

        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
                RecyclerView.ViewHolder(itemView) {

            var data: Data? = null
            var txtName: TextView = itemView.findViewById(R.id.txtname6)
            var txtlastname: TextView = itemView.findViewById(R.id.txtlastname6)
            var txtproduct: TextView = itemView.findViewById(R.id.txtproduct6)
            var txtsize: TextView = itemView.findViewById(R.id.txtsize6)
            var txtpaper: TextView = itemView.findViewById(R.id.txtpaper6)
            var txtprice: TextView = itemView.findViewById(R.id.txtprice6)
            var txtformatprint: TextView = itemView.findViewById(R.id.textView87)
            var txtStatus: TextView = itemView.findViewById(R.id.txtstaus6)


        }
    }
}