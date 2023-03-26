package com.main.mainproject.com.main.mainproject.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.main.mainproject.R
import com.main.mainproject.RvListnerNotes
import com.main.mainproject.com.main.mainproject.models.ModelNotes

class AdapterNotes(private val context:Context,
                   private val notesArrayList:ArrayList<ModelNotes>,
                   private val rvListnerNotes: RvListnerNotes
                   ):Adapter<AdapterNotes.HolderNotes>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderNotes {
        val view = LayoutInflater.from(context).inflate(R.layout.row_notes,parent,false)
        return HolderNotes(view)
    }

    override fun onBindViewHolder(holder: HolderNotes, position: Int) {
    val modelNotes = notesArrayList[position]
        holder.notesTitleTv.text = "${modelNotes.title}"
        if(modelNotes.description.length<30) {
            holder.notesDescriptionTv.text = modelNotes.description
        }else{
            holder.notesDescriptionTv.text = "${modelNotes.description.substring(0..30)}..."

        }
        holder.moreNotesOptionBtn.setOnClickListener {
            rvListnerNotes.onNotesMoreClick(modelNotes,position,holder)
        }
        holder.itemView.setOnClickListener{
            rvListnerNotes.onNotesClick(modelNotes,position)
        }
    }

    override fun getItemCount(): Int {
        return notesArrayList.size
    }

    inner class HolderNotes(itemView: View):ViewHolder(itemView){

        var notesTitleTv:TextView = itemView.findViewById(R.id.notesTitleTv)
        var notesDescriptionTv:TextView  = itemView.findViewById(R.id.notesDescriptionTv)
        var moreNotesOptionBtn:ImageView = itemView.findViewById(R.id.moreNotesOptionBtn)

    }
}