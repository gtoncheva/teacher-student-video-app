package com.searonix.teacherstudentvideoapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    lateinit var providers: List<AuthUI.IdpConfig>
    val RC_SIGN_IN = 413
    //camera fun
    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_TAKE_PHOTO = 12
    lateinit var currentPhotoPath: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Action Button for Camera
        //on buttonpress, check if there is a new photo taken. If yes, display the photo on the screen.
        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener {
            //camera
            dispatchTakePictureIntent()
        }

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
            //do nothing

        }

    }

    //camera create image file
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }


//camera fun
private fun dispatchTakePictureIntent() {
    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
        // Ensure that there's a camera activity to handle the intent
        takePictureIntent.resolveActivity(packageManager)?.also {
            // Create the File where the photo should go
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
                //...
                Log.e("IOEceptiopn", ex.message)
                null
            }
            // Continue only if the File was successfully created
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "com.searonix.teacherstudentvideoapp.fileprovider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
            }
        }
    }
}


//Menu Section
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    //inflate menu, this will add items to action bar
    val inflater: MenuInflater = menuInflater
    inflater.inflate(R.menu.main_menu, menu)
    return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.profile_button -> {
                val startProfileActivity = Intent(this, ProfileActivity::class.java)
                startActivity(startProfileActivity)
                true
            }
            R.id.sign_out_button -> {
                signOut()
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
    @RequiresApi(Build.VERSION_CODES.P)
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
                        Toast.makeText(this, "${user.email} has successfully signed in", Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button.
                finish()
            }
        }
        //handle return intent from camera, set picture taken as bitmap and put in imageview
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            //get uri from file
            val f = File(currentPhotoPath)
            val contentUri = Uri.fromFile(f)

            //convert uri into bitmap
            if (android.os.Build.VERSION.SDK_INT >= 29){
                // Use newer version
                val source = ImageDecoder.createSource(this.contentResolver, contentUri)
                val mBitmap = ImageDecoder.decodeBitmap(source)
                imageView.setImageBitmap(mBitmap)}
            else{
                // Use older version
                val mBitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentUri)
                imageView.setImageBitmap(mBitmap)
            }
        }
    }
    // [END auth_fui_result]

    private fun signOut() {
        // authUI signout
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                showSignInOptions()
            }
    }

}