package com.rys.siasat

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class InputNilaiActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var nilaiAdapter: NilaiAdapter
    private val mahasiswaList = ArrayList<User>()
    private val nilaiMap = mutableMapOf<String, Nilai>()

    private var idMk: String? = null
    private var periodeId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_nilai)

        database = FirebaseDatabase.getInstance("https://siasatapp-53d00-default-rtdb.asia-southeast1.firebasedatabase.app")

        idMk = intent.getStringExtra("ID_MK")
        periodeId = intent.getStringExtra("PERIODE_ID")

        if (idMk == null || periodeId == null) {
            Toast.makeText(this, "Data kelas tidak valid.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val textViewNamaMk = findViewById<TextView>(R.id.textViewNilaiNamaMk)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewNilai)
        val buttonSimpan = findViewById<Button>(R.id.buttonSimpanNilai)

        textViewNamaMk.text = "Input Nilai: ${intent.getStringExtra("NAMA_MK")}"

        recyclerView.layoutManager = LinearLayoutManager(this)
        nilaiAdapter = NilaiAdapter(mahasiswaList, nilaiMap)
        recyclerView.adapter = nilaiAdapter

        loadInitialData()

        buttonSimpan.setOnClickListener {
            simpanSemuaNilai()
        }
    }

    private fun loadInitialData() {
        // 1. Ambil data nilai yang sudah ada
        val nilaiRef = database.getReference("nilai").child(periodeId!!).child(idMk!!)
        nilaiRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshotNilai: DataSnapshot) {
                nilaiMap.clear()
                if (snapshotNilai.exists()) {
                    for (nilaiSnap in snapshotNilai.children) {
                        val nilai = nilaiSnap.getValue(Nilai::class.java)
                        if (nilai?.mahasiswaId != null) {
                            nilaiMap[nilai.mahasiswaId] = nilai
                        }
                    }
                }
                // 2. Setelah data nilai dimuat, baru muat daftar peserta kelas
                loadPesertaKelas()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadPesertaKelas() {
        val krsRef = database.getReference("krs").child(periodeId!!)
        krsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshotKrs: DataSnapshot) {
                mahasiswaList.clear()
                val userRef = database.getReference("users")

                for (mahasiswaSnap in snapshotKrs.children) {
                    if (mahasiswaSnap.hasChild(idMk!!)) {
                        val mahasiswaUid = mahasiswaSnap.key!!
                        userRef.child(mahasiswaUid).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshotUser: DataSnapshot) {
                                val user = snapshotUser.getValue(User::class.java)
                                if (user != null) {
                                    mahasiswaList.add(user)
                                }
                                if (mahasiswaList.size == snapshotKrs.children.count { it.hasChild(idMk!!) }) {
                                    nilaiAdapter.notifyDataSetChanged()
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun simpanSemuaNilai() {
        val nilaiRef = database.getReference("nilai").child(periodeId!!).child(idMk!!)

        // Buat map baru untuk menyimpan semua data nilai
        val dataToSave = HashMap<String, Any>()
        for ((mahasiswaId, nilai) in nilaiMap) {
            dataToSave[mahasiswaId] = nilai
        }

        nilaiRef.setValue(dataToSave).addOnSuccessListener {
            Toast.makeText(this, "Semua nilai berhasil disimpan", Toast.LENGTH_SHORT).show()
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Gagal menyimpan: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}