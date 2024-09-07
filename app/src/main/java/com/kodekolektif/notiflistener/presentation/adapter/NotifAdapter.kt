package com.kodekolektif.notiflistener.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kodekolektif.notiflistener.R
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
        holder.titleTextView.text = notification.name
        holder.bodyTextView.text = notification.nominal.toString()
        holder.pkgTextView.text = notification.packageName
        holder.dateTextView.text = notification.createAt.toString()
        holder.numberTextView.text = (notifications.size - position).toString()
        holder.syncDateTextView.text = notification.validatedAt.toString()
        when (notification.status) {
            1 -> {
                holder.statusTextView.text = "Belum terkirim"
                // set color gray from hex
                holder.statusTextView.setTextColor(0xFF9E9E9E.toInt())
            }
            2 -> {
                holder.statusTextView.text = "Tervalidasi"
                // set color green from hex
                holder.statusTextView.setTextColor(0xFF4CAF50.toInt())
            }
            3 -> {
                holder.statusTextView.text = "Gagal"
                // set color red from hex
                holder.statusTextView.setTextColor(0xFFF44336.toInt())
            }
            4 -> {
                holder.statusTextView.text = "Terkirim (Menunggu validasi"
                // set color orange from hex
                holder.statusTextView.setTextColor(0xFFFF9800.toInt())
            }
            else -> {
                holder.statusTextView.text = "Invalid"
                // set color gray from hex
                holder.statusTextView.setTextColor(0xFF9E9E9E.toInt())
            }
        }
    }

    override fun getItemCount() = notifications.size

    class NotifViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pkgTextView: TextView = itemView.findViewById(R.id.notification_pkg)
        val dateTextView: TextView = itemView.findViewById(R.id.notification_date)
        val titleTextView: TextView = itemView.findViewById(R.id.notification_title)
        val bodyTextView: TextView = itemView.findViewById(R.id.notification_body)
        val numberTextView: TextView = itemView.findViewById(R.id.number)
        val statusTextView: TextView = itemView.findViewById(R.id.notification_status)
        val syncDateTextView: TextView = itemView.findViewById(R.id.notification_sync_date)
    }
}