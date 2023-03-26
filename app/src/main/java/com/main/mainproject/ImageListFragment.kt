package com.main.mainproject

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.drawable.DrawableResource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.main.mainproject.adapters.AdapterImage
import com.main.mainproject.models.ModelImage
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors


class ImageListFragment : Fragment() {

    companion object{
        private const val TAG = "IMAGES_LIST_TAG"
    }


    private var imageUri: Uri?=null
    private lateinit var imagesRv:RecyclerView
    private lateinit var addImageFab:FloatingActionButton
    private  lateinit var progressDialog:ProgressDialog
    private lateinit var mContext: Context

    private lateinit var allImageArrayList:ArrayList<ModelImage>
    private lateinit var adapterImage: AdapterImage

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    //    cameraPermissions = arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE)
     //   storagePermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        addImageFab = view.findViewById(R.id.addImageFab)

       imagesRv = view.findViewById(R.id.imagesRv)
        progressDialog = ProgressDialog(mContext)
        progressDialog.setTitle("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)
         loadImages()
        view.findViewById<ImageView>(R.id.imagesItemPdf).setOnClickListener {
            val builder = MaterialAlertDialogBuilder(mContext)
            builder.setTitle("Convert to Pdf")
                .setMessage("Convert all/selected images to pdf ")
                .setPositiveButton("CONVERT ALL"){dialog,which->
                    convertImagesToPdf(true)
                }
                .setNeutralButton("CONVERT SELECTED IMAGES"){dialog,which->
                    convertImagesToPdf(false)
                }
                .setNegativeButton("CANCEL"){dialog,which->
                    dialog.dismiss()
                }.show()
        }

        view.findViewById<ImageView>(R.id.imageItemDelete).setOnClickListener {
            val builder = MaterialAlertDialogBuilder(mContext)
            builder.setTitle("Delete Image(s)")
                .setMessage("Are you sure you want to delete all selected Images?")
                .setPositiveButton("DELETE ALL"){dialog,which ->
                    deleteImages(true)
                }
                .setNeutralButton("DELETE SELECTED"){dialog,which ->
                    deleteImages(false)
                }
                .setNegativeButton("CANCEL") { dialog, which ->
                    dialog.dismiss()

                }.show()
        }
        addImageFab.setOnClickListener{
                showInputImageDialog()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)

    }

/*    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.menu_images,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }*/

/*    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val itemId = item.itemId
        if(itemId==R.id.imageItemDelete){
            val builder = AlertDialog.Builder(mContext)
            builder.setTitle("Delete Image(s)")
                .setMessage("Are you sure you want to delete all selected Images?")
                .setPositiveButton("DELETE ALL"){dialog,which ->
                    deleteImages(true)
                }
                .setNeutralButton("DELETE SELECTED"){dialog,which ->
                    deleteImages(false)
                }
                .setNegativeButton("CANCEL"){dialog,which ->
                    dialog.dismiss()

                }
                .show()
        }else if(itemId == R.id.imagesItemPdf){

            val builder = AlertDialog.Builder(mContext)
            builder.setTitle("Convert to Pdf")
                .setMessage("Convert all/selected images to pdf ")
                .setPositiveButton("CONVERT ALL"){dialog,which->
                    convertImagesToPdf(true)
                }
                .setNeutralButton("CONVERT SELECTED IMAGES"){dialog,which->
                    convertImagesToPdf(false)
                }
                .setNegativeButton("CANCEL"){dialog,which->
                    dialog.dismiss()
                }.show()
        }
        return super.onOptionsItemSelected(item)
    }*/


    /*private fun convertImagesToPdf(convertAll:Boolean){
        Log.d(TAG, "convertImagesToPdf: convertAll: $convertAll")

        progressDialog.setMessage("Converting to PDF....")
        progressDialog.show()
        val executorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executorService.execute{
            Log.d(TAG, "convertImagesToPdf: BG Work...")
            var imagesToPdfList = ArrayList<ModelImage>()

            if(convertAll){
                imagesToPdfList = allImageArrayList
            }else{
                for (i in allImageArrayList.indices){
                    if(allImageArrayList[i].checked){
                        imagesToPdfList.add(allImageArrayList[i])
                    }
                }
            }

            Log.d(TAG, "convertImagesToPdf: imagesToPdfList Size: ${imagesToPdfList.size}")
        //    toast("${imagesToPdfList.size}")

            try {
                val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                val root = File(mContext.getExternalFilesDir(null),Constants.PDF_FOLDER)
                root.mkdirs()
                val timestamp = System.currentTimeMillis()
                val fileName  = "PDF_$timestamp.pdf"

                Log.d(TAG, "convertImagesToPdf: fileName: $fileName")
//                Toast.makeText(mContext,   fileName,Toast.LENGTH_SHORT).show()
              //  val file1 = File(folder,fileName)
                val file2 = File(root,fileName)
              //  val fileOutputStream1 = FileOutputStream(file1)
                val fileOutputStream2 = FileOutputStream(file2)
                val pdfDocument = PdfDocument()
                for(i in imagesToPdfList.indices){
                    val imageToPdfUri = imagesToPdfList[i].imageUri
                    try {

                        var  bitmap: Bitmap
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(mContext.contentResolver,imageToPdfUri))
                        }else{
                            bitmap = MediaStore.Images.Media.getBitmap(mContext.contentResolver,imageToPdfUri)

                        }

                        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888,false)

                        val pageInfo = PageInfo.Builder(bitmap.width,bitmap.height,i+1).create()
                        val page = pdfDocument.startPage(pageInfo)

                        val paint = Paint()

                        paint.color = Color.WHITE
                        val canvas = page.canvas
                        canvas.drawPaint(paint)
                        canvas.drawBitmap(bitmap,0f,0f,null)
                        pdfDocument.finishPage(page)

                        bitmap.recycle()
                    }
                    catch (e:Exception){
                        Log.d(TAG, "convertImagesToPdf: ",e)
                    }
                }

             //   pdfDocument.writeTo(fileOutputStream1)
                pdfDocument.writeTo(fileOutputStream2)
                pdfDocument.close()

            }catch (e:Exception){
                Log.d(TAG, "convertImagesToPdf: ",e)
            }

            handler.post{
                Log.d(TAG, "convertImagesToPdf: Converted.....")
                progressDialog.dismiss()
                Toast.makeText(mContext,"Converted...",Toast.LENGTH_SHORT).show()
            }
        }
    }
*/
    private fun convertImagesToPdf(convertAll: Boolean) {
        Log.d(TAG, "convertImagesToPdf: convertAll: $convertAll")

        progressDialog.setMessage("Converting to PDF....")
        progressDialog.show()
        var count = 0
        val executorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executorService.execute {
            Log.d(TAG, "convertImagesToPdf: BG Work...")
            var imagesToPdfList = ArrayList<ModelImage>()

            if (convertAll) {
                count = allImageArrayList.size
                imagesToPdfList = allImageArrayList
            } else {
                for (i in allImageArrayList.indices) {
                    if (allImageArrayList[i].checked) {
                        count = count+1
                        imagesToPdfList.add(allImageArrayList[i])
                    }
                }
            }

            Log.d(TAG, "convertImagesToPdf: imagesToPdfList Size: ${imagesToPdfList.size}")
            if(count==0){
                Log.d(TAG, "convertImagesToPdf:Canceled")
            }else{
            try {
                val timestamp = System.currentTimeMillis()
                val fileName = "PDF_$timestamp.pdf"

                // Save PDF in Documents folder
                val contentResolver = mContext.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS )
                }
                val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
                val fileOutputStream1 = uri?.let { contentResolver.openOutputStream(it) }

                Log.d(TAG, "convertImagesToPdf: $fileOutputStream1")
                val root = File(mContext.getExternalFilesDir(null), Constants.PDF_FOLDER)
                root.mkdirs()
                val file2 = File(root, fileName)
                val fileOutputStream2 = FileOutputStream(file2)
                Log.d(TAG, "convertImagesToPdf: $file2")
                val pdfDocument = PdfDocument()

                for (i in imagesToPdfList.indices) {
                    val imageToPdfUri = imagesToPdfList[i].imageUri
                    try {
                        var bitmap: Bitmap
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, imageToPdfUri))
                        } else {
                            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageToPdfUri)
                        }
                        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false)

                        val pageInfo = PageInfo.Builder(bitmap.width, bitmap.height, i + 1).create()
                        val page = pdfDocument.startPage(pageInfo)

                        val paint = Paint()
                        paint.color = Color.WHITE
                        val canvas = page.canvas
                        canvas.drawPaint(paint)
                        canvas.drawBitmap(bitmap, 0f, 0f, null)
                        pdfDocument.finishPage(page)

                        bitmap.recycle()
                    } catch (e: Exception) {
                        Log.d(TAG, "convertImagesToPdf: ", e)
                    }
                }

                pdfDocument.writeTo(fileOutputStream1)
                pdfDocument.writeTo(fileOutputStream2)
                pdfDocument.close()

            } catch (e: Exception) {
                Log.d(TAG, "convertImagesToPdf: ", e)
            }}

            handler.post {
                progressDialog.dismiss()
                if(count!=0){
                    Log.d(TAG, "convertImagesToPdf: Converted.....")
                    Toast.makeText(mContext, "Converted...", Toast.LENGTH_SHORT).show()
            }else{
                    toast("No Image is Selected")
            }}

        }
    }


    private fun deleteImages(deleteAll:Boolean){
        var count = 0
        var imagesToDeleteList = ArrayList<ModelImage>()
        if(deleteAll){
            count = allImageArrayList.size
            imagesToDeleteList = allImageArrayList
        }else{
            for(modelImage in allImageArrayList){
                if(modelImage.checked){
                    count=count+1
                    imagesToDeleteList.add(modelImage)
                }
            }
        }
        if(count==0){
            toast("No Image is Selected")
        }else {
            for (modelImage in imagesToDeleteList) {
                try {

                    val pathOfImageToDelete = "${modelImage.imageUri.path}"
                    val file = File(pathOfImageToDelete)
                    if (file.exists()) {

                        val isDeleted = file.delete()
                        Log.d(TAG, "deleteImages: File Deleted: $isDeleted")

                    }

                } catch (e: Exception) {
                    Log.d(TAG, "deleteImages: ", e)
                }
            }

            Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show()
            loadImages()
        }

    }

    private fun loadImages(){
        Log.d(TAG,"loadImages: ")

        allImageArrayList = ArrayList<ModelImage>()
        adapterImage = AdapterImage(mContext,allImageArrayList)
        imagesRv.adapter = adapterImage

        val folder = File(mContext.getExternalFilesDir(null),Constants.IMAGES_FOLDER)

        if(folder.exists() ){
            Log.d(TAG,"loadImages: Folder exists")
            val files = folder.listFiles()
            if(files!=null){
                Log.d(TAG,"loadImages: Folder have files ${files.size}")
                for(file in files){
                    Log.d(TAG,"loadImages: fileName: ${file.name}")

                    val imageUri = Uri.fromFile(file)

                    val modelImage = ModelImage(imageUri,false)

                    allImageArrayList.add(modelImage)
                    adapterImage.notifyItemInserted(allImageArrayList.size)
                }
            }else{
                Log.d(TAG,"loadImages: Folder exists but have no files")
            }
        }else{
             Log.d(TAG,"loadImages: Folder doesn't exists ")
        }
    }

    private fun saveImageToAppLevelDirectory(imageUriToBeSaved:Uri){
        Log.d(TAG,"saveImageToAppLevelDirectory: $imageUriToBeSaved")

        try {

            val bitmap:Bitmap
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                bitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(mContext.contentResolver, imageUriToBeSaved))
            }else{
                bitmap = MediaStore.Images.Media.getBitmap(mContext.contentResolver,imageUriToBeSaved)
            }

            val directory = File(mContext.getExternalFilesDir(null),Constants.IMAGES_FOLDER)
            directory.mkdirs()


            val timestamp = System.currentTimeMillis()
            val fileName = "$timestamp.jpeg"

            val file = File(mContext.getExternalFilesDir(null),"${Constants.IMAGES_FOLDER}/$fileName")

            try {
                val fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos)
                fos.flush()
                fos.close()
                Log.d(TAG,"saveImageToAppLevelDirectory: Saved")
                toast("Image Saved")
            }catch (e:Exception){
                Log.d(TAG,"saveImageToAppLevelDirectory: ",e)
                Log.d(TAG,"saveImageToAppLevelDirectory: Failed to save the image due to ${e.message}")

                toast("Failed to save the image due to ${e.message}")
            }

        }catch (e: Exception){
            Log.d(TAG,"saveImageToAppLevelDirectory: ",e)
            Log.d(TAG,"saveImageToAppLevelDirectory: Failed prepare the image due to ${e.message}")
            toast("Failed prepare the image due to ${e.message}")


        }
    }

    private fun showInputImageDialog(){
        Log.d(TAG,"showInputImageDialog: ")
       // val wrapper = ContextThemeWrapper(mContext, R.style.MyPopupMenuStyle)
        val popupMenu = PopupMenu(mContext, addImageFab)
        popupMenu.menu.add(Menu.NONE, 1,1,"CAMERA")
        popupMenu.menu.add(Menu.NONE, 2,2,"GALLERY")

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener {menuItem ->

            val itemId = menuItem.itemId
            if(itemId == 1){
                Log.d(TAG,"showInputImageDialog: Camera is Clicked, check if camera permissions are granted or not")
                if(checkCameraPermission()){
                    pickImageCamera()
                }else{
                requestCameraPermission.launch(arrayOf(Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE))
                }

            }else if(itemId==2){
                Log.d(TAG,"showInputImageDialog: Gallery is Clicked, check if camera permissions are granted or not")
                if(checkStoragePermission()){
                    pickImageGallery()
                }else{

                    requestStoragePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }


            return@setOnMenuItemClickListener true
        }
    }

    private fun pickImageGallery(){
        Log.d(TAG,"pickImageGallery: ")

        val intent = Intent(Intent.ACTION_PICK)

        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        galleryActivityResultLauncher.launch(intent)
    }

  /*  private val galleryActivityResultLauncher = registerForActivityResult<Intent,ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ){result ->
            if(result.resultCode == Activity.RESULT_OK){
                val data = result.data

                imageUri = data!!.data
                Log.d(TAG,"galleryActivityResultLauncher: Gallery Image: $imageUri")
                saveImageToAppLevelDirectory(imageUri!!)


                val modelImage = ModelImage(imageUri!!,false)
                allImageArrayList.add(modelImage)
                adapterImage.notifyItemInserted(allImageArrayList.size)
            }else{
                Log.d(TAG,"galleryActivityResultLauncher: Cancelled")
                toast("Cancelled")
            }
        }*/
  private val galleryActivityResultLauncher = registerForActivityResult<Intent,ActivityResult>(
      ActivityResultContracts.StartActivityForResult()
  ){ result ->
      if(result.resultCode == Activity.RESULT_OK){
          val data = result.data
          val clipData = data?.clipData

          if (clipData != null) {
              // multiple images selected
              for (i in 0 until clipData.itemCount) {
                  val imageUri = clipData.getItemAt(i).uri
                  saveImageToAppLevelDirectory(imageUri)

                  val modelImage = ModelImage(imageUri, false)
                  allImageArrayList.add(modelImage)
                  adapterImage.notifyItemInserted(allImageArrayList.size)
              }
          } else {
              // single image selected
              val imageUri = data?.data
              if (imageUri != null) {
                  saveImageToAppLevelDirectory(imageUri)
              }

              val modelImage = ModelImage(imageUri!!, false)
              allImageArrayList.add(modelImage)
              adapterImage.notifyItemInserted(allImageArrayList.size)
          }
      } else {
          Log.d(TAG,"galleryActivityResultLauncher: Cancelled")
          toast("Cancelled")
      }
  }


    private fun pickImageCamera(){
        Log.d(TAG,"pickImageCamera: ")

        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE,"TEMP IMAGE TITLE")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION,"TEMP IMAGE DESCRIPTION")

        imageUri = mContext.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri)
        cameraActivityResultLauncher.launch(intent)

    }

    private val cameraActivityResultLauncher = registerForActivityResult<Intent,ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ){
        result ->
            if(result.resultCode==Activity.RESULT_OK){
            Log.d(TAG,"cameraActivityResult: Camera Image: $imageUri")

                saveImageToAppLevelDirectory(imageUri!!)


                val modelImage = ModelImage(imageUri!!,false)
                allImageArrayList.add(modelImage)
                adapterImage.notifyItemInserted(allImageArrayList.size)

            }else{
                Log.d(TAG,"cameraActivityResult: Cancelled")
                toast("Cancelled...")
            }
    }


    private fun checkStoragePermission():Boolean{
        Log.d(TAG,"checkStoragePermission: ")

    return ContextCompat.checkSelfPermission(mContext,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
    }

 /*   private fun requestStoragePermission(){
        Log.d(TAG,"requestStoragePermission: ")

        requestPermissions(storagePermissions, STORAGE_REQUEST_CODE)

        Log.d(TAG, "requestStoragePermission:  y")
    }*/

    private var requestStoragePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
        ActivityResultCallback<Boolean> {isGranted ->

            Log.d(TAG, "requestStoragePermission: isGraneted: $isGranted")

            if(isGranted){
                pickImageGallery()
            }else{
                toast("Permission denied...")
            }

        }
    )

    private fun checkCameraPermission():Boolean{
        Log.d(TAG,"checkCameraPermission: ")

        val cameraResult = ContextCompat.checkSelfPermission(mContext,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED
        val storageResult = ContextCompat.checkSelfPermission(mContext,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
        return cameraResult&&storageResult
    }

/*    private  fun requestCameraPermission(){
        Log.d(TAG, "requestCameraPermission: ")
        requestPermissions(cameraPermissions, CAMERA_REQUEST_CODE)

    }*/

    private var requestCameraPermission = registerForActivityResult(

        ActivityResultContracts.RequestMultiplePermissions(),
        ActivityResultCallback<Map<String,Boolean>>{result ->
            Log.d(TAG, "requestCameraPermission: ")
            Log.d(TAG, "requestCameraPermission: $result")

            var areAllGranted = true
            for(isGranted in result.values){
                Log.d(TAG, "requestCameraPermission: isGranted: $isGranted")
                areAllGranted = true//areAllGranted&&isGranted
            }

            if(areAllGranted){
                pickImageCamera()
                Log.d(TAG, "requestCameraPermission: All Premission granted...")
            }else{
                toast("Camera or Storage Permission Denied")
            }
            
        }

    )

/*    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG,"onRequestPermissionResult: ")

        when(requestCode){
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()){
                    val cameraAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1]==PackageManager.PERMISSION_GRANTED

                    if(cameraAccepted&&storageAccepted){

                        Log.d(TAG,"onRequestPermissionResult: both permissions(Camera & Gallery) are granted, we can launch camera intent")
                    }else{
                        Log.d(TAG,"onRequestPermissionResult: Camera & StoragePermissions are required")
                        toast("Camera & StoragePermissions   are required")
                    }
                }else{
                    toast("Cancelled")
                }
            }
            STORAGE_REQUEST_CODE ->{
                if(grantResults.isNotEmpty()){

                    val storageAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED

                    if(storageAccepted){
                        Log.d(TAG,"onRequestPermissionResult: storage permission granted, we can launch gallery intent")

                        pickImageGallery()
                    }else{
                        Log.d(TAG,"onRequestPermissionResult: storage permission denied, can't launch the gallery intent")
                    toast("Storage Permission is Required")
                    }
                }else{

                    Log.d(TAG,"onRequestPermissionResult: Neither allowed not denied, rather cancelled")
                    toast("Cancelled")
                }
            }
        }
    }*/

    private fun toast(message:String){
        Toast.makeText(mContext,message,Toast.LENGTH_SHORT).show()
    }
}