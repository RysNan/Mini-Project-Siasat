package com.rys.siasat

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class KelolaPeriodeActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var adapter: PeriodeAdapter
    private val periodeList = arrayListOf<Periode>()
    private val TAG = "SIASAT_DEBUG" // Tag untuk Logcat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kelola_periode)

        Log.d(TAG, "Activity Dibuat (onCreate)")

        database = FirebaseDatabase.getInstance("https://siasatapp-53d00-default-rtdb.asia-southeast1.firebasedatabase.app")

        val editTextIdPeriode = findViewById<EditText>(R.id.editTextIdPeriode)
        val editTextNamaPeriode = findViewById<EditText>(R.id.editTextNamaPeriode)
        val buttonTambah = findViewById<Button>(R.id.buttonTambahPeriode)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewPeriode)

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PeriodeAdapter(periodeList,
            onActivateClick = { periode -> setPeriodeAktif(periode) },
            onStatusChangeClick = { periode -> showUbahStatusDialog(periode) }
        )
        recyclerView.adapter = adapter

        // Panggil fungsi untuk memuat data
        loadDataPeriode()

        // Setup Tombol Tambah
        buttonTambah.setOnClickListener {
            Log.d(TAG, "Tombol Tambah Ditekan!")
            val id = editTextIdPeriode.text.toString().trim()
            val nama = editTextNamaPeriode.text.toString().trim()

            if (id.isNotEmpty() && nama.isNotEmpty()) {
                tambahPeriodeBaru(id, nama)
            } else {
                Toast.makeText(this, "ID dan Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadDataPeriode() {
        Log.d(TAG, "Memuat data dari Firebase...")
        val ref = database.getReference("periode_akademik")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d(TAG, "onDataChange terpanggil.")
                periodeList.clear()
                if (snapshot.exists()) {
                    for (periodeSnap in snapshot.children) {
                        val periodeData = periodeSnap.getValue(Periode::class.java)
                        periodeList.add(periodeData!!)
                        Log.d(TAG, "Data dimuat: ${periodeData.id}")
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    Log.d(TAG, "Snapshot tidak ada (kosong).")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Gagal memuat data: ${error.message}")
                Toast.makeText(this@KelolaPeriodeActivity, "Gagal memuat data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun tambahPeriodeBaru(id: String, nama: String) {
        Log.d(TAG, "Fungsi tambahPeriodeBaru dipanggil dengan ID: $id")
        val ref = database.getReference("periode_akademik").child(id)

        val periodeData = mapOf(
            "id" to id, "nama" to nama, "status" to "pendaftaran"
        )

        ref.setValue(periodeData) { databaseError, databaseReference ->
            if (databaseError != null) {
                Log.e(TAG, "Firebase GAGAL: ${databaseError.message}")
                Toast.makeText(this, "Gagal: ${databaseError.message}", Toast.LENGTH_LONG).show()
            } else {
                Log.d(TAG, "Firebase BERHASIL. Data ditulis ke ${databaseReference.path}")
                Toast.makeText(this, "Periode berhasil ditambahkan!", Toast.LENGTH_SHORT).show()

                // KOSONGKAN INPUT SETELAH BERHASIL
                findViewById<EditText>(R.id.editTextIdPeriode).text.clear()
                findViewById<EditText>(R.id.editTextNamaPeriode).text.clear()
            }
        }
        Log.d(TAG, "Perintah setValue sudah dikirim. Menunggu respon dari Firebase...")
    }

    private fun setPeriodeAktif(periode: Periode) {
        periode.id?.let { id ->
            database.getReference("status_sistem").child("periode_aktif_id").setValue(id)
                .addOnSuccessListener { Toast.makeText(this, "${periode.nama} sekarang aktif", Toast.LENGTH_SHORT).show() }
        }
    }

    private fun showUbahStatusDialog(periode: Periode) {
        val statusOptions = arrayOf("pendaftaran", "berjalan", "selesai")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Ubah Status untuk ${periode.nama}")
        builder.setItems(statusOptions) { _, which ->
            updateStatusPeriode(periode, statusOptions[which])
        }
        builder.show()
    }

    private fun updateStatusPeriode(periode: Periode, newStatus: String) {
        periode.id?.let { id ->
            database.getReference("periode_akademik").child(id).child("status").setValue(newStatus)
                .addOnSuccessListener { Toast.makeText(this, "Status berhasil diubah", Toast.LENGTH_SHORT).show() }
        }
    }
}