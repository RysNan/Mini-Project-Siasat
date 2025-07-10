package com.rys.siasat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RiwayatAbsensiAdapter(private val summaryList: List<AbsensiSummary>) : RecyclerView.Adapter<RiwayatAbsensiAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaMk: TextView = itemView.findViewById(R.id.textViewRiwayatNamaMk)
        val hadir: TextView = itemView.findViewById(R.id.textViewHadir)
        val sakit: TextView = itemView.findViewById(R.id.textViewSakit)
        val izin: TextView = itemView.findViewById(R.id.textViewIzin)
        val alpha: TextView = itemView.findViewById(R.id.textViewAlpha)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_riwayat_absensi, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = summaryList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val summary = summaryList[position]
        holder.namaMk.text = "${summary.mataKuliah?.nama_mk} (${summary.mataKuliah?.id_mk})"
        holder.hadir.text = "H: ${summary.hadir}"
        holder.sakit.text = "S: ${summary.sakit}"
        holder.izin.text = "I: ${summary.izin}"
        holder.alpha.text = "A: ${summary.alpha}"
    }
}