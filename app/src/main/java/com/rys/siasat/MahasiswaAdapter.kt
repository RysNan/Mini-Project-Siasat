package com.rys.siasat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MahasiswaAdapter(private val mahasiswaList: List<User>) : RecyclerView.Adapter<MahasiswaAdapter.MahasiswaViewHolder>() {

    class MahasiswaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaMahasiswa: TextView = itemView.findViewById(R.id.textViewNamaMahasiswa)
        val nimMahasiswa: TextView = itemView.findViewById(R.id.textViewNimMahasiswa)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MahasiswaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_mahasiswa_dosen, parent, false)
        return MahasiswaViewHolder(itemView)
    }

    override fun getItemCount(): Int = mahasiswaList.size

    override fun onBindViewHolder(holder: MahasiswaViewHolder, position: Int) {
        val mahasiswa = mahasiswaList[position]
        holder.namaMahasiswa.text = mahasiswa.nama
        holder.nimMahasiswa.text = mahasiswa.nim
    }
}