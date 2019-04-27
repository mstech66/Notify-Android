package com.example.manthan.notify

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_settings.*
import java.io.IOException

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        actionBar?.title = "Settings"
        actionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager.beginTransaction().replace(R.id.settingsActivity, SettingsFragment()).commit()
        val sharedPreferenceData = PreferenceManager.getDefaultSharedPreferences(this)
        val darkMode = sharedPreferenceData.getBoolean("darkMode",false)
        if(darkMode){
            setTheme(R.style.AppTheme_Dark)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                settingsActivity.setBackgroundColor(resources.getColor(R.color.darkMode, theme))
            }
            else{
                settingsActivity.setBackgroundColor(resources.getColor(R.color.darkMode))
            }
        }
        else{
            setTheme(R.style.AppTheme)
            settingsActivity.setBackgroundColor(Color.WHITE)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    class SettingsFragment : PreferenceFragmentCompat(){
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.pref_settings, rootKey)
            val switch = findPreference("darkMode")
            switch.setOnPreferenceClickListener {
                activity?.recreate()
                true
            }
            val logout = findPreference("logout")
            logout.setOnPreferenceClickListener {
                val sharedPreferences : SharedPreferences = activity!!.getSharedPreferences("NotifyLogin", Context.MODE_PRIVATE)
                sharedPreferences.edit().remove("LOGIN_ROLL").apply()
                sharedPreferences.edit().remove("LOGIN_PASS").apply()
                val intent = Intent(activity, LoginActivity::class.java)
                Thread(Runnable {
                    try{
                        FirebaseInstanceId.getInstance().deleteInstanceId()
                        FirebaseInstanceId.getInstance().instanceId
                    } catch (e: IOException){
                        e.printStackTrace()
                    }
                })
                activity?.finish()
                startActivity(intent)
                true
            }
        }
    }
}
