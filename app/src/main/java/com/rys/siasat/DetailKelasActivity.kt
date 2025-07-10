package com.rys.siasat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class DetailKelasActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var mahasiswaAdapter: MahasiswaAdapter
    private val mahasiswaList = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_kelas)

        database = FirebaseDatabase.getInstance("https://siasatapp-53d00-default-rtdb.asia-southeast1.firebasedatabase.app")

        // Ambil ID Mata Kuliah yang dikirim dari DosenDashboardActivity
        val idMk = intent.getStringExtra("ID_MK")
        if (idMk == null) {
            finish() // Jika tidak ada ID, tutup halaman
            return
        }
        val buttonAbsensi = findViewById<Button>(R.id.buttonInputAbsensi)
        buttonAbsensi.setOnClickListener {
            val intent = Intent(this, AbsensiActivity::class.java)
            // Kirim semua data yang dibutuhkan oleh AbsensiActivity
            intent.putExtra("ID_MK", idMk)
            intent.putExtra("PERIODE_ID", intent.getStringExtra("PERIODE_ID")) // Anda perlu mengirim ini dari DosenDashboard
            intent.putExtra("NAMA_MK", findViewById<TextView>(R.id.textViewDetailNamaMk).text.toString())
            startActivity(intent)
        }

        val buttonNilai = findViewById<Button>(R.id.buttonInputNilai)
        buttonNilai.setOnClickListener {
            val intent = Intent(this, InputNilaiActivity::class.java)
            intent.putExtra("ID_MK", idMk)
            intent.putExtra("PERIODE_ID", intent.getStringExtra("PERIODE_ID"))
            intent.putExtra("NAMA_MK", findViewById<TextView>(R.id.textViewDetailNamaMk).text.toString())
            startActivity(intent)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewMahasiswa)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mahasiswaAdapter = MahasiswaAdapter(mahasiswaList)
        recyclerView.adapter = mahasiswaAdapter

        loadDetailKelas(idMk)
    }

    private fun loadDetailKelas(idMk: String) {
        // 1. Ambil detail mata kuliah untuk mendapatkan nama & periode
        val matkulRef = database.getReference("mata_kuliah").child(idMk)
        matkulRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshotMatkul: DataSnapshot) {
                val matkul = snapshotMatkul.getValue(MataKuliah::class.java) ?: return

                // Tampilkan nama MK & Periode di TextView
                findViewById<TextView>(R.id.textViewDetailNamaMk).text = "${matkul.nama_mk} (${matkul.id_mk})"
                findViewById<TextView>(R.id.textViewDetailPeriode).text = "Periode: ${matkul.periode_id}"

                // 2. Setelah tahu periode, cari mahasiswa di node KRS
                loadPesertaKelas(matkul.periode_id!!, idMk)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadPesertaKelas(periodeId: String, idMk: String) {
        val krsRef = database.getReference("krs").child(periodeId)
        krsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshotKrs: DataSnapshot) {
                mahasiswaList.clear()

                val userRef = database.getReference("users")
                var studentCount = 0
                val totalStudents = snapshotKrs.childrenCount

                if (totalStudents == 0L) {
                    mahasiswaAdapter.notifyDataSetChanged() // Update UI jika tidak ada mahasiswa
                }

                for (mahasiswaSnap in snapshotKrs.children) {
                    // Cek apakah mahasiswa ini mengambil mata kuliah kita
                    if (mahasiswaSnap.hasChild(idMk)) {
                        val mahasiswaUid = mahasiswaSnap.key!!

                        // 3. Ambil detail user mahasiswa berdasarkan UID nya
                        userRef.child(mahasiswaUid).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshotUser: DataSnapshot) {
                                val user = snapshotUser.getValue(User::class.java)
                                if (user != null) {
                                    mahasiswaList.add(user)
                                }
                                studentCount++
                                if (studentCount == totalStudents.toInt()){
                                    mahasiswaAdapter.notifyDataSetChanged()
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {
                                studentCount++
                                if (studentCount == totalStudents.toInt()){
                                    mahasiswaAdapter.notifyDataSetChanged()
                                }
                            }
                        })
                    } else {
                        studentCount++
                        if (studentCount == totalStudents.toInt()){
                            mahasiswaAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}