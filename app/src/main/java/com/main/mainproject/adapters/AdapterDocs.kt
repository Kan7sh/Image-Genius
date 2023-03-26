package com.main.mainproject.com.main.mainproject.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.marginEnd
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.card.MaterialCardView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.main.mainproject.DocsListFragment
import com.main.mainproject.R
import com.main.mainproject.RvListinerDocs
import com.main.mainproject.com.main.mainproject.models.ModelDocs
import java.io.File

class AdapterDocs(private val context: Context,
                  private val docsArrayList: ArrayList<ModelDocs>,
                  private val rvlistnerDocs:RvListinerDocs,
                  private val upUid:String
): RecyclerView.Adapter<AdapterDocs.HolderDocs>() {

companion object{
    private const val TAG = "ADAPTER_DOCS"
}
    private var storage = Firebase.storage
    var storageRef = storage.reference

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDocs {
    val view = LayoutInflater.from(context).inflate(R.layout.row_room_pdf,parent,false)

        return HolderDocs(view)

    }

    override fun onBindViewHolder(holder: HolderDocs, position: Int) {
        val modelDocs = docsArrayList[position]
        holder.thumbnailIv.setImageResource(R.drawable.doc_chat)
        if(modelDocs.fileName.endsWith(".pdf")){
            holder.thumbnailIv.setImageResource(R.drawable.pdf_chat)
        }
        if(modelDocs.fileName.endsWith(".jpg")||modelDocs.fileName.endsWith(".png")||modelDocs.fileName.endsWith(".jpeg")){
            holder.thumbnailIv.setImageResource(R.drawable.image_chat)
        }
        holder.morebtn.isVisible = false
        val cardViewLayout = holder.cardView.layoutParams as MarginLayoutParams
        cardViewLayout.setMargins(0,5,100,0)
        if(modelDocs.upUid==upUid){
            holder.morebtn.isVisible = true
            holder.downloadBtn.isVisible = false
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context,R.color.yellow2))
            cardViewLayout.setMargins(100,5,0,0)
        }
        holder.nameIv.text = modelDocs.fileName
        var fileSize = "${((modelDocs.fileSize.toFloat()/1024)/1024).toString().substring(0,5)}MB"
        holder.sizeIv.setText(fileSize.toString())
        holder.dateIv.text = modelDocs.date
        holder.uploderNameTv.text = "by ${modelDocs.uploaderName}"
        holder.downloadBtn.setOnClickListener{
        rvlistnerDocs.onDownloadClick(modelDocs,position)
        }

        holder.morebtn.setOnClickListener{
            Log.d(TAG, "onBindViewHolder: sdfghj")
            rvlistnerDocs.onRoomMoreClick(modelDocs,position,holder)
        }

    }



    override fun getItemCount(): Int {
        return docsArrayList.size
    }

    inner class HolderDocs(itemView: View):ViewHolder(itemView){
        var cardView:MaterialCardView = itemView.findViewById(R.id.cardViewDoc)
        var thumbnailIv: ImageView = itemView.findViewById(R.id.thumbnailIv)
        var nameIv: TextView = itemView.findViewById(R.id.nameIv)
        var morebtn: ImageButton = itemView.findViewById(R.id.morebtn)
        var sizeIv: TextView = itemView.findViewById(R.id.sizeIv)
        var dateIv: TextView = itemView.findViewById(R.id.dateIv)
        var downloadBtn:ImageView=itemView.findViewById(R.id.downloadBtn)
        var uploderNameTv:TextView = itemView.findViewById(R.id.uploderNameTv)
    }
}