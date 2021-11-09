package com.example.mjphotoshop

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class PrintFragment : Fragment() {


    var imageViewSlipSelection: ImageView? = null
    var imageViewSlip: ImageView? = null
    var file: File? = null
    var imageFilePath: String? = null
    var userID: String? = null
    var spinnerSizeimage: Spinner? = null
    var spinnerPaper: Spinner? = null
    var txtprice: TextView? = null
    var editTextNote: EditText? = null
    var txtproduct: TextView? = null
    var btnphoto: Button? = null
    var txtprice2: TextView? = null
    var txtprice3: TextView? = null
    var recyclerView: RecyclerView? = null
    var productID = ArrayList<String>()
    var proID = ArrayList<String>()
    var totalprice = 0.00f
    private var paper = ArrayList<Paper>()
    var paperID = ""
    var price2 = ""

    private var sizeimage = ArrayList<Sizeimage>()
    var sizeimageID = ""
    var price = ""
    var size = ""


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var root= inflater.inflate(R.layout.fragment_print, container, false)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        val bundle =this.arguments

        val sharedPrefer = requireContext().getSharedPreferences(
                LoginActivity().appPreference, Context.MODE_PRIVATE)
        userID = sharedPrefer?.getString(LoginActivity().userIdPreference, null)

        txtprice = root.findViewById(R.id.txtTime)
        txtprice2 = root.findViewById(R.id.txtprice2)
        txtprice3 = root.findViewById(R.id.txtpricetest2)
        recyclerView = root.findViewById(R.id.recyclerView)
        btnphoto = root.findViewById(R.id.btnphoto)
        btnphoto?.setOnClickListener {
            addphoto()
            val fragmentTransaction = requireActivity().
            supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment, Order2Fragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

        }

        editTextNote = root.findViewById(R.id.editTextNote)

        showDataList("")

        //check permission
        permission()
        //txtproduct = root.findViewById(R.id.txtproduct)
        txtproduct?.setOnClickListener {
            val fragmentTransaction = requireActivity().
            supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.nav_host_fragment, ProductFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()

        }
        spinnerPaper = root.findViewById(R.id.spinnerPaper)
        listPaper()
        spinnerSizeimage = root.findViewById(R.id.spinnerSizeimage)
        listSizeimage()

        imageViewSlipSelection = root.findViewById(R.id.imageViewSlipSelection)
        imageViewSlip = root.findViewById(R.id.imageViewSlip)

        imageViewSlipSelection?.setImageResource(R.drawable.b2)
        imageViewSlipSelection?.setOnClickListener {
            val builder1 = AlertDialog.Builder(requireActivity())
            builder1.setMessage("ท่านต้องการเลือกรูปภาพที่มีอยู่แล้ว หรือ ถ่ายภาพใหม่?")
            builder1.setNegativeButton("เลือกรูปภาพ"
            ) { dialog, id -> //dialog.cancel();
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "image/*"
                startActivityForResult(intent, 100)
                imageViewSlip?.visibility = View.VISIBLE
            }
            builder1.setPositiveButton("ถ่ายภาพ"
            ) { dialog, id -> //dialog.cancel();
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                var imageURI: Uri? = null
                try {
                    imageURI = FileProvider.getUriForFile(requireActivity(),
                            BuildConfig.APPLICATION_ID.toString() + ".provider",
                            createImageFile()!!)
                } catch (e: IOException) { e.printStackTrace() }

                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI)
                startActivityForResult(intent, 200)
                imageViewSlip?.visibility = View.VISIBLE
            }

            val alert11 = builder1.create()
            alert11.show()
        }

        //Paper
        val adapterPaper = ArrayAdapter(
                requireContext(),android.R.layout.simple_spinner_item, paper)
        spinnerPaper?.adapter = adapterPaper
        //spinnerProvince?.setSelection(adapterProvince.getPosition(province[0]))
        spinnerPaper?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val paper = spinnerPaper!!.selectedItem as Paper
                totalprice = 0f
                paperID = paper.paperID
                price2 = paper.price2
                txtprice2?.text = price2.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }


        //Sizeimage
        val adapterSizeimage = ArrayAdapter(
                requireContext(),android.R.layout.simple_spinner_item, sizeimage)
        spinnerSizeimage?.adapter = adapterSizeimage
        //spinnerProvince?.setSelection(adapterProvince.getPosition(province[0]))
        spinnerSizeimage?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                val sizeimage = spinnerSizeimage!!.selectedItem as Sizeimage
                totalprice = 0f
                sizeimageID = sizeimage.sizeimageID
                price = sizeimage.price
                size = sizeimage.size
                Log.d("txt",size)
                if(size == "-เลือกขนาดรูป-"){
                    size = ""
                }
                Log.d("txt2",size)
                txtprice?.text = price.toString()
                showDataList(size)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}

        }

        return root
    }

    private fun addphoto()
    {

        var url: String = getString(R.string.root_url) + getString(R.string.addprintphoto_url)
        val okHttpClient = OkHttpClient()
        val formBody: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("iduser", userID.toString())
                .addFormDataPart("sizeimageID", sizeimageID.toString())
                .addFormDataPart("productID", proID[0])
                .addFormDataPart("note", editTextNote?.text.toString())
                .addFormDataPart("paperID", paperID.toString())
                .addFormDataPart("totalprice",txtprice3?.text.toString())
                .addFormDataPart("file",file?.name, RequestBody.create("application/octet-stream".toMediaTypeOrNull(),file!!))

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
                        Toast.makeText(requireContext(), "อัดรูปสำเร็จ", Toast.LENGTH_LONG).show()
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

    private fun showDataList(size: String) {
        val data = ArrayList<Data>()
        val url: String = getString(R.string.root_url) + getString(R.string.viewproduct_url)+"?size="+size
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder().url(url).get().build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val res = JSONArray(response.body!!.string())
                    if (res.length() > 0) {
                        for (i in 0 until res.length()) {
                            val item: JSONObject = res.getJSONObject(i)
                            data.add(Data(
                                    item.getString("productID"),
                                    item.getString("imageFileName"),
                                    item.getString("productname"),
                                    item.getString("size"),
                                    item.getString("price")
                            )
                            )
                            recyclerView!!.adapter = DataAdapter(data)
                        }
                    } else {
                        Toast.makeText(context, "ไม่สามารถแสดงข้อมูลได้",
                                Toast.LENGTH_LONG).show()
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
            var productID: String, var imageFileName: String, var productname: String,
            var size: String, var price: String
    )

    internal inner class DataAdapter(private val list: List<Data>) :
            RecyclerView.Adapter<DataAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View = LayoutInflater.from(parent.context).inflate(
                    R.layout.item_product,
                    parent, false
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val data = list[position]
            holder.data = data
            var url = getString(R.string.root_url) +
                    getString(R.string.user_image_url) + data.imageFileName
            Picasso.get().load(url).into(holder.imageFileName)
            productID.add(data.productID)
            holder.productname.text = data.productname
            holder.size.text = data.size
            if(proID.contains(data.productID)){
                holder.txtselect.text = "เลือกแล้ว"
                holder.txtselect.setTextColor(Color.parseColor("#006400"));
            }
            holder.price.text = data.price
            holder.txtselect.setOnClickListener {

                proID.clear()
                proID.add(data.productID)
                txtprice3?.text = (price.toFloat() + price2.toFloat() + data.price.toFloat()).toString()
                showDataList(size)

            }

        }
        override fun getItemCount(): Int {
            return list.size
        }

        internal inner class ViewHolder(itemView: View) :
                RecyclerView.ViewHolder(itemView) {
            var data: Data? = null
            var imageFileName: ImageView = itemView.findViewById(R.id.imageFileName)
            var productname: TextView = itemView.findViewById(R.id.txtname)
            var size: TextView = itemView.findViewById(R.id.txtsize6)
            var price: TextView = itemView.findViewById(R.id.txtTime)
            var txtselect: TextView = itemView.findViewById(R.id.txtselect)


        }
    }

    private fun listPaper() {
        paper.add(Paper("0", "-เลือกกระดาษ-",""))
        val urlPaper: String = getString(R.string.root_url) + getString(R.string.paper_url)
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder().url(urlPaper).get().build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val res = JSONArray(response.body!!.string())
                    if (res.length() > 0) {
                        for (i in 0 until res.length()) {
                            val item: JSONObject = res.getJSONObject(i)
                            paper.add(Paper(
                                    item.getString("paperID"),
                                    item.getString("papername"),
                                    item.getString("price")))

                        }
                    }
                } catch (e: JSONException) { e.printStackTrace() }
            } else { response.code }
        } catch (e: IOException) { e.printStackTrace() }
    }

    internal class Paper(var paperID: String, var papername: String, var price2: String) {
        override fun toString(): String {

            return papername

        }

    }

    private fun listSizeimage() {
        sizeimage.add(Sizeimage("0", "-เลือกขนาดรูป-",""))
        val urlSizeimage: String = getString(R.string.root_url) + getString(R.string.sizeimage_url)
        val okHttpClient = OkHttpClient()
        val request: Request = Request.Builder().url(urlSizeimage).get().build()
        try {
            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                try {
                    val res = JSONArray(response.body!!.string())
                    if (res.length() > 0) {
                        for (i in 0 until res.length()) {
                            val item: JSONObject = res.getJSONObject(i)
                            sizeimage.add(Sizeimage(
                                    item.getString("sizeimageID"),
                                    item.getString("size"),
                                    item.getString("price")))

                        }
                    }
                } catch (e: JSONException) { e.printStackTrace() }
            } else { response.code }
        } catch (e: IOException) { e.printStackTrace() }
    }

    internal class Sizeimage(var sizeimageID: String, var size: String, var price: String) {
        override fun toString(): String {

            return size

        }

    }


    private fun permission()
    {
        //Set permission to open camera and access a directory
        if ((ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE), 225)
        }
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && null != intent) {
            val uri = intent.data
            file = File(getFilePath(uri))
            val bitmap: Bitmap
            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                //show image
                imageViewSlip?.setImageBitmap(bitmap)
                imageViewSlip?.setImageURI(uri)
                imageViewSlip?.visibility = View.VISIBLE
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
            val imageUri = Uri.parse("file:$imageFilePath")
            file = File(imageUri.path)
            try {
                val ims: InputStream = FileInputStream(file)
                var imageBitmap = BitmapFactory.decodeStream(ims)
                imageBitmap = resizeImage(imageBitmap, 1024, 1024) //resize image
                imageBitmap = resolveRotateImage(imageBitmap, imageFilePath!!) //Resolve auto rotate image

                //show image
                imageViewSlip?.setImageBitmap(imageBitmap)
                imageViewSlip?.visibility = View.VISIBLE
                getFileName(imageUri)

            } catch (e: FileNotFoundException) {
                return
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun createImageFile(): File? {
        // Create an image file name
        val storageDir = File(
                Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "")
        val image = File.createTempFile(
                SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()), ".png",
                storageDir)
        imageFilePath = image.absolutePath
        return image
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun getFilePath(uri: Uri?): String? {
        var path = ""
        val wholeID = DocumentsContract.getDocumentId(uri)
        // Split at colon, use second item in the arraygetDocumentId(uri)
        val id = wholeID.split(":".toRegex()).toTypedArray()[1]
        val column = arrayOf(MediaStore.Images.Media.DATA)
        // where id is equal to
        val sel = MediaStore.Images.Media._ID + "=?"
        val cursor = requireActivity().contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, arrayOf(id), null)
        var columnIndex = 0
        if (cursor != null) {
            columnIndex = cursor.getColumnIndex(column[0])
            if (cursor.moveToFirst()) {
                path = cursor.getString(columnIndex)
            }
            cursor.close()
        }
        return path
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = requireActivity().contentResolver.query(
                    uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(
                            cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    private fun resizeImage(bm: Bitmap?, newWidth: Int, newHeight: Int): Bitmap? {
        val width = bm!!.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false)
        bm.recycle()
        return resizedBitmap
    }

    private fun resolveRotateImage(bitmap: Bitmap?, photoPath: String): Bitmap? {
        val ei = ExifInterface(photoPath)
        val orientation = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED)
        var rotatedBitmap: Bitmap? = null
        rotatedBitmap = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
            ExifInterface.ORIENTATION_NORMAL -> bitmap
            else -> bitmap
        }
        return rotatedBitmap
    }

    private fun rotateImage(source: Bitmap?, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source!!, 0, 0, source.width, source.height,
                matrix, true)
    }

}