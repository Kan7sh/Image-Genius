package com.main.mainproject.com.main.mainproject.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.main.mainproject.R
import com.main.mainproject.com.main.mainproject.models.ModelParticipants

class AdapterParticipants(private val mContext: Context,
                          private val modelParticipantsArrayList: ArrayList<ModelParticipants>,
                          private val adminUid:String,

                          ):Adapter<AdapterParticipants.HolderParticipants>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderParticipants {

    val view = LayoutInflater.from(mContext).inflate(R.layout.row_participants,parent,false)
        return HolderParticipants(view)
    }

    override fun onBindViewHolder(holder: HolderParticipants, position: Int) {
        val modelParticipants = modelParticipantsArrayList[position]
            holder.participantsNameTv.text = "${modelParticipants.name}"


        if(modelParticipants.uid.toString()==adminUid.toString()){
            holder.adminTv.text = "Admin"
        }else{
            holder.adminTv.text = " "
        }
    }

    override fun getItemCount(): Int {
        return modelParticipantsArrayList.size
    }

    inner class HolderParticipants(itemView: View):ViewHolder(itemView){
        var participantsNameTv:TextView = itemView.findViewById(R.id.participantsNameTv)
        var adminTv:TextView = itemView.findViewById(R.id.adminIndicatorTv)
    }
}