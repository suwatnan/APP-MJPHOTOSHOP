package com.example.mjphotoshop

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.app.DatePickerDialog.OnDateSetListener
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class BookingFragment : Fragment() {
    var editTextDate: EditText? = null
    var editTextNote: EditText? = null
    var spinnerFormat: Spinner? = null
    var spinnerReceiving: Spinner? = null
    var spinnerTime: Spinner? = null
    var btnaddbooking: Button? = null
    var q: Button? = null
    var userID: String? = null
    private var format = ArrayList<Format>()
    var formatID = ""

    private var time = ArrayList<Time>()
    var timeID = ""

    private var receiving = ArrayList<Receiving>()
    var receivingID = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var root = inflater.inflate(R.layout.fragment_booking, container, false)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val sharedPrefer = requireContext().getSharedPreferences(
                LoginActivity().appPreference, Context.MODE_PRIVATE)
        userID = sharedPrefer?.getString(LoginActivity().userIdPreference, null)

        q = root.findViewById(R.id.q)
        editTextDate = root.findViewById(R.id.editTextDate)
        spinnerFormat = root.findViewById(R.id.spinnerFormat)
        spinnerReceiving = root.findViewById(R.id.spinnerReceiving)
        spinnerTime = root.findViewById(R.id.spinnerTime)
        editTextNote = root.findViewById(R.id.editTextNote)
        btnaddbooking = root.findViewById(R.id.btnaddbooking)

        q?.setOnClickListener {
            q(editTextDate?.text.toString(),timeID.toString())
        }
        btnaddbooking?.setOnClickListener {
            addbooking()
            val fragmentTransaction = requireActivity().
            supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment, OrderFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

        }


        listFormat()
        listTime()
        listReceiving()

        //Date
        val myCalendar = Calendar.getInstance(Locale("th", "TH"))
        val date = OnDateSetListener { view, year, monthOfYear, dayOfMonth -> // TODO Auto-generated method stub
           myCalendar[Calendar.YEAR] = year
            myCalendar[Calendar.MONTH] = monthOfYear
            myCalendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            val myFormat = "yyyy-MM-dd" //In which you need put here
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            editTextDate?.setText(sdf.format(myCalendar.time))
        }
        editTextDate!!.setOnClickListener {
            spinnerTime?.isEnabled = true
            val sdf1 = SimpleDateFormat("dd/M/yyyy")
            val currentDate = sdf1.format(Date())
            val dpd = DatePickerDialog(requireContext(), date, myCalendar[Calendar.YEAR], myCalendar[Calendar.MONTH], myCalendar[Calendar.DAY_OF_MONTH])
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val d = sdf.parse(currentDate)
            dpd.datePicker.minDate = d.time
            dpd.show()

        }

        //Receiving
        val adapterReceiving = ArrayAdapter(
                requireContext(),android.R.layout.simple_spinner_item, receiving)
        spinnerReceiving?.adapter = adapterReceiving
        //spinnerProvince?.setSelection(adapterProvince.getPosition(province[0]))
        spinnerReceiving?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val receiving = spinnerReceiving!!.selectedItem as Receiving
                receivingID = receiving.receivingID
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        //Format
        val adapterFormat = ArrayAdapter(
            requireContext(),android.R.layout.simple_spinner_item, format)
        spinnerFormat?.adapter = adapterFormat
        //spinnerProvince?.setSelection(adapterProvince.getPosition(province[0]))
        spinnerFormat?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val format = spinnerFormat!!.selectedItem as Format
                formatID = format.formatID
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        //Time
        val adapterTime = ArrayAdapter(
                requireContext(),android.R.layout.simple_spinner_item, time)
        spinnerTime?.adapter = adapterTime
        //spinnerProvince?.setSelection(adapterProvince.getPosition(province[0]))
        spinnerTime?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val time = spinnerTime!!.selectedItem as Time
                timeID = time.timeID
                val sdf1 = SimpleDateFormat("dd/M/yyyy")
                val currentDate = sdf1.format(Date())

                if(currentDate == editTextDate?.text.toString()){
                    spinnerTime?.isEnabled = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        return root
    }

    private fun listReceiving() {
        receiving.add(Receiving("0", "-เลือก-"))
        val urlReceiving: String = getString(R.string.root_url) + getString(R.string.receiving_url)
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder().url(urlReceiving).get().build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val res = JSONArray(response.body!!.string())
                    if (res.length() > 0) {
                        for (i in 0 until res.length()) {
                            val item: JSONObject = res.getJSONObject(i)
                            receiving.add(Receiving(
                                    item.getString("receivingID"),
                                    item.getString("receivingname")))
                        }
                    }
                } catch (e: JSONException) { e.printStackTrace() }
            } else { response.code }
        } catch (e: IOException) { e.printStackTrace() }
    }


    private fun listFormat() {
        format.add(Format("0", "-เลือก-"))
        val urlFormat: String = getString(R.string.root_url) + getString(R.string.formatss_url)
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder().url(urlFormat).get().build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val res = JSONArray(response.body!!.string())
                    if (res.length() > 0) {
                        for (i in 0 until res.length()) {
                            val item: JSONObject = res.getJSONObject(i)
                            format.add(Format(
                                item.getString("formatID"),
                                item.getString("formatname")))
                        }
                    }
                } catch (e: JSONException) { e.printStackTrace() }
            } else { response.code }
        } catch (e: IOException) { e.printStackTrace() }
    }

    private fun listTime() {
        time.add(Time("0", "-เลือก-"))
        val urlTime: String = getString(R.string.root_url) + getString(R.string.timess_url)
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder().url(urlTime).get().build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val res = JSONArray(response.body!!.string())
                    if (res.length() > 0) {
                        for (i in 0 until res.length()) {
                            val item: JSONObject = res.getJSONObject(i)
                            time.add(Time(
                                    item.getString("timeID"),
                                    item.getString("duration")))
                        }
                    }
                } catch (e: JSONException) { e.printStackTrace() }
            } else { response.code }
        } catch (e: IOException) { e.printStackTrace() }
    }

    internal class Format(var formatID: String, var formatname: String) {
        override fun toString(): String {
            return formatname
        }

    }

    internal class Time(var timeID: String, var duration: String) {
        override fun toString(): String {
            return duration
        }

    }
    internal class Receiving(var receivingID: String, var receivingname: String) {
        override fun toString(): String {
            return receivingname
        }

    }


    private fun addbooking()
    {
        var url: String = getString(R.string.root_url) + getString(R.string.booking_url)
        Log.d("addbooking=",url)
        val okHttpClient = OkHttpClient()
        val formBody: RequestBody = FormBody.Builder()
            .add("date", editTextDate?.text.toString())
            .add("formatID", formatID)
            .add("timeID", timeID)
            .add("receivingID", receivingID)
            .add("note", editTextNote?.text.toString())
            .add("userID", userID.toString())
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
                        Toast.makeText(context, "การจองสำเร็จ", Toast.LENGTH_LONG).show()
                        response.code

                    }
                    else{
                        Toast.makeText(context, "ไม่รับคิวเวลานี้แล้ว", Toast.LENGTH_LONG).show()
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

    private fun q(date: String,time:String) {
        var url: String = getString(R.string.root_url) + getString(R.string.qq_url)+"?timeID="+time+"&date="+date
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder()
            .url(url)
            .get()
            .build()

        try {
            Log.d("log",url)
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val data = JSONObject(response.body!!.string())
                    if (data.length() > 0) {
                        Toast.makeText(context, "คิวตอนนี้ " + data.getString("queues1"), Toast.LENGTH_LONG).show()
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