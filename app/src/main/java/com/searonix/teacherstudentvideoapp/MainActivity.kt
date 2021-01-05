package com.searonix.teacherstudentvideoapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    val REQUEST_SIGN_IN = 413
    //camera fun
    val REQUEST_PERMISSION = 1
    val REQUEST_TAKE_PHOTO = 12
    lateinit var currentPhotoPath: String
    val arrayOfFiles: MutableList<String> = ArrayList()
    val arrayOfFileDates: MutableList<String> = ArrayList()

    //initialize Recyclerview+Adapter
    private var mAdapter: PictureListAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Action Button for Camera
        //on buttonpress, check if there is a new photo taken. If yes, display the photo on the screen.
        val fab: View = findViewById(R.id.fab)
        fab.setOnClickListener {
            //requires version 23 and up
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //check if permissions have not been granted
                if ((checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED )
                    || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    //cannot start camera, ask for permissions
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_PERMISSION)
                }

                else {
                    //all permissions granted
                    //check for image directory for user, if not there create it
                    directoryCreateAndCheck()
                    //take picture
                    dispatchTakePictureIntent()
                }
            }
            else {
                //doesnt require version 23 or up
                //check for image directory for user, if not there create it
                directoryCreateAndCheck()
                //take picture
                dispatchTakePictureIntent()
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
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/${Firebase.auth.uid}")
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = storageDir.toString()
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
                    getString(R.string.fileprovider_authority_label),
                    it
                )

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                val chooser = Intent.createChooser(takePictureIntent, getString(R.string.choose_pixel_label))
                startActivityForResult(chooser, REQUEST_TAKE_PHOTO)
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
            REQUEST_SIGN_IN)
        // [END authUI intent]
    }

    // Receive the permissions request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        var allPermissionsGranted = true

        when (requestCode) {
            REQUEST_PERMISSION ->{
                for (i in permissions.indices){
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false
                        if (permissions[i]=="android.permission.CAMERA") {
                            Toast.makeText(this, getString(R.string.user_deny_text_camera), Toast.LENGTH_SHORT).show()
                        }
                        if (permissions[i] =="android.permission.WRITE_EXTERNAL_STORAGE"){
                            Toast.makeText(this, getString(R.string.user_deny_text_write), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                if (allPermissionsGranted){
                    dispatchTakePictureIntent()
                }
            }

        }
    }


    // [START auth_fui_result]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SIGN_IN) {
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
        if (requestCode==REQUEST_TAKE_PHOTO){
            if (resultCode==Activity.RESULT_OK){
                //successful camera picture, do nothing

            }

            else{
                //camera did not take picture
                //Array path set up to delete temp files first
                val path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/${Firebase.auth.uid}"
                //directory
                val directory = File(path)
                if (directory.exists()) {
                    //check if directory exists
                    val files = directory.listFiles()
                    if (!files.isNullOrEmpty()) {
                        //folder not empty or null
                        for (f in files){
                            if (f.length().toInt()==0){
                                f.delete()
                            }
                        }
                    }

                }

            }

        }
    }
    // [END auth_fui_result]

    private fun directoryCreateAndCheck() {
        //checks for image directory for current user, if its not there, it is created
        currentPhotoPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/${Firebase.auth.uid}"
        val userImageDirectory = File(currentPhotoPath)
        if (!userImageDirectory.exists()){
            userImageDirectory.mkdir()
            Log.v("DirectoryCreationCheck", "(created) LOCATION OF DIRECTORY: " +currentPhotoPath + " DIRECTORY IS: " + userImageDirectory.exists())
        }

        var counts = File(currentPhotoPath).listFiles().count().toString()
        Log.v("DirectoryCreationCount", "Number of images in folder: " + counts)
        Log.v("DirectoryCreationCheck", "(already exists) LOCATION OF DIRECTORY: " +currentPhotoPath)
    }


    private fun countFilesinDirectoryandImageview(){
        //check if directory exists and count pics in folder if yes

        //Array path set up
        val path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/${Firebase.auth.uid}"

        //directory find
        val directory = File(path)

        Log.d("Count Files", "Path: $path")
        if (directory.exists()) {
            //check if directory exists
            val files = directory.listFiles()
            if (files.isNullOrEmpty()) {
                //folder does not have files yet
                //do nothing or return empty arraylist
                Log.v("Count Files", "Empty, Number of pics in folder: " + files.size)
            } else {
                //folder has files in it, add them to existing array
                Log.v("Count Files", "Not empty, Number of pics in folder: " + files.size)
                for (i in files.indices) {
                    arrayOfFiles.add(files[i].name)
                    //ADD TO FILE DATE ARRAY
                    val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS")
                    arrayOfFileDates.add(formatter.format(files[i].lastModified()))
                    Log.v("Count Files", "Added FileName:" + files[i].name)
                }

            }
        }
        else{
            //directory does not exist
        }
    }

    private fun signOut() {
        // authUI signout
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                showSignInOptions()
            }
    }

    override fun onResume() {
        super.onResume()

        val path = getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/${Firebase.auth.uid}"

        Log.v("ArrayCheck", "CURRENT PATH OUTSIDE OF LOOP: " + path)

        //get directory count
        countFilesinDirectoryandImageview()

        if(mAdapter==null){
            //IF CHECK IF ADAPTER IS NULL THEN MAKE IT, IF NOT USE SET FUNCTIONS, THEN UPDATE IT WITH NOTIFYDATASETCHANGE IN OTHER CLASS
            mAdapter = PictureListAdapter(this, path, arrayOfFiles, arrayOfFileDates)
            Log.v("ArrayCheck", "CURRENT PATH NEW: " + path)
            // Connect the adapter with the RecyclerView.
            recyclerview.adapter = mAdapter
            //layout manager
            recyclerview.layoutManager = LinearLayoutManager(this)
        }
        else{
            Log.v("ArrayCheck", "CURRENT PATH OLD: " + path)

            //set functions
            arrayOfFiles.clear()
            countFilesinDirectoryandImageview()
            mAdapter!!.setNewListOfFiles(path,arrayOfFiles,arrayOfFileDates)

        }

//check list also to see why duplicate
        //get picasso fir images

        Log.v("ArrayCheck", "array dates: " + arrayOfFileDates)
        Log.v("ArrayCheck", "array result: " + arrayOfFiles)

    }

}