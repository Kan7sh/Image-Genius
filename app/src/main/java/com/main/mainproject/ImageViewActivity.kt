package com.main.mainproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide

class ImageViewActivity : AppCompatActivity() {

    private lateinit var imageIv:ImageView

    private var imageUri = " "

    companion object{
        private const val TAG = "IMAGES_VIEW_TAG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)

        supportActionBar?.title ="Images View"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        imageIv = findViewById(R.id.imageIv)
        imageUri = intent.getStringExtra("imagesUri").toString()
        Log.d(TAG, "onCreate: $imageUri")
        Log.d(TAG,"onCreate: imageUri: $imageUri")
        Glide.with( this)
            .load( imageUri)
            .placeholder(R.drawable.ic_baseline_add_photo_alternate_24)
            .into(imageIv)

    }


    override fun onSupportNavigateUp(): Boolean {

        onBackPressed()
        return super.onSupportNavigateUp()
    }
}