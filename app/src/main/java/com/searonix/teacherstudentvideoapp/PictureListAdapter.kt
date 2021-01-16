package com.searonix.teacherstudentvideoapp


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.wordlist_item.view.*
import java.io.File


class PictureListAdapter(context: Context, var imageDirectory: String, var listOfFiles: MutableList<String>, var imageDates: MutableList<String>): RecyclerView.Adapter<PictureListAdapter.PictureViewHolder?>() {

    class PictureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //state 3 views and connect them to XML
        val imageView: ImageView = itemView.show_imageview
        val imageName: TextView = itemView.image_name_textview
        val imageDate: TextView = itemView.image_date_textview

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.wordlist_item, parent, false)

        return PictureViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return listOfFiles.size
    }

//MAKE IT ONE SET FUNCTION THAT RETURNS BOTH
    fun setNewListOfFiles(newDirectory: String, newListFileNames: MutableList<String>, newListFileDates: MutableList<String>) {
    imageDirectory = newDirectory
    listOfFiles = newListFileNames
    imageDates = newListFileDates
    notifyDataSetChanged()
}

    override fun onBindViewHolder(holder: PictureViewHolder, position: Int) {

        // Retrieve the data for that position (wrong thing to use maybe here? maybe make class of all 3 things??)
        // Add the data to the view holder
        val imgFileString = "$imageDirectory"+ "/" + "${listOfFiles[position]}"

        val imgFile = File(imgFileString)

        Log.v("ArrayCheck", "file added at path DIRECTORY: " + imageDirectory)
        Log.v("ArrayCheck", "file added at path LISTOFFILES[POSITION]: " + "${listOfFiles[position]}")

        Picasso.get().load(imgFile).fit().into(holder.imageView)

        Log.v("ArrayCheck", "file added at path AFTER HOLDER: " + imgFile.toString())

        holder.imageName.text = listOfFiles[position]
        holder.imageDate.text = imageDates[position]

        holder.imageView.setOnClickListener{
            //intent
            val intent = Intent(holder.imageView.context as Context, PictureDisplay::class.java)
            intent.putExtra(Intent.EXTRA_TEXT, imgFileString)
            //start new activity with intent
            startActivity(holder.imageView.context, intent, null)

        }

    }

}