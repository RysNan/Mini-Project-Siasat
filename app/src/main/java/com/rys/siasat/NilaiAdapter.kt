package com.rys.siasat

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NilaiAdapter(
    private val mahasiswaList: List<User>,
    private val nilaiMap: MutableMap<String, Nilai>
) : RecyclerView.Adapter<NilaiAdapter.NilaiViewHolder>() {

    class NilaiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaMahasiswa: TextView = itemView.findViewById(R.id.textViewNilaiNamaMhs)
        val nimMahasiswa: TextView = itemView.findViewById(R.id.textViewNilaiNim)
        val etTugas: EditText = itemView.findViewById(R.id.editTextTugas)
        val etUts: EditText = itemView.findViewById(R.id.editTextUts)
        val etUas: EditText = itemView.findViewById(R.id.editTextUas)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NilaiViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_nilai, parent, false)
        return NilaiViewHolder(itemView)
    }

    override fun getItemCount(): Int = mahasiswaList.size

    override fun onBindViewHolder(holder: NilaiViewHolder, position: Int) {
        val mahasiswa = mahasiswaList[position]
        holder.namaMahasiswa.text = mahasiswa.nama
        holder.nimMahasiswa.text = mahasiswa.nim

        // Ambil data nilai yang sudah ada dari map, atau buat baru jika belum ada
        val nilai = nilaiMap.getOrPut(mahasiswa.id!!) {
            Nilai(mahasiswa.id, mahasiswa.nama)
        }

        // Tampilkan nilai yang sudah ada
        holder.etTugas.setText(nilai.tugas?.toString() ?: "")
        holder.etUts.setText(nilai.uts?.toString() ?: "")
        holder.etUas.setText(nilai.uas?.toString() ?: "")

        // Tambahkan TextWatcher untuk menyimpan perubahan input secara real-time
        holder.etTugas.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                nilai.tugas = s.toString().toIntOrNull()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        holder.etUts.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                nilai.uts = s.toString().toIntOrNull()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        holder.etUas.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                nilai.uas = s.toString().toIntOrNull()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}