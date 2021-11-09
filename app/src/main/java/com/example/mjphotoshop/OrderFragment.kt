package com.example.mjphotoshop

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
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


class OrderFragment : Fragment() {
    var userID: String? = null
    var recyclerView: RecyclerView? = null
    var imgdetail:ImageButton? = null
    private val client = OkHttpClient()
    private val data = ArrayList<Data>()
    var bookingID = ArrayList<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root =  inflater.inflate(R.layout.fragment_order, container, false)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val sharedPrefer = requireContext().getSharedPreferences(
                LoginActivity().appPreference, Context.MODE_PRIVATE)
        userID = sharedPrefer?.getString(LoginActivity().userIdPreference, null)
        val bundle =this.arguments


        //List data
        recyclerView = root.findViewById(R.id.recyclerView)
        //imgdetail = root.findViewById(R.id.imgdetail)
        showDataList()
        return root
    }

    private fun showDataList() {
        Log.d("txt","x1")
        val data = ArrayList<Data>()
        val url: String = getString(R.string.root_url) + getString(R.string.viewbooking_url) +
                userID.toString()

        Log.d("txt",url)
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
                                "คลิกชำระค่ามัดจำ", "ตรวจใบเสร็จ","รอดำเนินการ","","ชำระเงินไม่สำเร็จ")
                        val status2 = arrayOf(
                                "ชำระหลังการถ่าย","ชำระหลังการถ่าย")
                        for (i in 0 until res.length()) {
                            val item: JSONObject = res.getJSONObject(i)
                            data.add( Data(
                                    item.getString("bookingID"),
                                    item.getString("firstname"),
                                    item.getString("lastname"),
                                    item.getString("date"),
                                    item.getString("receivingname"),
                                    item.getString("formatname"),
                                    item.getString("duration"),
                                    item.getString("queues"),
                                    item.getString("status"),
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
                } catch (e: JSONException) { e.printStackTrace() }
            } else { response.code }
        } catch (e: IOException) { e.printStackTrace() }
    }

    internal class Data(
            var bookingID: String,
            var bookingfirstname: String,
            var bookinglastname: String,
            var bookingDate: String,
            var bookingreceivingname: String,
            var bookingformat: String,
            var bookingtime: String,
            var bookingqueues: String,
            var bookingstatus: String,
            var bookingstatus1: String,
            var statuspayment: String

    )

    internal inner class DataAdapter(private val list: List<Data>) :
            RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            Log.d("txt","x2")
            val view: View = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_order,
                    parent, false
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Log.d("txt","x3")
            val data = list[position]
            holder.data = data
            holder.txtDate.text = data.bookingDate
            holder.txtformat2.text = data.bookingreceivingname
            holder.txtName.text = data.bookingfirstname
            holder.txtLastname.text = data.bookinglastname
            holder.txtFormat.text = data.bookingformat
            holder.txtTime.text = data.bookingtime
            holder.txtQueues.text = data.bookingqueues
            holder.txtStatus.text = data.bookingstatus1
            holder.txtpricebook.text = data.statuspayment
            bookingID.add(data.bookingID)
            if(data.bookingstatus == "0") {
                holder.txtStatus.visibility = View.VISIBLE
                holder.txtStatus.setTextColor(Color.parseColor("#F60303"));
                holder.txtStatus.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("idbooking",data.bookingID)
                    val fm = Payment2Fragment()
                    fm.arguments = bundle;
                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.replace(R.id.nav_host_fragment, fm)
                    fragmentTransaction.commit()

                }
            }

            if(data.bookingstatus == "4") {
                holder.txtStatus.visibility = View.VISIBLE
                holder.txtStatus.setTextColor(Color.parseColor("#F60303"));
                holder.txtStatus.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("idbooking",data.bookingID)
                    val fm = Payment3Fragment()
                    fm.arguments = bundle;
                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.replace(R.id.nav_host_fragment, fm)
                    fragmentTransaction.commit()

                }
            }

            //holder.btncon.visibility = View.GONE
            /*if(data.bookingstatus == "1"){
                holder.btncon.visibility = View.VISIBLE
            }
            holder.btncon.setOnClickListener {
                up(data.bookingID)
            }*/

            if(data.bookingstatus == "0"){
                holder.txtdelete.visibility = View.GONE
            }else if(data.bookingstatus == "1"){
                holder.txtdelete.visibility = View.GONE
            }else if(data.bookingstatus == "4"){
                holder.txtdelete.visibility = View.GONE
            }
            else if(data.bookingstatus == "2"){
                holder.txtdelete.visibility = View.VISIBLE
                holder.txtdelete.setOnClickListener {
                    deletebooking(data.bookingID)
                    showDataList()
                }
            }


        }

        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
                RecyclerView.ViewHolder(itemView) {

            var data: Data? = null
            var txtDate: TextView = itemView.findViewById(R.id.txtsize6)
            var txtName: TextView = itemView.findViewById(R.id.txtname6)
            var txtFormat: TextView = itemView.findViewById(R.id.txtprice6)
            var txtTime: TextView = itemView.findViewById(R.id.txtTime)
            var txtdelete : TextView= itemView.findViewById(R.id.txtdelete)
            var txtQueues: TextView = itemView.findViewById(R.id.txtproduct6)
            //var btncon: TextView = itemView.findViewById(R.id.btncon)
            var txtStatus: TextView = itemView.findViewById(R.id.txtStatus)
            var txtLastname: TextView = itemView.findViewById(R.id.txtlastname6)
            var txtformat2: TextView = itemView.findViewById(R.id.txtformat2)
            var txtpricebook: TextView = itemView.findViewById(R.id.txtpricebook)

        }
    }

    private fun deletebooking(bookingID: String?)
    {
        var url: String = getString(R.string.root_url) + getString(R.string.deletebooking_url) + bookingID
        Log.d("de",url)
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
                .url(url)
                .delete()
                .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                Toast.makeText(context, "ยกเลิกการจองแล้ว", Toast.LENGTH_LONG).show()
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
    }

    private fun up(bookingID: String)
    {
        var url: String = getString(R.string.root_url) + getString(R.string.up_url)+bookingID
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