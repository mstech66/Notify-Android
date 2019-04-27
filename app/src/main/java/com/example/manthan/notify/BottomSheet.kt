package com.example.manthan.notify

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Base64
import android.util.Log
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.bottom_sheet_layout.view.*
import java.lang.Exception
import java.net.URL

class BottomSheet : BottomSheetDialogFragment() {

    private var title : String? = null
    private var message : String? = null
    private var priority: String? = null
    private var base64: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.bottom_sheet_layout, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.bottom_title.text = title
        view.bottom_message.text = message
        view.priority.text = priority
        view.bottom_title.isSelected = true
        if(base64 != "Normal"){
            Toast.makeText(view.context, base64, Toast.LENGTH_LONG).show()
            DownloadImage(view.bottomImage).execute(base64)
        }
        when(view.priority.text){
            "Critical" -> view.dot.setImageResource(R.drawable.circle_red)
            "Payment" -> view.dot.setImageResource(R.drawable.circle_purple)
            "Event" -> view.dot.setImageResource(R.drawable.circle_yellow)
            "Exam" -> view.dot.setImageResource(R.drawable.circle_green)
        }
    }

    fun doSomething(title:String, message:String, priority:String, base64:String){
        this.title = title
        this.message = message
        this.base64 = base64
        try{
            this.priority = priority.split(" ")[0]
        }
        catch(e: Exception){
            this.priority = priority
        }
    }

    class DownloadImage : AsyncTask<String, Void, Bitmap?>{
        var image: ImageView? = null
        constructor(image: ImageView){
            this.image = image
        }
        override fun doInBackground(vararg params: String?): Bitmap? {
            val url: String? = params[0]
            var bitmap : Bitmap? = null
            try{
                val inputStream = URL(url).openStream()
                bitmap = BitmapFactory.decodeStream(inputStream)
            }
            catch (e: Exception){
                Log.e("Stack", e.message)
                e.printStackTrace()
            }
            return bitmap
        }

        override fun onPostExecute(result: Bitmap?) {
            image?.setImageBitmap(result)
        }
    }
}