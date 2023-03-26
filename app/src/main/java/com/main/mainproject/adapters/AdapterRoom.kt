package com.main.mainproject.com.main.mainproject.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.main.mainproject.R
import com.main.mainproject.RvListinerRoom
import com.main.mainproject.com.main.mainproject.models.ModelRoom

class AdapterRoom(private val roomArrayList:ArrayList<ModelRoom>,
                  private val context:Context,
                  private val rvListinerRoom: RvListinerRoom
                  ):Adapter<AdapterRoom.HolderRoom>() {



    private lateinit var database:DatabaseReference
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderRoom {
        val view  = LayoutInflater.from(context).inflate(R.layout.row_room, parent,false)
        return HolderRoom(view)

    }

    override fun onBindViewHolder(holder: HolderRoom, position: Int) {
        val modelRoom = roomArrayList[position]
        val name = modelRoom.roomName

        holder.nameTv.text = name
        database = Firebase.database.reference
        database.child("Participants").child(modelRoom.roomCode.toString()).addListenerForSingleValueEvent(
            object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val readData = snapshot.value as Map<String,Any>
                    holder.peopleNumberTv.text = readData.size.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            }
        )

        holder.itemView.setOnClickListener{
            rvListinerRoom.onRoomClick(modelRoom,position)
        }
        holder.moreBtn.setOnClickListener {
            rvListinerRoom.onPdfMoreClick(modelRoom,position,holder)
        }
    }

    override fun getItemCount(): Int {
        return roomArrayList.size
    }

    inner class HolderRoom(itemView: View):ViewHolder(itemView){
        var thumbnailTv:ImageView = itemView.findViewById(R.id.thumbnailRoomIv)
        var nameTv:TextView = itemView.findViewById(R.id.roomName)
        var peopleNumberTv:TextView = itemView.findViewById(R.id.peopleNumberTv)
        var moreBtn:ImageView = itemView.findViewById(R.id.moreBtn)

    }
}