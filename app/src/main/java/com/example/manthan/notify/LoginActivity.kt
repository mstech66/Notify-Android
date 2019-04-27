package com.example.manthan.notify

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorAccent)
        }
        setContentView(R.layout.activity_login)

        val sharedPreferences: SharedPreferences = getSharedPreferences("NotifyLogin", Context.MODE_PRIVATE)
        val loginRoll : String? = sharedPreferences.getString("LOGIN_ROLL", "")
        val loginPass : String? = sharedPreferences.getString("LOGIN_PASS", "")

        if(loginRoll!!.isNotEmpty() && loginPass!!.isNotEmpty()){
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish() //my job is finished here!!
        }

        button.setOnClickListener {
            val notifyUser = NotifyLogin()
            notifyUser.roll = editText.text.toString()
            notifyUser.password = editText2.text.toString()

            val notifyService : NotifyService = ServiceBuilder.buildService(NotifyService::class.java)
            val requestCall : Call<NotifyLogin> = notifyService.logIn(notifyUser)

            requestCall.enqueue(object : Callback<NotifyLogin> {
                override fun onFailure(call: Call<NotifyLogin>, t: Throwable) {
                    Toast.makeText(applicationContext, "Failed!", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<NotifyLogin>, response: Response<NotifyLogin>) {
                    if(response.isSuccessful){
                        val some = response.body()
                        val sem = some?.sem
                        val branch = some?.dept
                        val roll = some?.roll
                        val pass = some?.password
                        subscribeLogin(roll, branch, sem) //topic subscription according to branch sem and roll no

                        val sharedPreferences : SharedPreferences = getSharedPreferences("NotifyLogin", Context.MODE_PRIVATE)
                        val editor : SharedPreferences.Editor = sharedPreferences.edit()

                        editor.putString("LOGIN_ROLL", roll)
                        editor.putString("LOGIN_PASS", pass)
                        editor.apply()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish() //also here
//                        Toast.makeText(applicationContext, sem.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(applicationContext, "Failed!", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

    override fun onBackPressed() {

    }

    fun subscribeLogin(roll: String?, branch: String?, sem: String?){
        val sharedPreferences : SharedPreferences = getSharedPreferences("NotifyTopics", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = sharedPreferences.edit()
        FirebaseMessaging.getInstance().subscribeToTopic(roll)
        FirebaseMessaging.getInstance().subscribeToTopic(branch)
        FirebaseMessaging.getInstance().subscribeToTopic("$branch$sem")
        val stringBuilder = StringBuilder()
        stringBuilder.append("$roll,$branch,$branch$sem") //create a list of subscribed topics in string builder
        editor.putString("TOPICS",stringBuilder.toString())
        editor.apply()
    }
}
