package com.example.project_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.project_map.viewmodel.HomeViewModel

class StatistikFragment : Fragment() {

    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_statistik, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.start(requireContext())

        observeData(view)
    }

    private fun observeData(view: View) {

        val tvTotalProduk = view.findViewById<TextView>(R.id.tvTotalProduk)
        val tvRataHarga = view.findViewById<TextView>(R.id.tvRataHarga)
        val tvTotalStok = view.findViewById<TextView>(R.id.tvTotalStok)
        val tvPromoAktif = view.findViewById<TextView>(R.id.tvPromoAktif)

        val tvBarangMasuk = view.findViewById<TextView>(R.id.tvBarangMasuk)
        val tvBarangKeluar = view.findViewById<TextView>(R.id.tvBarangKeluar)
        val tvProdukTerlaris = view.findViewById<TextView>(R.id.tvProdukTerlaris)
        val tvStokTerendah = view.findViewById<TextView>(R.id.tvStokTerendah)

        viewModel.products.observe(viewLifecycleOwner) {
            tvTotalProduk.text = viewModel.getTotalProduk().toString()
            tvRataHarga.text = "Rp${viewModel.getRataHarga().toInt()}"
            tvTotalStok.text = viewModel.getTotalStok().toString()
            tvPromoAktif.text = viewModel.getPromoAktif().toString()
            tvStokTerendah.text = viewModel.getStokTerendah()
        }

        viewModel.history.observe(viewLifecycleOwner) {
            tvBarangMasuk.text = viewModel.getTotalMasuk().toString()
            tvBarangKeluar.text = viewModel.getTotalKeluar().toString()
            tvProdukTerlaris.text = viewModel.getProdukTerlaris()
        }
    }
}
