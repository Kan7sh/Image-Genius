package com.main.mainproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.main.mainproject.com.main.mainproject.adapters.AdapterNotes
import com.main.mainproject.com.main.mainproject.models.ModelNotes

class NotesHomeActivity : AppCompatActivity() {
    val myUid = Firebase.auth.currentUser!!.uid
    private lateinit var notesRv:RecyclerView
    private lateinit var database:DatabaseReference
    private lateinit var adapterNotes:AdapterNotes
    private lateinit var notesArrayList:ArrayList<ModelNotes>
    companion object{
        private const val TAG="NOTES_HOME"
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_home)
        loadNotes()
        findViewById<FloatingActionButton>(R.id.addNotesFab).setOnClickListener{
            val intent  = Intent(this,NotesTakingActivity::class.java)
            startActivity(intent)

        }
    }

    private fun loadNotes() {
        Log.d(TAG, "loadNotes: ")
        notesArrayList = ArrayList()
        notesRv  =findViewById(R.id.notesRv)
        database = Firebase.database.reference
        adapterNotes = AdapterNotes(this,notesArrayList,object:RvListnerNotes{
            override fun onNotesClick(modelNotes: ModelNotes, postion: Int) {

                val intent = Intent(this@NotesHomeActivity,NotesTakingActivity::class.java)
                intent.putExtra("noteId",modelNotes.notesId)
                startActivity(intent)
            }

            override fun onNotesMoreClick(
                modelNotes: ModelNotes,
                postion: Int,
                holder: AdapterNotes.HolderNotes
            ) {
                showMoreNotesOption(modelNotes,holder)
            }

        })
        notesRv.adapter = adapterNotes
        database.child("Notes").child(myUid).addListenerForSingleValueEvent(
            object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.value!=null){
                        Log.d(TAG, "onDataChange: $snapshot")
                    val readData = snapshot.value as Map<String,Map<String,Any>>
                    for(each in readData){
                        val modelNotes = ModelNotes(each.key.toInt(),each.value.get("title").toString(),each.value.get("description").toString())
                        notesArrayList.add(modelNotes)
                        adapterNotes.notifyItemInserted(notesArrayList.size)
                    }
                }}

                override fun onCancelled(error: DatabaseError) {

                }

            }
        )
    }

    private fun showMoreNotesOption(modelNotes: ModelNotes, holder: AdapterNotes.HolderNotes) {
        Log.d(TAG, "showMoreNotesOption: ")
        val popupMenu = PopupMenu(this,holder.moreNotesOptionBtn)
        popupMenu.menu.add(Menu.NONE,0,0,"Delete")
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { menuItem->
            val itemId = menuItem.itemId
            if(itemId==0){
            deleteNote(modelNotes)
            }
            true
        }
    }

    private fun deleteNote(modelNotes: ModelNotes) {
        Log.d(TAG, "deleteNote: ")
        database.child("Notes").child(myUid).child(modelNotes.notesId.toString()).removeValue()
        loadNotes()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loadNotes()
    }


}