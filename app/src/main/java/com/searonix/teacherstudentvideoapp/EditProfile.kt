package com.searonix.teacherstudentvideoapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfile: AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        //get intent from key in profile activity, set nameEditText as it
        var editName = intent?.getStringExtra(USERNAME_KEY)
        if (!editName.isNullOrBlank()) {
            nameEditText.setText(editName)
        }

        //when button pressed, finalize result as okay, otherwise default it to cancel
        setResult(Activity.RESULT_CANCELED)

        submit_button.setOnClickListener {
            //check if new name is valid and then send it back to ProfileActivity
            val resultIntent = Intent()
            val finalName = nameEditText.text.toString()
            resultIntent.putExtra(USERNAME_KEY, finalName)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

    }
}