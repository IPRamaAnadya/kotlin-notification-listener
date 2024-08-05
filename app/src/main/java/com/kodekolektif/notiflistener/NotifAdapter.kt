package com.kodekolektif.notiflistener

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kodekolektif.notiflistener.data.datasource.local.entities.NotifEntity

class NotifAdapter(private val notifications: List<NotifEntity>) :
    RecyclerView.Adapter<NotifAdapter.NotifViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notif_item, parent, false)
        return NotifViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotifViewHolder, position: Int) {
        val notification = notifications[position]
        holder.titleTextView.text = notification.title
        holder.bodyTextView.text = notification.body
        holder.pkgTextView.text = notification.packageName
        holder.dateTextView.text = notification.createAt.toString()
        holder.numberTextView.text = (notifications.size - position).toString()
    }

    override fun getItemCount() = notifications.size

    class NotifViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pkgTextView: TextView = itemView.findViewById(R.id.notification_pkg)
        val dateTextView: TextView = itemView.findViewById(R.id.notification_date)
        val titleTextView: TextView = itemView.findViewById(R.id.notification_title)
        val bodyTextView: TextView = itemView.findViewById(R.id.notification_body)
        val numberTextView: TextView = itemView.findViewById(R.id.number)
    }
}