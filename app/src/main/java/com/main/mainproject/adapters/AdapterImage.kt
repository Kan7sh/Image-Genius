package com.main.mainproject.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.main.mainproject.ImageViewActivity
import com.main.mainproject.R
import com.main.mainproject.models.ModelImage

class AdapterImage (private val context: Context, private val imageArrayList:ArrayList<ModelImage>):
    Adapter<AdapterImage.HolderImage>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderImage {
         val view = LayoutInflater.from(context).inflate(R.layout.row_image,parent,false)
        return HolderImage(view)
    }

    override fun onBindViewHolder(holder: HolderImage, position: Int) {
        val modelImage = imageArrayList[position]
        val imageUri = modelImage.imageUri

        Glide.with(context)
            .load(imageUri)
            .placeholder(R.drawable.ic_baseline_insert_photo_24)
            .into(holder.imageIv)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, ImageViewActivity::class.java)
            Log.d("kkkkkk", "onBindViewHolder: $imageUri")
            intent.putExtra("imagesUri","$imageUri")
            context.startActivity(intent)

        }

        holder.checkedBox.setOnCheckedChangeListener {view, isChecked ->
            modelImage.checked = isChecked

        }
    }

    override fun getItemCount(): Int {
        return imageArrayList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position

    }
    inner class HolderImage(itemView: View):ViewHolder( itemView){

        var imageIv = itemView.findViewById<ImageView>(R.id.imageIv)
        var checkedBox = itemView.findViewById<CheckBox>(R.id.checkBox)
    }

}