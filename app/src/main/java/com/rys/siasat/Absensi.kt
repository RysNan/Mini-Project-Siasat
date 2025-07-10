package com.rys.siasat

// Menyimpan status absensi untuk satu mahasiswa
data class Absensi(
    val mahasiswaId: String? = null,
    val mahasiswaNama: String? = null, // Untuk kemudahan saat menampilkan riwayat
    val status: String? = null // "Hadir", "Sakit", "Izin", "Alpha"
)