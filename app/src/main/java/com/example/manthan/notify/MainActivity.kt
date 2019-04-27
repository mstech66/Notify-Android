package com.example.manthan.notify

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.*
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.preference.PreferenceManager
import android.telephony.TelephonyManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.paytm.pgsdk.PaytmOrder
import com.paytm.pgsdk.PaytmPGService
import com.paytm.pgsdk.PaytmPaymentTransactionCallback
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.notify_item.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IllegalArgumentException
import java.security.Provider
import java.util.*
import java.util.jar.Manifest
import kotlin.random.Random

class MainActivity : AppCompatActivity(), PaytmPaymentTransactionCallback {

    private lateinit var notifyViewModel: NotifyViewModel
    private lateinit var context: Context
    private var broadcastMain : BroadcastMain? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotifyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //stop service when in Main
        this.context = this
        val intent = Intent(this.context, BroadcastService::class.java)
        this.stopService(intent)

        Log.d("Test 101", "onCreate() called!")

        recyclerView = findViewById(R.id.recyclerView)
        adapter = NotifyAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        notifyViewModel = ViewModelProviders.of(this)[NotifyViewModel::class.java] //association
        notifyViewModel.allNotifications.observe(this, Observer { notifications ->
            notifications?.let {
                adapter.setNotifications(it)
            }
        })

        if(ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.READ_SMS, android.Manifest.permission.RECEIVE_SMS), 101)
        }


        broadcastMain = BroadcastMain()
        val intentFilter = IntentFilter()
        intentFilter.addAction("ServiceToMain")
        registerReceiver(broadcastMain,intentFilter)

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(p0: androidx.recyclerview.widget.RecyclerView, p1: androidx.recyclerview.widget.RecyclerView.ViewHolder, p2: androidx.recyclerview.widget.RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(p0: androidx.recyclerview.widget.RecyclerView.ViewHolder, p1: Int) {
                notifyViewModel.delete(adapter.getNotifyAt(p0.adapterPosition))
                Toast.makeText(this@MainActivity, "Deleted", Toast.LENGTH_SHORT).show()
            }
        }).attachToRecyclerView(recyclerView)

        adapter.setOnItemClickListener(object : NotifyAdapter.onItemClickListener {
            override fun onItemClick(notify: Notify) {
                val bottomSheet = BottomSheet()
                bottomSheet.doSomething(notify.title, notify.message, notify.priority, notify.imageBase64)
                bottomSheet.show(supportFragmentManager, "notificationBottomSheet")
            }
        })

        swipeToRefreshLayout.setOnRefreshListener {
            notifyViewModel.allNotifications.observe(this, Observer { notifications ->
                notifications?.let {
                    adapter.setNotifications(it)
                }
            })
            swipeToRefreshLayout.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()
        this.context = this
        Log.d("Test 101", "onResume() called!")
        val intent = Intent(this.context, BroadcastService::class.java)
        this.stopService(intent)
        adapter.notifyDataSetChanged()
        val sharedPreferenceData = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
        val darkMode = sharedPreferenceData.getBoolean("darkMode",false)
        if(darkMode){
            setTheme(R.style.AppTheme_Dark)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                recyclerView.setBackgroundColor(resources.getColor(R.color.darkMode, theme))
            }
            else{
                recyclerView.setBackgroundColor(resources.getColor(R.color.darkMode))
            }
        }
        else{
            setTheme(R.style.AppTheme)
            recyclerView.setBackgroundColor(Color.WHITE)
        }
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("Test 101", "onRestart() called!")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Test 101", "onPause() called!")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Test 101", "onStop() called!")
//        this.context = this
//        val intent = Intent(this.context, BroadcastService::class.java)
//        this.stopService(intent)
        this.context = this
        val intent = Intent(this.context, BroadcastService::class.java)
        val isRunning = (PendingIntent.getBroadcast(this.context, 0, intent, 0))
        if (isRunning.equals(false)) {
            Log.d("Test 101", "Running from onStop()")
            val pendingIntent = PendingIntent.getBroadcast(this.context, 0, intent, 0)
            val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 60000, pendingIntent)
        }
        this.startService(intent)
        //unregister main broadcast
        try{
            unregisterReceiver(broadcastMain)
        }
        catch(e: IllegalArgumentException){
            Log.d("Test 101", "Handled Exception!!")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Test 101", "onDestroy() called!")
    }

    fun sendToMainActivity(title: String, message: String, priority: String, base64: String) {
        val notify = Notify(title, message, priority, base64)
        Log.d("Test 101", "MainActivity: $title $message $priority")
        getPriorityResolver(priority)
        this.notifyViewModel.insert(notify)
    }

    private fun getPriorityResolver(priority: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            when (priority) {
                "Critical" -> priorityResolver?.setImageDrawable(resources.getDrawable(R.drawable.circle_red, applicationContext.theme))
                "Payment" -> priorityResolver?.setImageDrawable(resources.getDrawable(R.drawable.circle_purple, applicationContext.theme))
                "Event" -> priorityResolver?.setImageDrawable(resources.getDrawable(R.drawable.circle_yellow, applicationContext.theme))
                "Exam" -> priorityResolver?.setImageDrawable(resources.getDrawable(R.drawable.circle_green, applicationContext.theme))
            }
        }
        else{
            when (priority) {
                "Critical" -> priorityResolver?.setImageDrawable(resources.getDrawable(R.drawable.circle_red))
                "Payment" -> priorityResolver?.setImageDrawable(resources.getDrawable(R.drawable.circle_purple))
                "Event" -> priorityResolver?.setImageDrawable(resources.getDrawable(R.drawable.circle_yellow))
                "Exam" -> priorityResolver?.setImageDrawable(resources.getDrawable(R.drawable.circle_green))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.notify_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.settings -> {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class BroadcastMain : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            val intentExtras: Bundle = p1!!.extras
            val title = intentExtras.getString("TITLE")
            val message = intentExtras.getString("MESSAGE")
            val priority = intentExtras.getString("PRIORITY")
            val base64 = intentExtras.getString("BASE64")
            sendToMainActivity(title, message, priority, base64)
            Log.d("Test 101", "Received by MainActivity")
        }
    }

    fun payTM(amount: String){
        val orderID: String = (0..10000).random().toString()
        val customerID: String? = orderID+13

        if(ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(android.Manifest.permission.READ_PHONE_STATE), 101)
        }
        val callbackUrl: String? = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=$orderID"
        val phoneNumber = "7777777777"
        val paytm: PayTM = PayTM(orderID, amount, customerID, callbackUrl, "abc@gmail.com", phoneNumber)
        val notifyService : NotifyService = ServiceBuilder.buildService(NotifyService::class.java)
        val requestCall: Call<Checksum> = notifyService.generateChecksum(
                paytm.mId,
                paytm.orderId,
                paytm.custId,
                paytm.mobileNo,
                paytm.email,
                paytm.channelId,
                paytm.txnAmnt,
                paytm.website,
                paytm.callBackUrl,
                paytm.industryType
        )

        requestCall.enqueue(object : Callback<Checksum>{
            override fun onFailure(call: Call<Checksum>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Something happened!", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<Checksum>, response: Response<Checksum>) {
                Log.d("Paytm", response.body()?.checksumHash.toString())
                initializePayment(response.body()?.checksumHash.toString(), paytm)
            }
        })
    }

    fun initializePayment(checksumhash:String?, paytm: PayTM){

        val service: PaytmPGService = PaytmPGService.getStagingService()

        val map : HashMap<String, String?> = HashMap()
        map.put("MID",paytm.mId)
        map.put("ORDER_ID", paytm.orderId)
        map.put("CUST_ID", paytm.custId)
        map.put("MOBILE_NO", paytm.mobileNo)
        map.put("EMAIL", paytm.email)
        map.put("CHANNEL_ID", paytm.channelId)
        map.put("TXN_AMOUNT", paytm.txnAmnt)
        map.put("WEBSITE", paytm.website)
        map.put("INDUSTRY_TYPE_ID", paytm.industryType)
        map.put("CALLBACK_URL", paytm.callBackUrl)
        map.put("CHECKSUMHASH",checksumhash)

        Log.d("Paytm", map.toString())

        val order = PaytmOrder(map)

        service.initialize(order, null)

        service.startPaymentTransaction(this, true, true, this@MainActivity)
    }

    override fun onTransactionResponse(inResponse: Bundle?) {
        Log.d("Paytm", inResponse.toString())
        Toast.makeText(this, "Successfully Done!", Toast.LENGTH_LONG).show()
    }

    override fun clientAuthenticationFailed(inErrorMessage: String?) {
        Log.d("Paytm", inErrorMessage.toString())
    }

    override fun someUIErrorOccurred(inErrorMessage: String?) {
        Log.d("Paytm", inErrorMessage.toString())
    }

    override fun onTransactionCancel(inErrorMessage: String?, inResponse: Bundle?) {
        Log.d("Paytm", inErrorMessage.toString())
    }

    override fun networkNotAvailable() {
        Log.d("Paytm", "Network Not available")
    }

    override fun onErrorLoadingWebPage(iniErrorCode: Int, inErrorMessage: String?, inFailingUrl: String?) {
        Log.d("Paytm", inErrorMessage.toString())
    }

    override fun onBackPressedCancelTransaction() {
        Log.d("Paytm", "Back Pressed")
    }
}
