package com.searonix.teacherstudentvideoapp

//import com.google.firebase.quickstart.auth.R
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger

class MainActivity : AppCompatActivity() {

    lateinit var providers: List<AuthUI.IdpConfig>
    val RC_SIGN_IN = 413

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //button and menu set up
        button_sign_out.setOnClickListener(){
            AuthUI.getInstance().signOut(this@MainActivity)
                .addOnCompleteListener {
                    button_sign_out.isEnabled = false
                    showSignInOptions()
                }
                .addOnFailureListener{
                    e-> Toast.makeText(this@MainActivity, e.message,Toast.LENGTH_SHORT).show()

                }

        }


        //create AuthUi intent
        providers = Arrays.asList<AuthUI.IdpConfig> (
            AuthUI.IdpConfig.EmailBuilder().build(), //email login
            AuthUI.IdpConfig.GoogleBuilder().build(), //google login
            AuthUI.IdpConfig.FacebookBuilder().build() //facebook login
        )

        //check if user is logged in/registered
        val user = Firebase.auth.currentUser
        if (user == null) //not logged in
        {
            //show login/register options, hide button
            button_sign_out.isEnabled = false
            showSignInOptions()
        }
        else {
            //stay on page and show logout button
            button_sign_out.isEnabled = true

        }

    }

    private fun showSignInOptions(){
        // Create and launch sign-in intent
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN)
        // [END authUI intent]
    }

    // [START auth_fui_result]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(this, ""+user!!.email, Toast.LENGTH_SHORT).show()
                button_sign_out.isEnabled = true

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.

                // user has cancelled or gone back, so just finish() to end activity
                Toast.makeText(this, ""+response!!.error!!.message, Toast.LENGTH_SHORT).show()
                //finish()
            }
        }
    }
    // [END auth_fui_result]

    private fun signOut() {
        // authUI signout
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                // ...
            }
    }

    private fun delete() {
        // authUI delete
        AuthUI.getInstance()
            .delete(this)
            .addOnCompleteListener {
                // ...
            }
    }

}