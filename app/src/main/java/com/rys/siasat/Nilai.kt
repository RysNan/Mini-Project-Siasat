package com.rys.siasat

import com.google.firebase.database.Exclude

data class Nilai(
    val mahasiswaId: String? = null,
    val mahasiswaNama: String? = null,
    var tugas: Int? = 0,
    var uts: Int? = 0,
    var uas: Int? = 0
) {
    // Fungsi ini tidak akan disimpan ke Firebase, hanya untuk perhitungan di aplikasi
    @Exclude
    fun getNilaiAkhir(): Double {
        return (tugas?.toDouble() ?: 0.0) * 0.3 + (uts?.toDouble() ?: 0.0) * 0.3 + (uas?.toDouble() ?: 0.0) * 0.4
    }

    @Exclude
    fun getNilaiHuruf(): String {
        val nilaiAkhir = getNilaiAkhir()
        return when {
            nilaiAkhir >= 85 -> "A"
            nilaiAkhir >= 75 -> "B"
            nilaiAkhir >= 65 -> "C"
            nilaiAkhir >= 50 -> "D"
            else -> "E"
        }
    }
}