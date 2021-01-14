package com.searonix.teacherstudentvideoapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_picture_display.*
import java.io.File

class PictureDisplay(): AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_display)

        var imageSource = intent?.getStringExtra(Intent.EXTRA_TEXT)

        Log.v("FileCheck", "File String given is: " + imageSource)

        val loadImage = File(imageSource)
        Picasso.get().load(loadImage).fit().into(picture_display)

        back_button.setOnClickListener {
            //when clicked sends user back to Main
            val startMainActivity = Intent(this, MainActivity::class.java)
            startActivity(startMainActivity)
            finish()
        }
    }
}