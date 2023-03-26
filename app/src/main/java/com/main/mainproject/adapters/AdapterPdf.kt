package com.main.mainproject.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.main.mainproject.Methods
import com.main.mainproject.R
import com.main.mainproject.RvListenerPdf
import com.main.mainproject.models.ModelPdf
import java.util.concurrent.Executors

class AdapterPdf(
    private val context: Context,
    private val pdfArrayList: ArrayList<ModelPdf>
    , private val rvListenerPdf:RvListenerPdf
    ) :Adapter<AdapterPdf.HolderPdf>() {

    companion object{
        private const val TAG = "ADAPTER_PDF_TAG"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdf {
        val view  = LayoutInflater.from(context).inflate(R.layout.row_pdf, parent,false)
        return HolderPdf(view)
    }

    override fun onBindViewHolder(holder: HolderPdf, position: Int) {

        val modelPdf = pdfArrayList[position]

        val name = modelPdf.file.name
        val timestamp = modelPdf.file.lastModified()
        val formattedDate: String = Methods.formatTimestamp(timestamp)

        loadThumbnailFromPdf(modelPdf,holder)
        loadFileSize(modelPdf,holder)

        holder.nameIv.text = name
        holder.dateIv.text = formattedDate

        holder.itemView.setOnClickListener{
           rvListenerPdf.onPdfClick(modelPdf , position)
        }

        holder.morebtn.setOnClickListener {
            rvListenerPdf.onPdfMoreClick(modelPdf,position,holder)
        }
    }

    private fun loadFileSize(modelPdf: ModelPdf, holder: AdapterPdf.HolderPdf) {

        Log.d(TAG, "loadFileSize: ")

        val bytes:Double = modelPdf.file.length().toDouble()

        val kb = bytes/1024
        val mb = kb/1024

        var size = ""
        if (mb>=1){
            size = String.format("%.2f",mb)+"MB"
        }else if(kb>=1){
            size = String.format("%.2f",kb)+"KB"
        }else{
            size = String.format("%.2f",bytes)+"bytes"
        }


        Log.d(TAG, "loadFileSize: $size")

        holder.sizeIv.text = size
    }

    private fun loadThumbnailFromPdf(modelPdf: ModelPdf, holder: AdapterPdf.HolderPdf) {

        Log.d(TAG, "loadThumbnailFromPdf: ")
        val executorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executorService.execute{

            var thumbnailBitmap: Bitmap ?= null
            var pageCount = 0

            try {

                val parcelFileDescriptor = ParcelFileDescriptor.open(modelPdf.file, ParcelFileDescriptor.MODE_READ_ONLY)

                val pdfRender = PdfRenderer(parcelFileDescriptor)
                pageCount = pdfRender.pageCount
                if (pageCount<=0){
                    Log.d(TAG, "loadThumbnailFromPdf: No Pages")
                }else{

                    val currentPage = pdfRender.openPage(0)
                    thumbnailBitmap = Bitmap.createBitmap(currentPage.width,currentPage.height,Bitmap.Config.ARGB_8888)

                    currentPage.render(thumbnailBitmap,null,null,PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                }

            }catch (e:Exception){
                Log.d(TAG, "loadThumbnailFromPdf: ",e)
            }

            handler.post{
                Log.d(TAG, "loadThumbnailFromPdf: Setting thumbnail and page count")
                Glide.with(context)
                    .load(thumbnailBitmap)
                    .fitCenter()
                    .placeholder(R.drawable.pdfgray)
                    .into(holder.thumbnailIv)
                holder.pagesIv.text = "$pageCount Pages"

            }

        }

    }

    override fun getItemCount(): Int {
        return pdfArrayList.size
    }

    inner class HolderPdf(itemView: View): ViewHolder(itemView) {

        var thumbnailIv:ImageView  = itemView.findViewById(R.id.thumbnailIv)
        var nameIv:TextView = itemView.findViewById(R.id.nameIv)
        var pagesIv:TextView = itemView.findViewById(R.id.pageIv)
        var morebtn:ImageButton = itemView.findViewById(R.id.morebtn)
        var sizeIv:TextView = itemView.findViewById(R.id.sizeIv)
        var dateIv:TextView = itemView.findViewById(R.id.dateIv)


    }

}