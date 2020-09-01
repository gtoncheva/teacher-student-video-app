package com.searonix.teacherstudentvideoapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_profile.*

val USERNAME_KEY = "sample"

class ProfileActivity: AppCompatActivity()  {
    val LAUNCH_EDIT_PROFILE_ACTIVITY = 612

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val user = Firebase.auth.currentUser
        user?.let {
            // get user's name and set it to textview
            nameTextView.setText(user.displayName)

            //on button press go to editprofile with username added to intent
            editButton.setOnClickListener {
                val editprofileIntent = Intent(this, EditProfile::class.java)
                editprofileIntent.putExtra(USERNAME_KEY, nameTextView.text)
                startActivityForResult(editprofileIntent, LAUNCH_EDIT_PROFILE_ACTIVITY)
            }

        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //get resultcode from editprofile
            if (resultCode == RESULT_OK) {
                //use key from intent sent back to set name to new name

                val finalName = data?.getStringExtra(USERNAME_KEY)
                nameTextView.setText(finalName)
            }
    }


}