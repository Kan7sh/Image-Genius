package com.main.mainproject

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Editable
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Locale

class NotesTakingActivity : AppCompatActivity() {
    private var noteID:Int = 0
    companion object{
        private const val TAG= "NOTES_TAKING"
    }
    private val speechRecognizer:SpeechRecognizer by lazy { SpeechRecognizer.createSpeechRecognizer(this) }
    private lateinit var titleEv:EditText
    private lateinit var descriptionEv:EditText
    private lateinit var database:DatabaseReference
    val myUid = Firebase.auth.currentUser!!.uid
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_taking)
        titleEv = findViewById(R.id.titleEv)
        database = Firebase.database.reference
        descriptionEv = findViewById(R.id.descriptionEv)
        noteID = intent.getIntExtra("noteId",0)
        database.child("Notes").child(myUid).child(noteID.toString()).addListenerForSingleValueEvent(
            object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.value!=null){
                    val readData = snapshot.value as Map<String,Any>
                    titleEv.setText(readData.get("title").toString())
                    descriptionEv.setText(readData.get("description").toString())
                }}

                override fun onCancelled(error: DatabaseError){
                }

            }
        )
        findViewById<FloatingActionButton>(R.id.notesMicBtn).setOnClickListener{
            if(checkMicPermission()){
            recordNotes()
            }else{
                requestMicPermission.launch(android.Manifest.permission.RECORD_AUDIO)
            }
        }
        findViewById<ImageView>(R.id.saveNoteIv).setOnClickListener {
            if(titleEv.text.toString()==""){
                Toast.makeText(this,"Please Enter The Title",Toast.LENGTH_SHORT).show()
            }else{
                if(noteID!=0){
                    database.child("Notes").child(myUid).child(noteID.toString()).child("title").setValue("${titleEv.text}")
                    database.child("Notes").child(myUid).child(noteID.toString()).child("description").setValue("${descriptionEv.text}")
                   // val intent = Intent(this,NotesHomeActivity::class.java)
                   // startActivity(intent)
                    finish()
            }else{
                    var noteID = 0
                    var available:Boolean = false
                    while(available==false){
                        noteID = (1000..9999).random()
                        available = checkNoteId(noteID)
                    }
                    database.child("Notes").child(myUid).child(noteID.toString()).child("title").setValue("${titleEv.text}")
                    database.child("Notes").child(myUid).child(noteID.toString()).child("description").setValue("${descriptionEv.text}")
                 //   val intent = Intent(this,NotesHomeActivity::class.java)
                 //   startActivity(intent)
                    finish()
                }}
        }
    }

    private fun recordNotes() {
        Log.d(TAG, "recordNotes: ")
        val intent  = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true)
        val previousNote = descriptionEv.text
        speechRecognizer.startListening(intent)
        speechRecognizer.setRecognitionListener(object:RecognitionListener{
            override fun onReadyForSpeech(params: Bundle?) {
            }

            override fun onBeginningOfSpeech() {
            }

            override fun onRmsChanged(rmsdB: Float) {
            }

            override fun onBufferReceived(buffer: ByteArray?) {
            }

            override fun onEndOfSpeech() {
            }

            override fun onError(error: Int) {
            }

            override fun onResults(results: Bundle?) {
/*                results?.let {
                    val result = it.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val previousText = descriptionEv.text.toString()
                    descriptionEv.setText("$previousText\n${result?.get(0)}")*/
            }//}

            override fun onPartialResults(partialResults: Bundle?) {
                Log.d(TAG, "onPartialResults: ")
                partialResults?.let {
                    val result = it.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    descriptionEv.setText("$previousNote\n${result?.get(0)}")
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
            }

        })


    }
    private fun checkNoteId(noteId:Int):Boolean{
        var available:Boolean = true
        database.child("Notes").child(myUid).addListenerForSingleValueEvent(
            object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val readData = snapshot.value as Map<String,Map<String,Any>>
                    for(each in readData){
                        if(each.key.toInt()==noteId){
                            available = false
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            }
        )
        return available
    }
    private var requestMicPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ActivityResultCallback<Boolean> { isGranted->
            Log.d(TAG, "requestPermission: $isGranted")
            if(isGranted){
                recordNotes()
            }else{
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_SHORT).show()
            }
        }
    )
    private fun checkMicPermission():Boolean{
        return  ContextCompat.checkSelfPermission(this,android.Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED
    }
}