package com.example.mjphotoshop

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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

class HistoryFragment : Fragment() {
    var userID: String? = null
    var recyclerView: RecyclerView? = null

    //var imgde: ImageButton? = null
    private val client = OkHttpClient()
    private val data = ArrayList<Data>()
    var bookingID = ArrayList<String>()
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_history, container, false)

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
        val url: String = getString(R.string.root_url) + getString(R.string.viewhistory_url) +
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
                                "", "", "", "ถ่ายสำเร็จ")
                        val status2 = arrayOf(
                            "", "ชำระเงินครบแล้ว")
                        for (i in 0 until res.length()) {
                            val item: JSONObject = res.getJSONObject(i)
                            data.add(Data(
                                    item.getString("bookingID"),
                                    item.getString("firstname"),
                                    item.getString("lastname"),
                                    item.getString("queues"),
                                    item.getString("date"),
                                    item.getString("formatname"),
                                    item.getString("duration"),
                                    item.getString("receivingname"),
                                    status[item.getInt("status")],
                                    status2[item.getInt("statuspayment")]

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
            var bookingID: String,
            var firstname: String,
            var lastname: String,
            var queues: String,
            var date: String,
            var formatname: String,
            var duration: String,
            var receivingname: String,
            var status: String,
            var statuspayment: String
    )

    internal inner class DataAdapter(private val list: List<Data>) :
            RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            Log.d("txt", "x2")
            val view: View = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_history,
                    parent, false
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Log.d("txt", "x3")
            val data = list[position]
            holder.data = data
            holder.txtname2.text = data.firstname
            holder.txtlastname2.text = data.lastname
            holder.txtq.text = data.queues
            holder.txtdate.text = data.date
            holder.txtformat.text = data.formatname
            holder.txttime.text = data.duration
            holder.txtformat3.text = data.receivingname
            holder.txtstaus.text = data.status
            holder.txtpa.text = data.statuspayment

            bookingID.add(data.bookingID)
            /*holder.imgde.setOnClickListener {
                deletebooking(data.bookingID)
                showDataList()

            }*/


        }

        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
                RecyclerView.ViewHolder(itemView) {

            var data: Data? = null
            var txtname2: TextView = itemView.findViewById(R.id.txtname2)
            var txtlastname2: TextView = itemView.findViewById(R.id.txtlastname2)
            var txtq: TextView = itemView.findViewById(R.id.txtq)
            var txtdate: TextView = itemView.findViewById(R.id.txtdate)
            var txtformat: TextView = itemView.findViewById(R.id.txtformat)
            var txttime: TextView = itemView.findViewById(R.id.txttime)
            var txtformat3: TextView = itemView.findViewById(R.id.txtformat3)
            var txtstaus: TextView = itemView.findViewById(R.id.txtstaus)
            var txtpa: TextView = itemView.findViewById(R.id.txtpa)
            //var imgde: ImageButton = itemView.findViewById(R.id.imgde)


        }
    }

    /*private fun deletebooking(bookingID: String?) {
        var url: String = getString(R.string.root_url) + getString(R.string.deletebooking_url) + bookingID
        Log.d("de", url)
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
                .url(url)
                .delete()
                .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                Toast.makeText(context, "ลบประวัติแล้ว", Toast.LENGTH_LONG).show()
                try {

                    val data = JSONObject(response.body!!.string())
                    if (data.length() > 0) {
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
    }*/

}