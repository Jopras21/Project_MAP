package com.example.project_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter

    companion object {
        val productList = mutableListOf<Product>() // data sederhana
        val stockHistoryList = mutableListOf<StockHistory>()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val tvWelcome = view.findViewById<TextView>(R.id.tvWelcome)
        val tvTotalProduk = view.findViewById<TextView>(R.id.tvTotalProduk)
        val btnTambah = view.findViewById<Button>(R.id.btnTambah)
        val btnPrediksi = view.findViewById<Button>(R.id.btnPrediksi)

        recyclerView = view.findViewById(R.id.rvProduk)
        adapter = ProductAdapter(productList, findNavController()) {
            updateProductCount()
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val sharedPref = requireActivity().getSharedPreferences(
            PrefConstants.PREF_NAME,
            AppCompatActivity.MODE_PRIVATE
        )
        val userName = sharedPref.getString(PrefConstants.KEY_USERNAME, "Pengguna")

        tvWelcome.text = getString(R.string.home_welcome_message, userName)

        updateProductCount()

        btnTambah.setOnClickListener {
            findNavController().navigate(R.id.produkFormFragment)
        }

        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        btnPrediksi.setOnClickListener {
            bottomNav?.selectedItemId = R.id.prediksiFragment
        }
    }

    override fun onResume() {
        super.onResume()

        val sharedPref = requireActivity().getSharedPreferences(
            PrefConstants.PREF_NAME,
            AppCompatActivity.MODE_PRIVATE
        )
        val userName = sharedPref.getString(PrefConstants.KEY_USERNAME, "Pengguna")
        view?.findViewById<TextView>(R.id.tvWelcome)?.text = "Halo, $userName"

        updateProductCount()

        adapter.notifyDataSetChanged()
    }

    private fun updateProductCount() {
        view?.findViewById<TextView>(R.id.tvTotalProduk)?.text =
            "Kamu memiliki ${productList.size} produk yang siap diprediksi"
    }
}
