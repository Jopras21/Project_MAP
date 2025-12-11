package com.example.project_map

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_map.viewmodel.RiwayatViewModel

class RiwayatFragment : Fragment() {

    private val viewModel: RiwayatViewModel by viewModels()
    private lateinit var adapter: RiwayatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_riwayat, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val rv = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvRiwayat)
        rv.layoutManager = LinearLayoutManager(requireContext())

        adapter = RiwayatAdapter(mutableListOf())
        rv.adapter = adapter

        viewModel.start(requireContext())

        viewModel.riwayat.observe(viewLifecycleOwner) { list ->
            adapter.updateData(list)
        }
    }
}
