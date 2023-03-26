package com.main.mainproject

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.main.mainproject.com.main.mainproject.Uploader
import com.main.mainproject.com.main.mainproject.adapters.AdapterDocs
import com.main.mainproject.com.main.mainproject.models.ModelDocs
import java.io.File


class DocsListFragment(private val roomCode:String) : Fragment() {
    val myName = Firebase.auth.currentUser!!.displayName
    val myUid = Firebase.auth.currentUser!!.uid
    private lateinit var roomDocsListArray:ArrayList<ModelDocs>
    private lateinit var tempArrayList:ArrayList<ModelDocs>
    private lateinit var mContext:Context
    private lateinit var addDocsFab:FloatingActionButton
    private lateinit var docsRv:RecyclerView
    val PICKFILE_RESULT_CODE = 100
    private var storage = Firebase.storage
    var storageRef = storage.reference
    private  var counter:Int = 0
    private lateinit var database: DatabaseReference
    private lateinit var adapterDocs: AdapterDocs


    companion object{

        private const val TAG = "DOCS_LIST_TAG"

    }
    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        database = Firebase.database.reference
        return inflater.inflate(R.layout.fragment_docs_list, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addDocsFab = view.findViewById(R.id.addDocFab)
        docsRv = view.findViewById(R.id.docsRv)
        loadDocs()
        addDocsFab.setOnClickListener{
            var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
            chooseFile.setType("*/*")
            chooseFile = Intent.createChooser(chooseFile,"Choose a File")
            startActivityForResult(chooseFile, PICKFILE_RESULT_CODE)
        }
    }
    private fun loadDocs() {
        val timestamp= System.currentTimeMillis()
        Log.d(TAG, "loadDocs: $timestamp")
        roomDocsListArray = ArrayList()
        tempArrayList = ArrayList()
        database = Firebase.database.reference
        adapterDocs = AdapterDocs(mContext,tempArrayList,object:RvListinerDocs{
            override fun onDownloadClick(modelDocs: ModelDocs, position: Int) {
                downloadFile(modelDocs.fileName)
            }

            override fun onRoomMoreClick(
                modelDocs: ModelDocs,
                position: Int,
                holder: AdapterDocs.HolderDocs
            ) {
                if(modelDocs.upUid==myUid){
                deleteDoc(modelDocs,holder)
                }
            }

        },myUid)

        docsRv.adapter = adapterDocs

        database.child("FilesUploaded").child(roomCode.toString()).addListenerForSingleValueEvent(
            object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.value!=null){
                        Log.d(TAG, "onDataChange: $snapshot")
                    val readData = snapshot.value as Map<String,Map<String,Any>>
                    for(each in readData){
                        val modelDocs = ModelDocs(each.value.get("name").toString(),each.value.get("uploader").toString(),each.value.get("size").toString(),each.value.get("date").toString(),each.value.get("uid").toString(),each.key)
                        roomDocsListArray.add(modelDocs)
                  //      adapterDocs.notifyItemInserted(roomDocsListArray.size)

                    }
                        roomDocsListArray.sortByDescending {
                            it.uniqueVal
                        }
                        for (each in roomDocsListArray){
                            tempArrayList.add(each)
                            Log.d(TAG, "onDataChange: ${each.uniqueVal}")
                            adapterDocs.notifyItemInserted(tempArrayList.size)
                        }
                }}

                override fun onCancelled(error: DatabaseError) {
                }

            }
        )

        Log.d(TAG, "loadDocs: trvfdc")


    }

    private fun deleteDoc(modelDocs: ModelDocs, holder: AdapterDocs.HolderDocs) {
        Log.d(TAG, "deleteDoc: ")
        val popupMenu = PopupMenu(mContext,holder.morebtn)

        popupMenu.menu.add(Menu.NONE,0,0,"Delete")
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { menuItem->
            val itemId = menuItem.itemId
            if(itemId==0){
                val builder = MaterialAlertDialogBuilder(mContext)
                builder.setTitle("Delete Document")
                    .setMessage("Are you sure you want to delete the Document?")
                    .setPositiveButton("NO"){dialog,which->
                        dialog.dismiss()
                    }.setNegativeButton("YES"){dialog,which->
                        database.child("FilesUploaded").child(roomCode.toString()).child(modelDocs.uniqueVal).removeValue()
                        storageRef.child("uploads").child(roomCode.toString()).child(modelDocs.fileName).delete()
                        loadDocs()
                    }.show()
            }
          true
        }
    }

    private fun downloadFile(fileName: String) {
        Log.d(TAG, "downloadFile: ")
        val isLandRef=storageRef.child("uploads/$roomCode/$fileName")

      //  val localFile = File(fileLocation, "$fileName")
        isLandRef.downloadUrl.addOnSuccessListener {
            Log.d(TAG, "downloadFile: $it")
            val storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(storageDirectory, fileName)
            val downloadManager = mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse(it.toString()))
                .setTitle(fileName)
                .setDescription("Downloading")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationUri(Uri.fromFile(file))
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
            downloadManager.enqueue(request)

            Toast.makeText(mContext, "Downloading", Toast.LENGTH_LONG).show()

        }
        /*isLandRef.getFile(localFile).addOnSuccessListener {
            Log.d(TAG, "downloadFile: ${mContext.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}")
            Toast.makeText(mContext,"File Downloaded", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(mContext,"There was Some Error in Downloading", Toast.LENGTH_SHORT).show()

        }*/
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICKFILE_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                uploadFileToFirebaseStorage(uri)
            }
        }
    }
    private fun uploadFileToFirebaseStorage(fileUri: Uri) {

        var fileName = getFileName(fileUri,mContext)
        val fileRef = storageRef.child("uploads/$roomCode/$fileName")
        Log.d(TAG, "uploadFileToFirebaseStorage: $fileName")
        fileRef.putFile(fileUri)
            .addOnSuccessListener { taskSnapshot ->
                Log.d(TAG, "uploadFileToFirebaseStorage: File uploaded successfully:$fileName")
                val fileSize = taskSnapshot.bytesTransferred.toString()
                val timestamp = System.currentTimeMillis()
                val formattedDate: String = Methods.formatTimestamp(timestamp)
                val uploder = Uploader(fileName.toString(),myUid,formattedDate,myName.toString(),fileSize)
                fileName = fileName.replace(".","")
                Log.d(TAG, "uploadFileToFirebaseStorage: $roomCode   $fileName    $uploder")
                val key = database.child("FilesUploaded").child(roomCode.toString()).push().key
                database.child("FilesUploaded").child(roomCode.toString()).child(key!!).setValue(uploder).addOnSuccessListener {
                    Log.d(TAG, "uploadFileToFirebaseStorage: Doc added to database")
                    Toast.makeText(mContext,"Uploaded",Toast.LENGTH_SHORT).show()
                }.addOnCanceledListener {
                    Log.d(TAG, "uploadFileToFirebaseStorage: Doc failed to be uploaded")
                }.addOnCompleteListener{
                    loadDocs()
                }

            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "uploadFileToFirebaseStorage: File uploaded unsuccessfully")

            }

    }




    @SuppressLint("Range")
    private fun getFileName(uri: Uri, mContext:Context): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = mContext.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.lastPathSegment
        }
        return result!!
    }


}