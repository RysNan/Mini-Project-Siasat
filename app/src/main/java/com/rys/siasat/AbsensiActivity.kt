package com.rys.siasat

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class AbsensiActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var absensiAdapter: AbsensiAdapter
    private val mahasiswaList = ArrayList<User>()

    private var idMk: String? = null
    private var periodeId: String? = null
    private val tanggalHariIni = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_absensi)

        database = FirebaseDatabase.getInstance("https://siasatapp-53d00-default-rtdb.asia-southeast1.firebasedatabase.app")

        idMk = intent.getStringExtra("ID_MK")
        periodeId = intent.getStringExtra("PERIODE_ID")

        if (idMk == null || periodeId == null) {
            Toast.makeText(this, "Data kelas tidak valid.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val textViewNamaMk = findViewById<TextView>(R.id.textViewAbsensiNamaMk)
        val textViewTanggal = findViewById<TextView>(R.id.textViewAbsensiTanggal)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewAbsensi)
        val buttonSimpan = findViewById<Button>(R.id.buttonSimpanAbsensi)

        // Tampilkan nama MK dan tanggal
        val namaMk = intent.getStringExtra("NAMA_MK")
        textViewNamaMk.text = "Absensi: $namaMk"
        textViewTanggal.text = "Tanggal: $tanggalHariIni"

        // Setup RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        absensiAdapter = AbsensiAdapter(mahasiswaList)
        recyclerView.adapter = absensiAdapter

        loadPesertaKelas()

        buttonSimpan.setOnClickListener {
            simpanAbsensi()
        }
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
                                // Refresh adapter setelah semua data mahasiswa dimuat
                                if(mahasiswaList.size == snapshotKrs.children.count { it.hasChild(idMk!!) }){
                                    absensiAdapter.notifyDataSetChanged()
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

    private fun simpanAbsensi() {
        val absensiRef = database.getReference("absensi").child(periodeId!!).child(idMk!!).child(tanggalHariIni)
        val dataToSave = HashMap<String, Any>()

        for ((mahasiswaId, status) in absensiAdapter.absensiStatusMap) {
            val mahasiswa = mahasiswaList.find { it.id == mahasiswaId }
            val absensiData = Absensi(mahasiswaId, mahasiswa?.nama, status)
            dataToSave[mahasiswaId] = absensiData
        }

        absensiRef.setValue(dataToSave).addOnSuccessListener {
            Toast.makeText(this, "Absensi berhasil disimpan", Toast.LENGTH_SHORT).show()
            finish() // Kembali ke halaman detail kelas
        }.addOnFailureListener {
            Toast.makeText(this, "Gagal menyimpan: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}