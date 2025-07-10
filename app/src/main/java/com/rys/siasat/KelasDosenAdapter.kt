package com.rys.siasat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class KelasDosenAdapter(
    private val kelasList: List<KelasDosen>,
    private val onItemClick: (KelasDosen) -> Unit
) : RecyclerView.Adapter<KelasDosenAdapter.KelasViewHolder>() {

    class KelasViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaMk: TextView = itemView.findViewById(R.id.textViewNamaMkDosen)
        val jumlahMahasiswa: TextView = itemView.findViewById(R.id.textViewJumlahMahasiswa)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KelasViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_kelas_dosen, parent, false)
        return KelasViewHolder(itemView)
    }

    override fun getItemCount(): Int = kelasList.size

    override fun onBindViewHolder(holder: KelasViewHolder, position: Int) {
        val kelas = kelasList[position]
        holder.namaMk.text = "${kelas.mataKuliah.nama_mk} (${kelas.mataKuliah.id_mk})"
        holder.jumlahMahasiswa.text = "Jumlah Mahasiswa: ${kelas.jumlahMahasiswa}"

        // Menambahkan listener agar item bisa diklik
        holder.itemView.setOnClickListener {
            onItemClick(kelas)
        }
    }
}