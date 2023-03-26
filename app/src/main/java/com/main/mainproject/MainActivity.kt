package com.main.mainproject

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.main.mainproject.com.main.mainproject.Home

class MainActivity : AppCompatActivity() {
    lateinit var auth:FirebaseAuth
    private lateinit var googleSignInClient:GoogleSignInClient
    lateinit var session:LoginPref

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        session=LoginPref(this)
        auth = FirebaseAuth.getInstance()
        if(session.isLoggedIn()){
            var i : Intent = Intent(applicationContext, Home::class.java)
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            finish()
        }

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this,gso)
        findViewById<Button>(R.id.loginButton).setOnClickListener{
            signInGoogle()
        }
    }

    private fun signInGoogle(){
        var signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if(result.resultCode== Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResult(task)
        }
    }

    private fun handleResult(task: Task<GoogleSignInAccount>){
        if(task.isSuccessful){
            val account:GoogleSignInAccount? =task.result
            if(account!=null){
                updateUi(account)
            }
        }else{
            Toast.makeText(this,task.exception.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUi(account:GoogleSignInAccount){
        val credentials = GoogleAuthProvider.getCredential(account.idToken,null)
        auth.signInWithCredential(credentials).addOnCompleteListener{
            if(it.isSuccessful){
                val intent :Intent =  Intent(this, Home::class.java)
                session.createLoginSession(account.displayName,account.email)
                intent.putExtra("email",account.email)
                intent.putExtra("name",account.displayName)
                startActivity(intent)
            }else{
                Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                Log.d("SignIN","${it.exception.toString()}")
            }
        }
    }
}