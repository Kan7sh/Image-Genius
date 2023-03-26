package com.main.mainproject

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class RoomHomeActivity : AppCompatActivity() {
    private lateinit var roomHomeBottomMenu:BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_home)
        roomHomeBottomMenu = findViewById(R.id.roomHomeBottomNav)
        loadRoomDocs()
        roomHomeBottomMenu.setOnItemSelectedListener { menuItem->
            when(menuItem.itemId){
                R.id.bottomRoomDocs->{
                    loadRoomDocs()
                }
                R.id.bottomRoomParticipants->{
                    loadRoomParticipants()
                }
            }
            return@setOnItemSelectedListener true

        }

    }

    private fun loadRoomParticipants() {
        title = "Participants"
        val roomCode = intent.getStringExtra("RoomCode")
        val roomName = intent.getStringExtra("RoomName")
        val participantsListFragment = ParticipantsListFragment(roomCode!!,roomName!!)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.roomHomeFrameLayout,participantsListFragment,"ParticipantsListFragment")
        fragmentTransaction.commit()


    }

    private fun loadRoomDocs() {
        title = "Docs"
        val roomCode = intent.getStringExtra("RoomCode").toString()
        val docsListFragment = DocsListFragment(roomCode)
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.roomHomeFrameLayout,docsListFragment,"DocsListFragment")
        fragmentTransaction.commit()


    }
}