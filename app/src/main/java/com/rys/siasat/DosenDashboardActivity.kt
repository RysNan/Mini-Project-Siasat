package com.rys.siasat

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DosenDashboardActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var kelasAdapter: KelasDosenAdapter
    private val kelasDosenList = ArrayList<KelasDosen>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dosen_dashboard)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://siasatapp-53d00-default-rtdb.asia-southeast1.firebasedatabase.app")

        val textViewNamaDosen = findViewById<TextView>(R.id.textViewNamaDosen)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewKelasDosen)

        recyclerView.layoutManager = LinearLayoutManager(this)
        kelasAdapter = KelasDosenAdapter(kelasDosenList) { kelas ->

            val intent = Intent(this, DetailKelasActivity::class.java)
            intent.putExtra("ID_MK", kelas.mataKuliah.id_mk)
            intent.putExtra("PERIODE_ID", kelas.mataKuliah.periode_id) // <-- INI YANG PENTING
            startActivity(intent)

            Toast.makeText(this, "Membuka kelas: ${kelas.mataKuliah.nama_mk}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = kelasAdapter

        loadDosenInfo(textViewNamaDosen)
        loadJadwalDosen()
    }

    private fun loadDosenInfo(textView: TextView) {
        val dosenUid = auth.currentUser?.uid ?: return
        database.getReference("users").child(dosenUid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                textView.text = "Selamat Datang, ${user?.nama ?: "Dosen"}"
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadJadwalDosen() {
        val dosenUid = auth.currentUser?.uid ?: return

        // 1. Ambil ID periode yang sedang aktif
        database.getReference("status_sistem/periode_aktif_id").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshotPeriode: DataSnapshot) {
                val periodeAktifId = snapshotPeriode.getValue(String::class.java) ?: return

                // 2. Ambil semua mata kuliah
                database.getReference("mata_kuliah").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshotMatkul: DataSnapshot) {
                        kelasDosenList.clear()

                        // Filter mata kuliah yang diajar oleh dosen ini & pada periode aktif
                        val filteredMatkul = snapshotMatkul.children.mapNotNull {
                            it.getValue(MataKuliah::class.java)
                        }.filter {
                            it.dosen_pengampu_id == dosenUid && it.periode_id == periodeAktifId
                        }

                        if (filteredMatkul.isEmpty()) {
                            kelasAdapter.notifyDataSetChanged()
                            return
                        }

                        // 3. Untuk setiap mata kuliah, hitung jumlah mahasiswanya
                        val krsRef = database.getReference("krs").child(periodeAktifId)
                        krsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshotKrs: DataSnapshot) {
                                for (matkul in filteredMatkul) {
                                    var count = 0
                                    // Iterasi semua mahasiswa di KRS periode aktif
                                    for (mahasiswaSnap in snapshotKrs.children) {
                                        // Cek apakah mahasiswa ini mengambil mata kuliah `matkul`
                                        if (mahasiswaSnap.hasChild(matkul.id_mk!!)) {
                                            count++
                                        }
                                    }
                                    kelasDosenList.add(KelasDosen(matkul, count))
                                }
                                kelasAdapter.notifyDataSetChanged()
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}