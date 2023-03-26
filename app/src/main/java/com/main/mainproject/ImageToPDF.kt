package com.main.mainproject.com.main.mainproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.main.mainproject.ImageListFragment
import com.main.mainproject.PDFListFragment
import com.main.mainproject.R

class ImageToPDF : AppCompatActivity() {

    private lateinit var bottomNavigationView:BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_to_pdf)

        bottomNavigationView = findViewById(R.id.bottomNavigationMenu)


        loadImagesFragment()

        bottomNavigationView.setOnItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.bottomMenuImages -> {
                    loadImagesFragment()
                }
                R.id.bottomMenuPDF -> {
                    loadPDFFragment()
                }
            }
            return@setOnItemSelectedListener true
        }
    }

    private fun loadImagesFragment(){
        title = "Images"
        val imageListFragment = ImageListFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,imageListFragment,"ImageListFragment")
        fragmentTransaction.commit()
    }

    private fun loadPDFFragment(){
        title = "PDFs"
        val pdfListFragment = PDFListFragment()
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, pdfListFragment,"PDFListFragment")
        fragmentTransaction.commit()
    }
}