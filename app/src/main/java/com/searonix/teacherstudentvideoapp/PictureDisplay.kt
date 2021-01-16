package com.searonix.teacherstudentvideoapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_picture_display.*
import java.io.File

class PictureDisplay(): AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_display)

        // Get a support ActionBar corresponding to this toolbar and enable the Up button
        var toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_picture_display)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.image_gallery_label)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var imageSource = intent?.getStringExtra(Intent.EXTRA_TEXT)

        Log.v("FileCheck", "File String given is: " + imageSource)

        val loadImage = File(imageSource)
        Picasso.get().load(loadImage).fit().into(picture_display)

    }
}