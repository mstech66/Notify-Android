package com.example.manthan.notify

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.manthan.notify.R.id.paytmbutton
import com.paytm.pgsdk.PaytmPGService
import kotlinx.android.synthetic.main.notify_item.*

class NotifyAdapter constructor(context: Context): androidx.recyclerview.widget.RecyclerView.Adapter<NotifyAdapter.NotifyHolder>() {
    private var listener: onItemClickListener? = null
    private var context: Context? = null

    init {
        this.context = context
    }

    inner class NotifyHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.card_title)
        val message: TextView = itemView.findViewById(R.id.card_description)
        val priority: TextView = itemView.findViewById(R.id.card_priority)
        val priorityResolver: ImageView = itemView.findViewById(R.id.priorityResolver)
        val paytm: Button = itemView.findViewById(R.id.paytmbutton)
        val some = itemView.setOnClickListener {
            val position = adapterPosition
            if (listener != null && position != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                listener?.onItemClick(notifications[position])
            }
        }
    }

    interface onItemClickListener {
        fun onItemClick(notify: Notify)
    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        this.listener = listener
    }

    private var notifications = emptyList<Notify>() //cached copy of notifications
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): NotifyHolder {
        val itemView: View = LayoutInflater.from(p0.context)
                .inflate(R.layout.notify_item, p0, false)
        return NotifyHolder(itemView)
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun onBindViewHolder(p0: NotifyHolder, p1: Int) {
        val currentNotification: Notify = notifications[p1]
        p0.title.text = currentNotification.title
        p0.message.text = currentNotification.message
        try{
            val priority = currentNotification.priority.split(" ")[0]
            p0.priority.text = priority
        }
        catch(e: Exception){
            p0.priority.text = currentNotification.priority
        }
        when (p0.priority.text) {
            "Critical" -> p0.priorityResolver.setImageResource(R.drawable.circle_red)
            "Payment" -> p0.priorityResolver.setImageResource(R.drawable.circle_purple)
            "Event" -> p0.priorityResolver.setImageResource(R.drawable.circle_yellow)
            "Exam" -> p0.priorityResolver.setImageResource(R.drawable.circle_green)
        }
        if(p0.priority.text == "Payment"){
            val amount: String = currentNotification.priority.split(" ")[1]
            Log.d("Paytm", "Amount is...$amount")
            p0.paytm.visibility = View.VISIBLE
            val buttonText: String = "Pay ₹ $amount" //u20B9 for rupee symbol
            p0.paytm.text = buttonText
            p0.paytm.setOnClickListener {
                Toast.makeText(it.context, "Touche", Toast.LENGTH_SHORT).show()
                (context as MainActivity).payTM(amount)
            }
        }
        else{
            p0.paytm.visibility = View.GONE
        }
    }

    internal fun setNotifications(notification: List<Notify>) {
        this.notifications = notification
        notifyDataSetChanged()
    }

    internal fun getNotifyAt(position: Int): Notify {
        return notifications[position]
    }
}