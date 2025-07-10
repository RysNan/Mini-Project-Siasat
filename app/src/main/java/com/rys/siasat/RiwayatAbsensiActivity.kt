package com.rys.siasat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RiwayatAbsensiActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var absensiAdapter: RiwayatAbsensiAdapter
    private val summaryList = ArrayList<AbsensiSummary>()
    private val allMatkulMap = HashMap<String, MataKuliah>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayat_absensi)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://siasatapp-53d00-default-rtdb.asia-southeast1.firebasedatabase.app")

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewRiwayatAbsensi)
        recyclerView.layoutManager = LinearLayoutManager(this)
        absensiAdapter = RiwayatAbsensiAdapter(summaryList)
        recyclerView.adapter = absensiAdapter

        loadAllMataKuliah()
    }

    private fun loadAllMataKuliah() {
        database.getReference("mata_kuliah").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (snap in snapshot.children) {
                        val matkul = snap.getValue(MataKuliah::class.java)
                        if (matkul?.id_mk != null) {
                            allMatkulMap[matkul.id_mk] = matkul
                        }
                    }
                }
                loadAbsensiMahasiswa()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadAbsensiMahasiswa() {
        val uid = auth.currentUser?.uid ?: return

        database.getReference("krs").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(krsSnapshot: DataSnapshot) {
                val matkulDiambil = ArrayList<String>()
                // Cari semua matkul yang diambil mahasiswa ini di semua periode
                for (periodeSnap in krsSnapshot.children) {
                    if (periodeSnap.hasChild(uid)) {
                        for (mkSnap in periodeSnap.child(uid).children) {
                            mkSnap.key?.let { matkulDiambil.add(it) }
                        }
                    }
                }

                // Setelah tahu semua matkul yang diambil, hitung absensinya
                hitungAbsensi(matkulDiambil, uid)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun hitungAbsensi(matkulDiambil: List<String>, uid: String) {
        summaryList.clear()
        val absensiRef = database.getReference("absensi")
        absensiRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(absensiSnapshot: DataSnapshot) {
                for (idMk in matkulDiambil) {
                    val matkul = allMatkulMap[idMk] ?: continue
                    val summary = AbsensiSummary(matkul)

                    for (periodeSnap in absensiSnapshot.children) {
                        val absensiMkSnap = periodeSnap.child(idMk)
                        if (absensiMkSnap.exists()) {
                            for (pertemuanSnap in absensiMkSnap.children) {
                                val status = pertemuanSnap.child(uid).child("status").getValue(String::class.java)
                                when (status) {
                                    "Hadir" -> summary.hadir++
                                    "Sakit" -> summary.sakit++
                                    "Izin" -> summary.izin++
                                    "Alpha" -> summary.alpha++
                                }
                            }
                        }
                    }
                    summaryList.add(summary)
                }
                absensiAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}