package com.example.project_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.project_map.HomeFragment.Companion.productList // import list

class StatistikFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_statistik, container, false) // layout statistik
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ambil view
        val tvTotalProduk = view.findViewById<TextView>(R.id.tvTotalProduk)
        val tvRataHarga = view.findViewById<TextView>(R.id.tvRataHarga)
        val tvTotalStok = view.findViewById<TextView>(R.id.tvTotalStok)
        val tvPromoAktif = view.findViewById<TextView>(R.id.tvPromoAktif)

        updateStatistics(
            tvTotalProduk,
            tvRataHarga,
            tvTotalStok,
            tvPromoAktif
        ) // isi data awal
    }

    override fun onResume() {
        super.onResume()
        // refresh saat kembali
        view?.let {
            val tvTotalProduk = it.findViewById<TextView>(R.id.tvTotalProduk)
            val tvRataHarga = it.findViewById<TextView>(R.id.tvRataHarga)
            val tvTotalStok = it.findViewById<TextView>(R.id.tvTotalStok)
            val tvPromoAktif = it.findViewById<TextView>(R.id.tvPromoAktif)
            updateStatistics(
                tvTotalProduk,
                tvRataHarga,
                tvTotalStok,
                tvPromoAktif
            )
        }
    }

    private fun updateStatistics(
        tvTotalProduk: TextView,
        tvRataHarga: TextView,
        tvTotalStok: TextView,
        tvPromoAktif: TextView
    ) {
        if (productList.isEmpty()) {
            // jika kosong
            tvTotalProduk.text = "0"
            tvRataHarga.text = "Rp0"
            tvTotalStok.text = "0"
            tvPromoAktif.text = "0"
        } else {
            // hitung ringkasan
            tvTotalProduk.text = productList.size.toString()

            val totalHarga = productList.sumOf { it.harga }
            val rataRataHarga = if (productList.isNotEmpty()) totalHarga / productList.size else 0.0
            tvRataHarga.text = "Rp${"%.0f".format(rataRataHarga)}" // tanpa desimal

            val totalStok = productList.sumOf { it.stok }
            tvTotalStok.text = totalStok.toString()

            val promoAktifCount = productList.count { it.promoAktif }
            tvPromoAktif.text = promoAktifCount.toString()
        }
    }
}
