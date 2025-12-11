package com.example.project_map

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_map.viewmodel.HomeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val tvWelcome = view.findViewById<TextView>(R.id.tvWelcome)
        val tvTotalProduk = view.findViewById<TextView>(R.id.tvTotalProduk)
        val btnTambah = view.findViewById<Button>(R.id.btnTambah)
        val btnPrediksi = view.findViewById<Button>(R.id.btnPrediksi)

        adapter = ProductAdapter(LocalData.productList, findNavController())
        val rv = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvProduk)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        viewModel.start(requireContext())

        viewModel.user.observe(viewLifecycleOwner) { user ->
            tvWelcome.text = "Halo, ${user.name}"
        }

        viewModel.products.observe(viewLifecycleOwner) { list ->
            LocalData.productList.clear()
            LocalData.productList.addAll(list)
            adapter.notifyDataSetChanged()
            tvTotalProduk.text = "Kamu memiliki ${list.size} produk yang siap diprediksi"
        }

        viewModel.history.observe(viewLifecycleOwner) { list ->
            LocalData.stockHistoryList.clear()
            LocalData.stockHistoryList.addAll(list)
        }

        btnTambah.setOnClickListener {
            findNavController().navigate(R.id.produkFormFragment)
        }

        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        btnPrediksi.setOnClickListener {
            bottomNav?.selectedItemId = R.id.prediksiFragment
        }
    }
}
