package com.main.mainproject

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.main.mainproject.adapters.AdapterPdf
import com.main.mainproject.models.ModelPdf
import java.io.File

class PDFListFragment : Fragment() {

    private lateinit var pdfRv:RecyclerView

    private lateinit var pdfArrayList:ArrayList<ModelPdf>
    private lateinit var adapterPdf: AdapterPdf


    companion object{
        private const val TAG = "PDF_LIST_TAG"
    }

    private lateinit var mContext:Context
    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_p_d_f_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pdfRv = view.findViewById(R.id.pdfRv)
        loadPdfDocuments()
    }

    private fun loadPdfDocuments(){
        Log.d(TAG, "loadPdfDocuments: ")

        pdfArrayList = ArrayList()
        adapterPdf = AdapterPdf(mContext,pdfArrayList,object:RvListenerPdf{
            override fun onPdfClick(modelPdf: ModelPdf, position: Int) {

                val intent = Intent(mContext,PdfViewActivity::class.java)
                intent.putExtra("pdfUri","${modelPdf.uri}")
                startActivity(intent)

            }

            override fun onPdfMoreClick(modelPdf: ModelPdf, position: Int, holder: AdapterPdf.HolderPdf) {
                showMoreOptionsDialog(modelPdf,holder)
            }
        })

        pdfRv.adapter = adapterPdf

        val folder = File(mContext.getExternalFilesDir(null),Constants.PDF_FOLDER)

        if (folder.exists() ){

            val files = folder.listFiles()
            Log.d(TAG, "loadPdfDocuments: FilesCount: ${files.size}")
            for(fileEntry in files){
                Log.d(TAG, "loadPdfDocuments: FileName: ${fileEntry.name}")
                val uri  = Uri.fromFile(fileEntry)
                val modelPdf = ModelPdf(fileEntry,uri)

                pdfArrayList.add(modelPdf)

                adapterPdf.notifyItemInserted(pdfArrayList.size)
            }

        }else{
            Toast.makeText(mContext,"Folder Dosen't exists",Toast.LENGTH_SHORT).show()
            Log.d(TAG, "loadPdfDocuments: No PDF Files yet...")
        }


    }

    private fun showMoreOptionsDialog(modelPdf: ModelPdf, holder: AdapterPdf.HolderPdf) {

        Log.d(TAG, "showMoreOptionsDialog: ")
        val popupMenu = PopupMenu(mContext,holder.morebtn)

        popupMenu.menu.add(Menu.NONE,0,0,"Rename")
        popupMenu.menu.add(Menu.NONE,1,1,"Delete")
        popupMenu.menu.add(Menu.NONE,2,2,"Share")
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener {menuItem->
            val itemId= menuItem.itemId
            if(itemId==0){
                pdfRename(modelPdf)
            }else if(itemId==1){
                pdfDelete(modelPdf)
            }else if(itemId==2){
                pdfShare(modelPdf)
            }
            true
        }

    }

    private fun pdfShare(modelPdf: ModelPdf) {
        Log.d(TAG, "pdfShare: ")

        val file = modelPdf.file

        val fileUri = FileProvider.getUriForFile(mContext,"com.main.mainproject.fileprovider",file)

        val intent  = Intent(Intent.ACTION_SEND)
        intent.type = "application/pdf"
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.putExtra(Intent.EXTRA_STREAM,fileUri)
        startActivity(Intent.createChooser(intent,"Share PDF"))
    }

    private fun pdfDelete(modelPdf: ModelPdf) {
        Log.d(TAG, "pdfDelete: ")

        val dialog = AlertDialog.Builder(mContext)

        dialog.setTitle("Delete File")
            .setMessage("Are you sure want to delete ${modelPdf.file.name} ?")
            .setPositiveButton("DELETE"){dialog,which ->

                try {
                    modelPdf.file.delete()

                    Toast.makeText(mContext,"Deleted Successfully...",Toast.LENGTH_SHORT).show()
                    loadPdfDocuments()
                }catch (e:Exception){
                    Toast.makeText(mContext,"Failed to delete due to ${e.message}",Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "pdfDelete: ",e)
                }

            }
            .setNegativeButton("CANCEL"){dialog,which->

                dialog.dismiss()

            }
            .show()
    }


    private fun pdfRename(modelPdf:ModelPdf){
        Log.d(TAG, "pdfRename:  ")

        val view  = LayoutInflater.from(mContext).inflate(R.layout.dialog_rename,null)
        val pdfRenameEt = view.findViewById<EditText>(R.id.pdfRenameEt)
        val renameBtn= view.findViewById<Button>(R.id.renameBtn)

        val prevName = "${modelPdf.file.nameWithoutExtension}"

        pdfRenameEt.setText(prevName)
        Log.d(TAG, "pdfRename: prevName: $prevName")
        val  builder =MaterialAlertDialogBuilder(mContext)
        builder.setView(view)

        val alertDialog = builder.create()
        alertDialog.show()

        renameBtn.setOnClickListener {
            val newName = pdfRenameEt.text.toString().trim()
            Log.d(TAG, "pdfRename: newName: $newName")

            if(newName.isEmpty()){
                Toast.makeText(mContext,"Enter Name...!",Toast.LENGTH_SHORT).show()
            }else{
                try {

                    val newFile = File(mContext.getExternalFilesDir(null),Constants.PDF_FOLDER+"/"+newName+".pdf")

                    modelPdf.file.renameTo(newFile)

                    Toast.makeText(mContext,"Renamed Successfully...",Toast.LENGTH_SHORT).show()

                    loadPdfDocuments()

                }catch (e: Exception){
                    Toast.makeText(mContext,"Failed to rename due to ${e.message}",Toast.LENGTH_SHORT).show()

                    Log.d(TAG, "pdfRename: ",e)
                }

                alertDialog.dismiss()
            }
        }
    }

}