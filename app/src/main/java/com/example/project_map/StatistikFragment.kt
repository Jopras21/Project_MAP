package com.example.project_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class StatistikFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_statistik, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateStatistik(view)
    }

    override fun onResume() {
        super.onResume()
        view?.let { updateStatistik(it) }
    }

    private fun updateStatistik(view: View) {

        val tvTotalProduk = view.findViewById<TextView>(R.id.tvTotalProduk)
        val tvRataHarga = view.findViewById<TextView>(R.id.tvRataHarga)
        val tvTotalStok = view.findViewById<TextView>(R.id.tvTotalStok)
        val tvPromoAktif = view.findViewById<TextView>(R.id.tvPromoAktif)

        val tvBarangMasuk = view.findViewById<TextView>(R.id.tvBarangMasuk)
        val tvBarangKeluar = view.findViewById<TextView>(R.id.tvBarangKeluar)
        val tvProdukTerlaris = view.findViewById<TextView>(R.id.tvProdukTerlaris)
        val tvStokTerendah = view.findViewById<TextView>(R.id.tvStokTerendah)

        val productList = HomeFragment.productList
        val historyList = HomeFragment.stockHistoryList

        tvTotalProduk.text = productList.size.toString()

        val rataHarga =
            if (productList.isNotEmpty())
                productList.sumOf { it.listedPrice } / productList.size
            else 0.0

        tvRataHarga.text = "Rp${rataHarga.toInt()}"

        tvTotalStok.text = productList.sumOf { it.stok }.toString()
        tvPromoAktif.text = productList.count { it.promoAktif }.toString()

        val totalMasuk = historyList
            .filter { it.jenis == "MASUK" }
            .sumOf { it.jumlah }

        val totalKeluar = historyList
            .filter { it.jenis == "KELUAR" }
            .sumOf { it.jumlah }

        tvBarangMasuk.text = totalMasuk.toString()
        tvBarangKeluar.text = totalKeluar.toString()

        val produkTerlaris = historyList
            .filter { it.jenis == "KELUAR" }
            .groupBy { it.namaProduk }
            .mapValues { it.value.sumOf { h -> h.jumlah } }
            .maxByOrNull { it.value }

        tvProdukTerlaris.text = produkTerlaris?.key ?: "-"

        val stokTerendah = productList.minByOrNull { it.stok }
        tvStokTerendah.text =
            stokTerendah?.let { "${it.nama} (${it.stok})" } ?: "-"
    }
}
