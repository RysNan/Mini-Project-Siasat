package com.rys.siasat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MahasiswaDashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mahasiswa_dashboard)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://siasatapp-53d00-default-rtdb.asia-southeast1.firebasedatabase.app")

        val textViewNama = findViewById<TextView>(R.id.textViewNamaMahasiswa)
        val buttonKrs = findViewById<Button>(R.id.buttonKrs)
        val buttonLihatKhs = findViewById<Button>(R.id.buttonLihatKhs)
        val buttonLihatAbsensi = findViewById<Button>(R.id.buttonLihatAbsensi)

        loadMahasiswaInfo(textViewNama)

        buttonKrs.setOnClickListener {
            startActivity(Intent(this, KrsActivity::class.java))
        }

        buttonLihatKhs.setOnClickListener {
            startActivity(Intent(this, KhsActivity::class.java))
        }

        buttonLihatAbsensi.setOnClickListener {
            startActivity(Intent(this, RiwayatAbsensiActivity::class.java))
        }
    }

    private fun loadMahasiswaInfo(textView: TextView) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            val userRef = database.getReference("users").child(uid)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    textView.text = "Selamat Datang, ${user?.nama ?: "Mahasiswa"}"
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }
}