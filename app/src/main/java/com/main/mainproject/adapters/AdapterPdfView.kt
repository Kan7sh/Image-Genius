package com.main.mainproject.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.main.mainproject.R
import com.main.mainproject.models.ModelPdfView

class AdapterPdfView(
    private val context: Context,
    private val pdfViewArrayList:ArrayList<ModelPdfView>
) : Adapter<AdapterPdfView.HolderPdfView>(){





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterPdfView.HolderPdfView {
        val view  = LayoutInflater.from(context).inflate(R.layout.row_pdf_view,parent,false)

        return HolderPdfView(view)
    }

    override fun onBindViewHolder(holder: AdapterPdfView.HolderPdfView, position: Int) {

        val modelPdfView = pdfViewArrayList[position]

        val pageNumber = position+1
        val bitmap = modelPdfView.bitmap

        Glide.with(context)
            .load(bitmap)
            .placeholder(R.drawable.ic_baseline_image_24)
            .into(holder.imageTv)
        holder.pageNumberTv.text = "$pageNumber"

    }

    override fun getItemCount(): Int {
        return pdfViewArrayList.size
    }
    inner class HolderPdfView(itemView:View):ViewHolder(itemView){

        val pageNumberTv:TextView = itemView.findViewById(R.id.pageNumberTv)
        val imageTv:ImageView = itemView.findViewById(R.id.imageIv)
    }
}