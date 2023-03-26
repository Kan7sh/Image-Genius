package com.main.mainproject

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.main.mainproject.adapters.AdapterPdfView
import com.main.mainproject.models.ModelPdfView
import java.io.File
import java.util.concurrent.Executors

class PdfViewActivity : AppCompatActivity() {

    private lateinit var pdfViewRv:RecyclerView

    companion object{
        private  const val TAG = "PDF_VIEW_TAG"
    }

    private var pdfUri:String  = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_view)

        supportActionBar?.title = "PDF"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        pdfViewRv = findViewById(R.id.pdfViewRv)
        pdfUri = intent.getStringExtra("pdfUri").toString()
        Log.d(TAG, "onCreate: pdfUri: $pdfUri")

        loadPdfPages()
    }

    private var mCurrentPage:PdfRenderer.Page? = null
    private fun loadPdfPages() {

        Log.d(TAG, "loadPdfPages: ")


        val pdfViewArrayList:ArrayList<ModelPdfView> = ArrayList()

        val adapterPdfView = AdapterPdfView(this,pdfViewArrayList)

        pdfViewRv.adapter = adapterPdfView

        val file  = File(Uri.parse(pdfUri).path)
        try {
            supportActionBar?.setSubtitle(file.name)

        }catch (e: Exception){
            Log.d(TAG, "loadPdfPages: ",e)
        }

        val executorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executorService.execute {
            try {
                val parcelFileDescription = ParcelFileDescriptor.open(file,ParcelFileDescriptor.MODE_READ_ONLY)

                val pdfRenderer  = PdfRenderer(parcelFileDescription)

                val pageCount = pdfRenderer.pageCount
                if(pageCount<=0){
                    Log.d(TAG, "loadPdfPages: No pages in PDF file")
                }else{
                    Log.d(TAG, "loadPdfPages: Pages in PDF file are $pageCount")

                    for(i in 0 until pageCount){
                        if(mCurrentPage!=null){
                            mCurrentPage?.close()
                        }

                        mCurrentPage = pdfRenderer.openPage(i)

                        val bitmap = Bitmap.createBitmap(
                            mCurrentPage!!.width,
                            mCurrentPage!!.height,
                            Bitmap.Config.ARGB_8888
                            )


                        mCurrentPage!!.render(bitmap,null,null,PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                        val modelPdf = ModelPdfView(Uri.parse(pdfUri),(i+1),pageCount,bitmap)

                        pdfViewArrayList.add(modelPdf)
                    }
                }
            }catch (e:Exception){
                Log.d(TAG, "loadPdfPages: ",e)
            }

            handler.post{
                Log.d(TAG, "loadPdfPages: UI thread...")
                adapterPdfView.notifyDataSetChanged()
            }

        }

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}