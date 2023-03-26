package com.main.mainproject.com.main.mainproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.main.mainproject.*

class Home : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private lateinit var session: LoginPref


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        if(isOnline(this)==false){
            Toast.makeText(this,"No Internet",Toast.LENGTH_SHORT).show()
        }
        findViewById<Button>(R.id.signOutButton).setOnClickListener {
            session = LoginPref(this)
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Logout")
                .setMessage("Are you sure you want to Logout?")
                .setPositiveButton("NO"){dialog,which->
                    dialog.dismiss()
                }.setNegativeButton("YES"){dialog,which->
                    session.LogoutUser()
                    startActivity(Intent(this, MainActivity::class.java))
                    this.finish()
                }.show()

        }

        findViewById<Button>(R.id.logoHomeButton).setOnClickListener {
            var intent = Intent(this, LogoGenerator::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.imageToPDFHomeButton).setOnClickListener {

            var intent = Intent(this, ImageToPDF::class.java)
            startActivity(intent)

        }

        findViewById<Button>(R.id.pdfRoomHomeButton).setOnClickListener {

            var intent  = Intent(this, PDFRoom::class.java)
            startActivity(intent)
        }
        findViewById<Button>(R.id.notesTakerBtn).setOnClickListener {
            var intent = Intent(this, NotesHomeActivity::class.java)
            startActivity(intent)
        }
    }
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                } else {
                    TODO("VERSION.SDK_INT < M")
                }
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }
    override fun onBackPressed() {
        if(session.isLoggedIn()==false){
            super.onBackPressed()
        }

    }
}