package com.main.mainproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.main.mainproject.com.main.mainproject.Rooms
import com.main.mainproject.com.main.mainproject.adapters.AdapterParticipants
import com.main.mainproject.com.main.mainproject.adapters.AdapterRoom
import com.main.mainproject.com.main.mainproject.models.ModelParticipants
import com.main.mainproject.com.main.mainproject.models.ModelRoom


class PDFRoom : AppCompatActivity() {

    companion object{
        private const val TAG = "PDFRoom"
    }
    val myUid = Firebase.auth.currentUser!!.uid
    val myName = Firebase.auth.currentUser!!.displayName
    private lateinit var roomOptionBtn: FloatingActionButton
    private lateinit var database: DatabaseReference
    private lateinit var user:LoginPref
    private lateinit var email:String
    private lateinit var roomRv:RecyclerView
    private lateinit var roomJoindList:ArrayList<ModelRoom>
    private lateinit var adapterRoom: AdapterRoom
    private lateinit var theadmin:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdfroom)
        roomOptionBtn = findViewById(R.id.roomOptionBtn)
        roomRv = findViewById(R.id.roomRv)
        loadRooms()
        println(myUid)
        roomOptionBtn.setOnClickListener{
            showRoomOptions()
        }
    }

    private fun loadRooms() {
        Log.d(TAG, "loadRooms: ")
        roomJoindList = ArrayList()
        database = Firebase.database.reference
        user = LoginPref(this)
        email = user.getUserDetails().get("email").toString()
        adapterRoom = AdapterRoom(roomJoindList,this,object:RvListinerRoom{
            override fun onRoomClick(modelroom: ModelRoom, position: Int) {
                val intent = Intent(this@PDFRoom,RoomHomeActivity::class.java)
                intent.putExtra("RoomCode","${modelroom.roomCode}")
                intent.putExtra("RoomName","${modelroom.roomName}")
                startActivity(intent)
            }

            override fun onPdfMoreClick(
                modelRoom: ModelRoom,
                position: Int,
                holder: AdapterRoom.HolderRoom
            ) {
                showMoreOptionsDialog(modelRoom,holder)

            }


        })

        roomRv.adapter = adapterRoom

        database.child("UserJoined").child(myUid).addListenerForSingleValueEvent(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d(TAG, "onDataChange: ")
                    Log.d(TAG, "onDataChange: ${snapshot.value}")
                    if (snapshot.value != null) {
                        val readRoomData = snapshot.value as Map<String, Any>
                        for (each in readRoomData) {
                            val modelRoom = ModelRoom(each.key.toInt(), each.value.toString())
                            roomJoindList.add(modelRoom)
                            adapterRoom.notifyItemInserted(roomJoindList.size)

                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
             }

            }


        )


/*
            val modelRoom = ModelRoom(12345,it.toString())
*//*
            roomJoindList.add(modelRoom)
            adapterRoom.notifyItemInserted(roomJoindList.size)*/
        }

    private fun showMoreOptionsDialog(modelRoom: ModelRoom, holder: AdapterRoom.HolderRoom) {
        Log.d(TAG, "showMoreOptionsDialog: ")
        val popupMenu = PopupMenu(this,holder.moreBtn)
        popupMenu.menu.add(Menu.NONE,0,0,"Leave")
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener{menuItem->
            val itemId = menuItem.itemId
            if(itemId==0){
                leaveRoom(modelRoom)
            }
           true
        }
    }

    private fun leaveRoom(modelRoom: ModelRoom) {
        Log.d(TAG, "leaveRoom: ")
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("leave Room?")
            .setMessage("Are you sure you want to leave the Room")
            .setPositiveButton("NO"){dialog,which->
                dialog.dismiss()
            }
            .setNegativeButton("YES"){dailog,which->


                database.child("Rooms").child(modelRoom.roomCode.toString()).addListenerForSingleValueEvent(
                    object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            Log.d(TAG, "onDataChange: $snapshot")
                            var admin = snapshot.value as Map<String, Any>
                            Log.d(TAG, "onDataChange: ${admin?.get("admin")}")
                            theadmin = admin.get("admin").toString()
                            if(theadmin==myUid){
                                database.child("UserJoined").child(myUid).child(modelRoom.roomCode.toString()).removeValue()
                                database.child("Participants").child(modelRoom.roomCode.toString()).child(myUid).removeValue()
                                database.child("Participants").addListenerForSingleValueEvent(
                                    object:ValueEventListener{
                                        override fun onDataChange(snapshot2: DataSnapshot) {
                                            Log.d(TAG, "onDataChange: 2222")
                                            val readData = snapshot2.value as Map<String,Map<String,Any>>
                                            if(readData.get("${modelRoom.roomCode}")==null){
                                                Log.d(TAG, "onDataChange: delete room")
                                                database.child("Rooms").child(modelRoom.roomCode.toString()).removeValue()
                                                database.child("FilesUploaded").child(modelRoom.roomCode.toString()).removeValue()
                                                loadRooms()
                                            }else{
                                                database.child("Participants").child(modelRoom.roomCode.toString()).addListenerForSingleValueEvent(
                                                    object :ValueEventListener{
                                                        override fun onDataChange(snapshot3: DataSnapshot) {
                                                            val readData2 = snapshot3.value as Map<String,Any>
                                                            val newAdmin:Int = (0..readData.size).random()
                                                            var count:Int=0
                                                            for(each in readData2){
                                                                if(count==newAdmin){
                                                                    database.child("Rooms").child(modelRoom.roomCode.toString()).child("admin").setValue(each.key)
                                                                    Log.d(TAG, "onDataChange: newadmin is ${each.key}")
                                                                    break
                                                                }
                                                                count=count+1
                                                            }
                                                        }

                                                        override fun onCancelled(error: DatabaseError) {
                                                        }

                                                    }
                                                )
                                                loadRooms()
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                        }

                                    }
                                )
                            }else{
                                database.child("UserJoined").child(myUid).child(modelRoom.roomCode.toString()).removeValue()
                                database.child("Participants").child(modelRoom.roomCode.toString()).child(myUid).removeValue()
                                loadRooms()
                            }

                        }

                        override fun onCancelled(error: DatabaseError) {

                        }


                    }
                )            }.show()

    }


    private fun showRoomOptions() {
        Log.d(TAG, "showRoomOptions: ")

        val popupMenu = PopupMenu(this,roomOptionBtn)
        popupMenu.menu.add(Menu.NONE, 1,1,"Join Room")
        popupMenu.menu.add(Menu.NONE, 2,2,"Create Room")

        popupMenu.show()
        popupMenu.setOnMenuItemClickListener {menuItem->
            val itemId = menuItem.itemId
            if(itemId==1){
                joinRoom()
            }else if(itemId==2){
                createRoom()
            }
            return@setOnMenuItemClickListener true

        }


    }

    private fun joinRoom() {
        Log.d(TAG, "joinRoom: ")
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_join_room,null)
        val roomCodeEt = view.findViewById<EditText>(R.id.roomCodeEt)
        val roomJoinBtn = view.findViewById<Button>(R.id.roomJoinBtn)
        val joinRoomErrorTv = view.findViewById<TextView>(R.id.joinRoomErrorTv)
        val  builder = MaterialAlertDialogBuilder(this)
        builder.setView(view)

        val alertDialog = builder.create()
        alertDialog.show()

        roomJoinBtn.setOnClickListener {
            val roomCode = roomCodeEt.text.toString()
            database.child("Rooms").child(roomCode).addListenerForSingleValueEvent(
                object:ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d(TAG, "onDataChange: $snapshot")
                   if(snapshot.value==null){
                       joinRoomErrorTv.text = "Invalid Room Code"
                   }else{
                       database.child("Rooms").child(roomCode).addListenerForSingleValueEvent(
                           object:ValueEventListener{
                               override fun onDataChange(snapshot2: DataSnapshot) {
                                   val readData  = snapshot2.value as Map<String,Any>
                                   database.child("UserJoined").child(myUid).child(roomCode.toString()).setValue(readData.get("name"))
                                   database.child("Participants").child(roomCode.toString()).child(myUid).setValue(myName)
                                   loadRooms()
                                   alertDialog.dismiss()
                               }

                               override fun onCancelled(error: DatabaseError) {
                               }

                           }
                       )
                                      }
                    }


                    override fun onCancelled(error: DatabaseError) {

                    }


                }
            )
        }

    }

    private fun createRoom() {
        Log.d(TAG, "createRoom: ")
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_create_room,null)
        val roomNameEt = view.findViewById<EditText>(R.id.roomNameEt)
        val createRoomBtn = view.findViewById<Button>(R.id.createRoomBtn)
        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(view)
        val alertDialog = builder.create()
        alertDialog.show()
        createRoomBtn.setOnClickListener {
            var roomCode = 0
            var available:Boolean = false
            while(available==false){
                roomCode=(10000..99999).random()
                available = checkRoom(roomCode)
            }
            val roomName  = roomNameEt.text.toString()

            val room = Rooms(roomName,roomCode,myUid)

            database.child("Rooms").child(roomCode.toString()).setValue(room).addOnSuccessListener {
                Toast.makeText(this,"Room created",Toast.LENGTH_SHORT).show()

            }
            database.child("UserJoined").child(myUid).child(roomCode.toString()).setValue(roomName)
            database.child("Participants").child(roomCode.toString()).child(myUid).setValue(myName)
            Log.d(TAG, "createRoom: Room Created with Name: ${roomName}")
            alertDialog.dismiss()
            loadRooms()

        }


    }

    private fun checkRoom(roomId:Int):Boolean{
        var available:Boolean = true
        database.child("Rooms").addListenerForSingleValueEvent(
            object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val readData = snapshot.value as Map<String,Map<String,Any>>
                    for(each in readData){
                        if(each.key.toInt()==roomId){
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


}