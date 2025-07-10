package com.rys.siasat

data class AbsensiSummary(
    val mataKuliah: MataKuliah? = null,
    var hadir: Int = 0,
    var sakit: Int = 0,
    var izin: Int = 0,
    var alpha: Int = 0
)