package com.example.project_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_map.repository.HistoryTypeFilter
import com.example.project_map.repository.TimeFilter
import com.example.project_map.viewmodel.RiwayatViewModel
import com.google.android.material.button.MaterialButton

class RiwayatFragment : Fragment() {

    private val viewModel: RiwayatViewModel by viewModels()
    private lateinit var adapter: RiwayatAdapter

    private var selectedProductId: String? = null
    private var selectedTimeFilter = TimeFilter.TODAY
    private var selectedTypeFilter = HistoryTypeFilter.ALL

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_riwayat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val spinnerProduk = view.findViewById<Spinner>(R.id.spinnerProduk)
        val rvRiwayat = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvRiwayat)

        val tvMasuk = view.findViewById<TextView>(R.id.tvTotalMasuk)
        val tvKeluar = view.findViewById<TextView>(R.id.tvTotalKeluar)

        val btnToday = view.findViewById<MaterialButton>(R.id.btnToday)
        val btn7 = view.findViewById<MaterialButton>(R.id.btn7Days)
        val btn30 = view.findViewById<MaterialButton>(R.id.btn30Days)

        val btnAll = view.findViewById<MaterialButton>(R.id.btnAll)
        val btnMasuk = view.findViewById<MaterialButton>(R.id.btnMasuk)
        val btnKeluar = view.findViewById<MaterialButton>(R.id.btnKeluar)

        adapter = RiwayatAdapter(mutableListOf())
        rvRiwayat.layoutManager = LinearLayoutManager(requireContext())
        rvRiwayat.adapter = adapter

        val productNames = mutableListOf("Semua Produk")
        productNames.addAll(LocalData.productList.map { it.nama })

        spinnerProduk.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            productNames
        )

        spinnerProduk.setOnItemSelectedListener { position ->
            selectedProductId = if (position == 0) null else LocalData.productList[position - 1].id
            reload()
        }

        val timeButtons = listOf(btnToday, btn7, btn30)

        btnToday.setOnClickListener {
            selectedTimeFilter = TimeFilter.TODAY
            setActive(btnToday, timeButtons)
            reload()
        }

        btn7.setOnClickListener {
            selectedTimeFilter = TimeFilter.LAST_7_DAYS
            setActive(btn7, timeButtons)
            reload()
        }

        btn30.setOnClickListener {
            selectedTimeFilter = TimeFilter.LAST_30_DAYS
            setActive(btn30, timeButtons)
            reload()
        }

        val typeButtons = listOf(btnAll, btnMasuk, btnKeluar)

        btnAll.setOnClickListener {
            selectedTypeFilter = HistoryTypeFilter.ALL
            setActive(btnAll, typeButtons)
            reload()
        }

        btnMasuk.setOnClickListener {
            selectedTypeFilter = HistoryTypeFilter.MASUK
            setActive(btnMasuk, typeButtons)
            reload()
        }

        btnKeluar.setOnClickListener {
            selectedTypeFilter = HistoryTypeFilter.KELUAR
            setActive(btnKeluar, typeButtons)
            reload()
        }

        viewModel.riwayat.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)

            tvMasuk.text = "Masuk: ${
                list.filter { it.jenis == "MASUK" }.sumOf { it.jumlah }
            }"

            tvKeluar.text = "Keluar: ${
                list.filter { it.jenis == "KELUAR" }.sumOf { it.jumlah }
            }"
        }

        setActive(btnToday, timeButtons)
        setActive(btnAll, typeButtons)
        reload()
    }

    private fun reload() {
        viewModel.load(
            context = requireContext(),
            productId = selectedProductId,
            timeFilter = selectedTimeFilter,
            typeFilter = selectedTypeFilter
        )
    }

    private fun setActive(active: MaterialButton, buttons: List<MaterialButton>) {
        buttons.forEach { it.strokeWidth = 2 }
        active.strokeWidth = 0
    }

    private fun Spinner.setOnItemSelectedListener(onSelect: (Int) -> Unit) {
        this.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                onSelect(position)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }
}