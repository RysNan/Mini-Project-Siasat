package com.rys.siasat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.rys.siasat.DosenDashboardActivity
import com.rys.siasat.KaprodiDashboardActivity
import com.rys.siasat.MahasiswaDashboardActivity
import com.rys.siasat.R

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inisialisasi Firebase Auth dan Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://siasatapp-53d00-default-rtdb.asia-southeast1.firebasedatabase.app")

        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)

        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan Password tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Proses Login dengan Firebase Authentication
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Jika login berhasil, ambil data user dari Realtime Database
                        checkUserRole(task.result?.user?.uid)
                    } else {
                        // Jika login gagal, tampilkan pesan error
                        Toast.makeText(baseContext, "Login Gagal: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun checkUserRole(uid: String?) {
        if (uid == null) return

        val userRef = database.getReference("users").child(uid)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val role = snapshot.child("role").getValue(String::class.java)

                // Arahkan ke dashboard yang sesuai
                when (role) {
                    "mahasiswa" -> {
                        startActivity(Intent(this@LoginActivity, MahasiswaDashboardActivity::class.java))
                    }
                    "dosen" -> {
                        startActivity(Intent(this@LoginActivity, DosenDashboardActivity::class.java))
                    }
                    "kaprodi" -> {
                        startActivity(Intent(this@LoginActivity, KaprodiDashboardActivity::class.java))
                    }
                    else -> {
                        Toast.makeText(baseContext, "Role tidak dikenali!", Toast.LENGTH_SHORT).show()
                    }
                }
                finish() // Tutup activity login setelah berhasil
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}