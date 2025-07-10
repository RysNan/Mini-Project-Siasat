package com.rys.siasat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AbsensiAdapter(private val mahasiswaList: List<User>) : RecyclerView.Adapter<AbsensiAdapter.AbsensiViewHolder>() {

    // Peta untuk menyimpan status absensi yang dipilih untuk setiap mahasiswa
    val absensiStatusMap = mutableMapOf<String, String>()

    init {
        // Set status default "Hadir" untuk semua mahasiswa saat adapter dibuat
        mahasiswaList.forEach { mahasiswa ->
            mahasiswa.id?.let {
                absensiStatusMap[it] = "Hadir"
            }
        }
    }

    class AbsensiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaMahasiswa: TextView = itemView.findViewById(R.id.textViewAbsensiNamaMhs)
        val nimMahasiswa: TextView = itemView.findViewById(R.id.textViewAbsensiNim)
        val radioGroup: RadioGroup = itemView.findViewById(R.id.radioGroupStatus)
        val radioHadir: RadioButton = itemView.findViewById(R.id.radioHadir)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbsensiViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_absensi, parent, false)
        return AbsensiViewHolder(itemView)
    }

    override fun getItemCount(): Int = mahasiswaList.size

    override fun onBindViewHolder(holder: AbsensiViewHolder, position: Int) {
        val mahasiswa = mahasiswaList[position]
        holder.namaMahasiswa.text = mahasiswa.nama
        holder.nimMahasiswa.text = mahasiswa.nim

        // Set default radio button yang terpilih adalah "Hadir"
        holder.radioHadir.isChecked = true

        // Listener untuk RadioGroup
        holder.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val status = when (checkedId) {
                R.id.radioSakit -> "Sakit"
                R.id.radioIzin -> "Izin"
                R.id.radioAlpha -> "Alpha"
                else -> "Hadir"
            }
            // Simpan status yang dipilih ke dalam map
            mahasiswa.id?.let {
                absensiStatusMap[it] = status
            }
        }
    }
}