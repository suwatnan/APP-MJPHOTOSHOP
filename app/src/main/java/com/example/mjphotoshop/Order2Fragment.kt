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


class Order2Fragment : Fragment() {
    var userID: String? = null
    var recyclerView: RecyclerView? = null
    var y: String? = null
    private val client = OkHttpClient()
    private val data = ArrayList<Data>()
    var printphotoID = ArrayList<String>()
    var paperID = ArrayList<String>()
    var productID = ArrayList<String>()
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var root =  inflater.inflate(R.layout.fragment_order2, container, false)


        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val bundle =this.arguments



        val sharedPrefer = requireContext().getSharedPreferences(
                LoginActivity().appPreference, Context.MODE_PRIVATE)
        userID = sharedPrefer?.getString(LoginActivity().userIdPreference, null)
        //List data
        recyclerView = root.findViewById(R.id.recyclerView)
        showDataList()
        return root
    }

    private fun showDataList() {
        Log.d("txt","x1")
        val data = ArrayList<Data>()
        val url: String = getString(R.string.root_url) + getString(R.string.viewprintphotodetail_url) +
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
                                "รอการชำระเงิน", "ตรวจใบเสร็จ" ,"","","","ชำระเงินไม่สำเร็จ")
                        for (i in 0 until res.length()) {
                            val item: JSONObject = res.getJSONObject(i)
                            data.add( Data(

                                    item.getString("printphotoID"),
                                    item.getString("productID"),
                                    item.getString("paperID"),
                                    item.getString("firstname"),
                                    item.getString("lastname"),
                                    item.getString("productname"),
                                    item.getString("size"),
                                    item.getString("papername"),
                                    item.getString("totalprice"),
                                    item.getString("formatprint"),
                                    item.getString("status"),
                                    status[item.getInt("status")]
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
            var printphotoID: String,
            var productID: String,
            var paperID: String,
            var firstname: String,
            var lastname: String,
            var productname: String,
            var size: String,
            var papername: String,
            var totalprice: String,
            var formatprint: String,
            var status: String,
            var status2: String


    )

    internal inner class DataAdapter(private val list: List<Data>) :
            RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            Log.d("txt","x2")
            val view: View = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_order2,
                    parent, false
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Log.d("txt","x3")
            val data = list[position]
            holder.data = data
            holder.txtname5.text = data.firstname
            holder.txtlastname5.text = data.lastname
            holder.txtproduct5.text = data.productname
            holder.txtsize5.text = data.size
            holder.txtpaper5.text = data.papername
            holder.txtprice5.text = data.totalprice
            holder.txtformatprint.text = data.formatprint
            holder.txtstatus5.text = data.status2

            printphotoID.add(data.printphotoID)
            paperID.add(data.paperID)
            productID.add(data.productID)
            if(data.status == "0"){
                holder.txtpayment.visibility = View.VISIBLE
                holder.txtpayment.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("price",data.totalprice)
                    bundle.putString("id",data.printphotoID)
                    bundle.putString("idproduct",data.productID)
                    bundle.putString("idpaper",data.paperID)
                    val fm = PaymentFragment()
                    fm.arguments = bundle;
                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.replace(R.id.nav_host_fragment, fm)
                    fragmentTransaction.commit()
                }
            }else if (data.status == "1"){
                holder.txtpayment.visibility = View.GONE
            }else if(data.status == "5"){
                holder.txtpayment.visibility = View.GONE
            }

            if(data.status == "5") {
                holder.txtstatus5.visibility = View.VISIBLE
                holder.txtstatus5.setTextColor(Color.parseColor("#F60303"));
                holder.txtstatus5.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("price",data.totalprice)
                    bundle.putString("id",data.printphotoID)
                    val fm = Payment4Fragment()
                    fm.arguments = bundle;
                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.replace(R.id.nav_host_fragment, fm)
                    fragmentTransaction.commit()
                }
            }

        }

        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
                RecyclerView.ViewHolder(itemView) {

            var data: Data? = null
            var txtname5: TextView = itemView.findViewById(R.id.txtname5)
            var txtlastname5: TextView = itemView.findViewById(R.id.txtlastname5)
            var txtproduct5: TextView = itemView.findViewById(R.id.txtproduct5)
            var txtsize5: TextView = itemView.findViewById(R.id.txtsize5)
            var txtpaper5: TextView = itemView.findViewById(R.id.txtpaper5)
            var txtprice5: TextView = itemView.findViewById(R.id.txtprice5)
            var txtformatprint: TextView = itemView.findViewById(R.id.txtformatprint)
            var txtstatus5: TextView = itemView.findViewById(R.id.txtstatus5)
            var txtpayment: TextView = itemView.findViewById(R.id.txtpayment)


        }
    }


}