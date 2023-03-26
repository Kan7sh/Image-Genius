package com.main.mainproject

import com.main.mainproject.adapters.AdapterPdf
import com.main.mainproject.com.main.mainproject.adapters.AdapterDocs
import com.main.mainproject.com.main.mainproject.adapters.AdapterNotes
import com.main.mainproject.com.main.mainproject.adapters.AdapterRoom
import com.main.mainproject.com.main.mainproject.models.ModelDocs
import com.main.mainproject.com.main.mainproject.models.ModelNotes
import com.main.mainproject.com.main.mainproject.models.ModelRoom
import com.main.mainproject.models.ModelPdf

interface RvListenerPdf {

    fun onPdfClick(modelPdf: ModelPdf, position: Int)
    fun onPdfMoreClick(modelPdf: ModelPdf, position: Int, holder: AdapterPdf.HolderPdf)
}

interface RvListinerRoom{

    fun onRoomClick(modelroom: ModelRoom, position: Int)
    fun onPdfMoreClick(modelRoom: ModelRoom, position: Int, holder: AdapterRoom.HolderRoom)

}


interface RvListinerDocs{

    fun onDownloadClick(modelDocs: ModelDocs, position: Int)
    fun onRoomMoreClick(modelDocs: ModelDocs, position: Int, holder: AdapterDocs.HolderDocs)

}

interface RvListnerNotes{
    fun onNotesClick(modelNotes:ModelNotes,postion:Int)
    fun onNotesMoreClick(modelNotes:ModelNotes,postion: Int,holder:AdapterNotes.HolderNotes)
}
