package com.example.project_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RiwayatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RiwayatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_riwayat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.rvRiwayat)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = RiwayatAdapter(HomeFragment.stockHistoryList)
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}
