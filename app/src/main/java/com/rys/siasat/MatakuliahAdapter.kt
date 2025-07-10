package com.rys.siasat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MataKuliahAdapter(private val matkulList: List<MataKuliah>) : RecyclerView.Adapter<MataKuliahAdapter.MatkulViewHolder>() {

    class MatkulViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaMk: TextView = itemView.findViewById(R.id.textViewNamaMk)
        val sks: TextView = itemView.findViewById(R.id.textViewSks)
        val dosen: TextView = itemView.findViewById(R.id.textViewDosen)
        val periode: TextView = itemView.findViewById(R.id.textViewPeriode)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatkulViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_matkul, parent, false)
        return MatkulViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return matkulList.size
    }

    override fun onBindViewHolder(holder: MatkulViewHolder, position: Int) {
        val currentItem = matkulList[position]

        holder.namaMk.text = "${currentItem.nama_mk} (${currentItem.id_mk})"
        holder.sks.text = "${currentItem.sks} SKS"

        // Untuk Dosen dan Periode, kita akan tampilkan ID nya dulu
        // Nanti kita bisa kembangkan untuk menampilkan nama aslinya
        holder.dosen.text = "Dosen ID: ${currentItem.dosen_pengampu_id}"
        holder.periode.text = "Periode: ${currentItem.periode_id}"
    }
}