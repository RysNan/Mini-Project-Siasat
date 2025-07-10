package com.rys.siasat

// Gunakan "data class" dan letakkan properti di dalam kurung ()
data class MataKuliah(
    val id_mk: String? = null,
    val nama_mk: String? = null,
    val sks: Int? = null,
    val dosen_pengampu_id: String? = null,
    val periode_id: String? = null
)