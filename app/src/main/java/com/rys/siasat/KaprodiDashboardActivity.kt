package com.rys.siasat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class KaprodiDashboardActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kaprodi_dashboard)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://siasatapp-53d00-default-rtdb.asia-southeast1.firebasedatabase.app")

        val textViewNama = findViewById<TextView>(R.id.textViewNamaKaprodi)
        val buttonKelolaPeriode = findViewById<Button>(R.id.buttonKelolaPeriode)
        val buttonKelolaMatkul = findViewById<Button>(R.id.buttonKelolaMatkul)

        loadKaprodiInfo(textViewNama)

        buttonKelolaPeriode.setOnClickListener {
            startActivity(Intent(this, KelolaPeriodeActivity::class.java))
        }

        buttonKelolaMatkul.setOnClickListener {
            startActivity(Intent(this, KelolaMatkulActivity::class.java))
        }
    }

    private fun loadKaprodiInfo(textView: TextView) {
        val uid = auth.currentUser?.uid ?: return
        database.getReference("users").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                textView.text = "Selamat Datang, ${user?.nama ?: "Kaprodi"}"
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}