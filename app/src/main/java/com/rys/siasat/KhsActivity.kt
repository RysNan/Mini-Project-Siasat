package com.rys.siasat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class KhsActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var khsAdapter: KhsAdapter
    private val khsItemList = ArrayList<KhsItem>()
    private val allMatkulMap = HashMap<String, MataKuliah>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_khs)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://siasatapp-53d00-default-rtdb.asia-southeast1.firebasedatabase.app")

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewKhs)
        recyclerView.layoutManager = LinearLayoutManager(this)
        khsAdapter = KhsAdapter(khsItemList)
        recyclerView.adapter = khsAdapter

        loadAllMataKuliah()
    }

    // Ambil semua data mata kuliah sekali saja untuk efisiensi
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
                // Setelah semua data matkul siap, baru muat nilai mahasiswa
                loadNilaiMahasiswa()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadNilaiMahasiswa() {
        val uid = auth.currentUser?.uid ?: return

        // Asumsi kita hanya melihat nilai di semua periode,
        // bisa dikembangkan untuk memfilter per periode
        val nilaiRef = database.getReference("nilai")
        nilaiRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                khsItemList.clear()
                for (periodeSnap in snapshot.children) {
                    // Cek apakah ada data nilai untuk user ini di periode ini
                    if (periodeSnap.hasChild(uid)) {
                        val nilaiUserPerPeriode = periodeSnap.child(uid)
                        for (nilaiMkSnap in nilaiUserPerPeriode.children) {
                            val nilai = nilaiMkSnap.getValue(Nilai::class.java)
                            val matkul = allMatkulMap[nilaiMkSnap.key] // Ambil detail matkul dari peta

                            if (nilai != null && matkul != null) {
                                khsItemList.add(KhsItem(matkul, nilai))
                            }
                        }
                    }
                }
                khsAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}