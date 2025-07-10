package com.rys.siasat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class KhsAdapter(private val khsList: List<KhsItem>) : RecyclerView.Adapter<KhsAdapter.KhsViewHolder>() {

    class KhsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaMk: TextView = itemView.findViewById(R.id.textViewKhsNamaMk)
        val sks: TextView = itemView.findViewById(R.id.textViewKhsSks)
        val nilaiHuruf: TextView = itemView.findViewById(R.id.textViewKhsNilaiHuruf)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KhsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_khs, parent, false)
        return KhsViewHolder(itemView)
    }

    override fun getItemCount(): Int = khsList.size

    override fun onBindViewHolder(holder: KhsViewHolder, position: Int) {
        val item = khsList[position]
        val matkul = item.mataKuliah
        val nilai = item.nilai

        holder.namaMk.text = "${matkul?.nama_mk} (${matkul?.id_mk})"
        holder.sks.text = "${matkul?.sks} SKS"
        holder.nilaiHuruf.text = nilai?.getNilaiHuruf() ?: "E"
    }
}