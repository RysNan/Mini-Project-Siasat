package com.rys.siasat

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Tambahkan 2 parameter fungsi di constructor
class PeriodeAdapter(
    private val periodeList: ArrayList<Periode>,
    private val onActivateClick: (Periode) -> Unit,
    private val onStatusChangeClick: (Periode) -> Unit
) : RecyclerView.Adapter<PeriodeAdapter.PeriodeViewHolder>() {

    class PeriodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaPeriode: TextView = itemView.findViewById(R.id.textViewNamaPeriode)
        val statusPeriode: TextView = itemView.findViewById(R.id.textViewStatusPeriode)
        val idPeriode: TextView = itemView.findViewById(R.id.textViewIdPeriode)
        val btnJadikanAktif: Button = itemView.findViewById(R.id.buttonJadikanAktif)
        val btnUbahStatus: Button = itemView.findViewById(R.id.buttonUbahStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeriodeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_periode, parent, false)
        return PeriodeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return periodeList.size
    }

    override fun onBindViewHolder(holder: PeriodeViewHolder, position: Int) {
        val currentItem = periodeList[position]
        holder.namaPeriode.text = currentItem.nama
        holder.statusPeriode.text = "Status: ${currentItem.status}"
        holder.idPeriode.text = "ID: ${currentItem.id}"

        // Set OnClickListener untuk setiap tombol
        holder.btnJadikanAktif.setOnClickListener {
            // Panggil fungsi yang diterima dari Activity
            Log.d("PeriodeAdapter", "Tombol 'Jadikan Aktif' diklik untuk item: ${currentItem.id}")
            onActivateClick(currentItem)
        }
        holder.btnUbahStatus.setOnClickListener {
            // Panggil fungsi yang diterima dari Activity
            Log.d("PeriodeAdapter", "Tombol 'Ubah Status' diklik untuk item: ${currentItem.id}")
            onStatusChangeClick(currentItem)
        }
    }

}