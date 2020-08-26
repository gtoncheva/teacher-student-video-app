package com.searonix.teacherstudentvideoapp

//import com.google.firebase.quickstart.auth.R
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
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
import kotlin.math.sign

class MainActivity : AppCompatActivity() {

    lateinit var providers: List<AuthUI.IdpConfig>
    val RC_SIGN_IN = 413

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //create AuthUi intent
        providers = Arrays.asList<AuthUI.IdpConfig> (
            AuthUI.IdpConfig.EmailBuilder().build(), //email login
            AuthUI.IdpConfig.GoogleBuilder().build(), //google login
            AuthUI.IdpConfig.FacebookBuilder().build() //facebook login
        )

        //check if user is logged in/registered
        val user = Firebase.auth.currentUser
        if (user == null || user.isAnonymous) //not logged or user is anonymously logged in
        {
            //show login/register options
            showSignInOptions()

        }
        else {
            //show menu with logout options

        }

    }

//Menu Section
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    //inflate menu, this will add items to action bar
    val inflater: MenuInflater = menuInflater
    inflater.inflate(R.menu.mainmenu, menu)
    return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.sign_out_button -> {
                signOut()
                showSignInOptions()
                true
            }

            else -> super.onOptionsItemSelected(item)
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

                //get user and then check if user is anonymous or not
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    if (user.isAnonymous){
                        showSignInOptions()
                    }
                    else {
                        Toast.makeText(this, "${user.email} has successfully Signed In", Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                if (response==null){
                    finish()
                }
                else {
                    //Sign in failed due to some other reason
                }
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

}