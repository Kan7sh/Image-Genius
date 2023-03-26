package com.main.mainproject

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import kotlin.math.log


/*private class DownloadImageTask() : AsyncTask<String, Void, Bitmap>() {
    private lateinit var root: File

    constructor(root: File) : this() {
        this.root = root
    }
    override fun doInBackground(vararg params: String): Bitmap? {
        return try {
            BitmapFactory.decodeStream(URL(params[0]).openStream())
        } catch (e: Exception) {
            Log.e("Error", e.message, e)
            null
        }
    }

    override fun onPostExecute(result: Bitmap?) {
        if (result != null) {

            val storageDirectory = Environment.getExternalStorageDirectory().toString()
            val timestamp = System.currentTimeMillis()
            val fileName = "$timestamp.jpeg"
            val file = File(root,fileName)
            file.createNewFile()
            FileOutputStream(file).use { out ->
                result.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
            }
            Log.d("LOGOGEN", "onPostExecute: ${root.toString()}/$fileName")
            //Toast.makeText(this@DownloadImageTask, "$storageDirectory/$fileName", Toast.LENGTH_SHORT).show()
        }
    }*/

   /* fun execute(url: String, root: File) {

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DownloadImageTask> {
        override fun createFromParcel(parcel: Parcel): DownloadImageTask {
            return DownloadImageTask(parcel)
        }

        override fun newArray(size: Int): Array<DownloadImageTask?> {
            return arrayOfNulls(size)
        }
    }*/
//}

class LogoGenerator : AppCompatActivity() {
    private var flag = 0
    private lateinit var url: String
    private lateinit var progressBar: ProgressBar
    @SuppressLint("MissingInflatedId")
    companion object{
        private const val TAG="LOGO_GENERATER"
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo_generator)
        progressBar = findViewById(R.id.loadingImagePb)
        progressBar.visibility = View.GONE
        findViewById<Button>(R.id.generateLogo).setOnClickListener {
            Log.d(TAG, "onCreate: generate logo button pressed")
            progressBar.visibility = View.VISIBLE
            generateLogo()
        }
        findViewById<FloatingActionButton>(R.id.clearBtn).setOnClickListener {
            findViewById<EditText>(R.id.logoDescription).setText("")
        }

        findViewById<Button>(R.id.downloadLogo).setOnClickListener {
            Log.d(TAG, "onCreate:")
            if(flag==1){
                downloadImage(url)
                flag=3
        }else if(flag==0){
            Toast.makeText(this,"Please Generate the Image First",Toast.LENGTH_SHORT).show()
        }else if(flag==3){
            Toast.makeText(this,"Wait",Toast.LENGTH_SHORT).show()
        }else if(flag==4){
            Toast.makeText(this,"Image already downloaded",Toast.LENGTH_SHORT).show()
        }}
    }

    private fun generateLogo() {
        Log.d(TAG, "generateLogo: ")
            var generatedImage: ImageView = findViewById(R.id.logoImage)
            var context = findViewById<EditText>(R.id.logoDescription).text.toString()
            if(context!=""){
                var GenerateLogo = DALLE()
                url = GenerateLogo.generateImage(context)?.toString().toString()
                Log.d("testing", "generateLogo: $url")
            }
            /*     Glide.with(this)
                     .load(url)
                     .listener(object : RequestListener<Drawable> {
                         override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                             progressBar.visibility = View.GONE // Hide the progress spinner
                             return false
                         }

                         override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                             progressBar.visibility = View.GONE // Hide the progress spinner
                             return false
                         }
                     })
                     .into(generatedImage)*/
            if (url != "") {
                Toast.makeText(this,"Generating",Toast.LENGTH_LONG).show()
                Log.d("hh", url.toString())
                Picasso.get().load(url).into(generatedImage,object :Callback{
                    override fun onSuccess() {
                        progressBar.visibility = View.GONE
                        flag=1
                    }

                    override fun onError(e: java.lang.Exception?) {
                        Toast.makeText(this@LogoGenerator,"Please use appropriate words",Toast.LENGTH_SHORT).show()
                    }

                })
            }else{
                progressBar.visibility = View.GONE
                Toast.makeText(this@LogoGenerator,"Please use appropriate words",Toast.LENGTH_SHORT).show()
            }




    }

    private fun downloadImage(url: String) {
        Log.d(TAG, "downloadImage: ")
       if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            val REQUEST_WRITE_STORAGE = 102
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_WRITE_STORAGE
            )
        } else {
            Log.d("LOGOGEN", "downloadImage: ")
            val timestamp = System.currentTimeMillis()
            val fileName = "$timestamp.jpeg"
            saveImage(url)
           flag =0
        }
    }

    private fun saveImage(url: String) {
       /* Log.d(TAG, "saveImage: ")
        val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        val root = File(this.getExternalFilesDir(null),Constants.LOGO_FOLDER)
        root.mkdirs()
        DownloadImageTask(folder).execute(url)*/
        val storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val fileName = "${System.currentTimeMillis()}.jpeg"
        Log.d(TAG, "saveImage: $fileName")
        val file = File(storageDirectory, fileName)
        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        Toast.makeText(this, "Downloading", Toast.LENGTH_LONG).show()
        val request = DownloadManager.Request(Uri.parse(url))
            .setTitle(fileName)
            .setDescription("Downloading")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationUri(Uri.fromFile(file))
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
        downloadManager.enqueue(request)
        flag = 4

    }
}
