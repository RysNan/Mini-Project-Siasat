package com.rys.siasat

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class KrsActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var krsAdapter: KrsAdapter
    private val availableMatkulList = ArrayList<MataKuliah>()
    private var activePeriodeId: String? = null
    private var activePeriodeNama: String? = null
    private val krsMahasiswa = HashSet<String>() // Gunakan Set untuk efisiensi
    private val dosenMap = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_krs)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://siasatapp-53d00-default-rtdb.asia-southeast1.firebasedatabase.app")

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewKrs)
        recyclerView.layoutManager = LinearLayoutManager(this)
        krsAdapter = KrsAdapter(availableMatkulList, krsMahasiswa, dosenMap) { matkul ->
            ambilMataKuliah(matkul)
        }
        recyclerView.adapter = krsAdapter

        loadInitialData()
    }

    private fun loadInitialData() {
        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid == null) {
            Toast.makeText(this, "Sesi tidak valid, silakan login ulang.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 1. Ambil ID Periode Aktif
        database.getReference("status_sistem/periode_aktif_id").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                activePeriodeId = snapshot.getValue(String::class.java)
                if (activePeriodeId == null) {
                    Toast.makeText(this@KrsActivity, "Tidak ada periode aktif.", Toast.LENGTH_SHORT).show()
                    return
                }

                // 2. Ambil Nama Periode Aktif
                database.getReference("periode_akademik/$activePeriodeId/nama").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(namaSnapshot: DataSnapshot) {
                        activePeriodeNama = namaSnapshot.getValue(String::class.java)
                        findViewById<TextView>(R.id.textViewPeriodeAktif).text = "Periode Aktif: $activePeriodeNama"
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })

                // 3. Ambil data Dosen untuk mapping ID ke Nama
                loadDosenData()

                // 4. Ambil data KRS mahasiswa yang sudah ada
                loadKrsMahasiswa(currentUserUid)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadDosenData() {
        database.getReference("users").orderByChild("role").equalTo("dosen").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dosenMap.clear()
                if(snapshot.exists()){
                    for(snap in snapshot.children){
                        val dosen = snap.getValue(User::class.java)
                        if(dosen?.id != null && dosen.nama != null){
                            dosenMap[dosen.id] = dosen.nama
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadKrsMahasiswa(uid: String) {
        val krsRef = database.getReference("krs/$activePeriodeId/$uid")
        krsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                krsMahasiswa.clear()
                if (snapshot.exists()) {
                    for (krsSnap in snapshot.children) {
                        krsSnap.key?.let { krsMahasiswa.add(it) }
                    }
                }
                // 5. Setelah tahu KRS mahasiswa, baru muat semua mata kuliah
                loadAvailableMataKuliah()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadAvailableMataKuliah() {
        val matkulRef = database.getReference("mata_kuliah").orderByChild("periode_id").equalTo(activePeriodeId)
        matkulRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                availableMatkulList.clear()
                if (snapshot.exists()) {
                    for (matkulSnap in snapshot.children) {
                        val matkul = matkulSnap.getValue(MataKuliah::class.java)
                        if (matkul != null) {
                            availableMatkulList.add(matkul)
                        }
                    }
                }
                krsAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun ambilMataKuliah(matkul: MataKuliah) {
        val currentUserUid = auth.currentUser?.uid ?: return
        val matkulId = matkul.id_mk ?: return

        val krsRef = database.getReference("krs/$activePeriodeId/$currentUserUid").child(matkulId)
        krsRef.setValue(true).addOnSuccessListener {
            Toast.makeText(this, "${matkul.nama_mk} berhasil diambil", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Gagal mengambil: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}