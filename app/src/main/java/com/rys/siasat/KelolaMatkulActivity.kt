package com.rys.siasat

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class KelolaMatkulActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var matkulAdapter: MataKuliahAdapter
    private val matkulList = ArrayList<MataKuliah>()

    // Variabel untuk menampung data dari spinner
    private val dosenList = ArrayList<User>() // User adalah data class dari user
    private val periodeList = ArrayList<Periode>()
    private var selectedDosenId: String? = null
    private var selectedPeriodeId: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kelola_matkul)

        database = FirebaseDatabase.getInstance("https://siasatapp-53d00-default-rtdb.asia-southeast1.firebasedatabase.app")

        // Inisialisasi Views
        val editTextKodeMk = findViewById<EditText>(R.id.editTextKodeMk)
        val editTextNamaMk = findViewById<EditText>(R.id.editTextNamaMk)
        val editTextSks = findViewById<EditText>(R.id.editTextSks)
        val spinnerDosen = findViewById<Spinner>(R.id.spinnerDosen)
        val spinnerPeriode = findViewById<Spinner>(R.id.spinnerPeriode)
        val buttonTambahMatkul = findViewById<Button>(R.id.buttonTambahMatkul)
        val recyclerViewMatkul = findViewById<RecyclerView>(R.id.recyclerViewMatkul)

        // Setup RecyclerView
        recyclerViewMatkul.layoutManager = LinearLayoutManager(this)
        matkulAdapter = MataKuliahAdapter(matkulList)
        recyclerViewMatkul.adapter = matkulAdapter

        // Muat data untuk Spinner dan RecyclerView
        setupDosenSpinner(spinnerDosen)
        setupPeriodeSpinner(spinnerPeriode)
        loadMataKuliahData()

        // Fungsi Tombol Tambah
        buttonTambahMatkul.setOnClickListener {
            val kodeMk = editTextKodeMk.text.toString().trim()
            val namaMk = editTextNamaMk.text.toString().trim()
            val sksText = editTextSks.text.toString().trim()

            if (kodeMk.isEmpty() || namaMk.isEmpty() || sksText.isEmpty() || selectedDosenId == null || selectedPeriodeId == null) {
                Toast.makeText(this, "Harap isi semua kolom dan pilihan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sks = sksText.toInt()
            val matkulBaru = MataKuliah(kodeMk, namaMk, sks, selectedDosenId, selectedPeriodeId)
            tambahMataKuliah(matkulBaru)
        }
    }

    private fun setupDosenSpinner(spinner: Spinner) {
        val dosenRef = database.getReference("users").orderByChild("role").equalTo("dosen")
        dosenRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dosenList.clear()
                val namaDosenList = ArrayList<String>()
                namaDosenList.add("Pilih Dosen Pengampu") // Hint pertama
                dosenList.add(User()) // Placeholder untuk hint

                if (snapshot.exists()) {
                    for (dosenSnap in snapshot.children) {
                        val dosen = dosenSnap.getValue(User::class.java)
                        if (dosen != null) {
                            dosenList.add(dosen)
                            namaDosenList.add(dosen.nama ?: "Tanpa Nama")
                        }
                    }
                }
                val adapter = ArrayAdapter(this@KelolaMatkulActivity, android.R.layout.simple_spinner_item, namaDosenList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) { /* Handle error */ }
        })

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedDosenId = if (position > 0) dosenList[position].id else null
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupPeriodeSpinner(spinner: Spinner) {
        val periodeRef = database.getReference("periode_akademik")
        periodeRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                periodeList.clear()
                val namaPeriodeList = ArrayList<String>()
                namaPeriodeList.add("Pilih Periode")
                periodeList.add(Periode()) // Placeholder

                if (snapshot.exists()){
                    for(snap in snapshot.children){
                        val periode = snap.getValue(Periode::class.java)
                        if (periode != null){
                            periodeList.add(periode)
                            namaPeriodeList.add(periode.nama ?: "Tanpa Nama")
                        }
                    }
                }
                val adapter = ArrayAdapter(this@KelolaMatkulActivity, android.R.layout.simple_spinner_item, namaPeriodeList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
            override fun onCancelled(error: DatabaseError) { /* Handle error */ }
        })

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedPeriodeId = if (position > 0) periodeList[position].id else null
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadMataKuliahData() {
        val matkulRef = database.getReference("mata_kuliah")
        matkulRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                matkulList.clear()
                if (snapshot.exists()){
                    for(snap in snapshot.children){
                        val matkul = snap.getValue(MataKuliah::class.java)
                        if (matkul != null){
                            matkulList.add(matkul)
                        }
                    }
                    matkulAdapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(error: DatabaseError) { /* Handle error */ }
        })
    }

    private fun tambahMataKuliah(matkul: MataKuliah) {
        val ref = database.getReference("mata_kuliah").child(matkul.id_mk!!)
        ref.setValue(matkul) { error, _ ->
            if (error == null) {
                Toast.makeText(this, "Mata Kuliah berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                // Kosongkan form
                findViewById<EditText>(R.id.editTextKodeMk).text.clear()
                findViewById<EditText>(R.id.editTextNamaMk).text.clear()
                findViewById<EditText>(R.id.editTextSks).text.clear()
                findViewById<Spinner>(R.id.spinnerDosen).setSelection(0)
                findViewById<Spinner>(R.id.spinnerPeriode).setSelection(0)
            } else {
                Toast.makeText(this, "Gagal menambahkan: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

// Pastikan Anda sudah memiliki data class User.kt
// data class User(val id: String? = null, val nama: String? = null, val role: String? = null)