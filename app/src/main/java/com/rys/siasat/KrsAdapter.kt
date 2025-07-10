package com.rys.siasat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class KrsAdapter(
    private val matkulList: List<MataKuliah>,
    private val krsMahasiswa: Set<String>, // Set berisi ID mata kuliah yang sudah diambil
    private val dosenMap: Map<String, String>, // Peta dari ID dosen ke nama dosen
    private val onAmbilClick: (MataKuliah) -> Unit
) : RecyclerView.Adapter<KrsAdapter.KrsViewHolder>() {

    class KrsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaMk: TextView = itemView.findViewById(R.id.textViewNamaMkKrs)
        val sks: TextView = itemView.findViewById(R.id.textViewSksKrs)
        val dosen: TextView = itemView.findViewById(R.id.textViewDosenKrs)
        val btnAmbil: Button = itemView.findViewById(R.id.buttonAmbilKrs)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KrsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_krs, parent, false)
        return KrsViewHolder(itemView)
    }

    override fun getItemCount(): Int = matkulList.size

    override fun onBindViewHolder(holder: KrsViewHolder, position: Int) {
        val matkul = matkulList[position]
        holder.namaMk.text = "${matkul.nama_mk} (${matkul.id_mk})"
        holder.sks.text = "SKS: ${matkul.sks}"

        // Ambil nama dosen dari peta, jika tidak ada, tampilkan ID
        val namaDosen = dosenMap[matkul.dosen_pengampu_id] ?: matkul.dosen_pengampu_id
        holder.dosen.text = "Dosen: $namaDosen"

        // Cek apakah mata kuliah ini sudah ada di KRS mahasiswa
        if (krsMahasiswa.contains(matkul.id_mk)) {
            holder.btnAmbil.text = "Diambil"
            holder.btnAmbil.isEnabled = false
        } else {
            holder.btnAmbil.text = "Ambil"
            holder.btnAmbil.isEnabled = true
            holder.btnAmbil.setOnClickListener {
                onAmbilClick(matkul)
            }
        }
    }
}