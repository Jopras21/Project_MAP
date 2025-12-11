package com.example.project_map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RiwayatAdapter(
    private val data: MutableList<StockHistory>
) : RecyclerView.Adapter<RiwayatAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvNamaProduk)
        val tvJenis: TextView = view.findViewById(R.id.tvJenis)
        val tvJumlah: TextView = view.findViewById(R.id.tvJumlah)
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_riwayat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.tvNama.text = item.namaProduk
        holder.tvJumlah.text = "Jumlah: ${item.jumlah}"
        holder.tvTanggal.text = item.tanggal

        if (item.jenis == "MASUK") {
            holder.tvJenis.text = "MASUK"
            holder.tvJenis.setTextColor(0xFF2E7D32.toInt())
        } else {
            holder.tvJenis.text = "KELUAR"
            holder.tvJenis.setTextColor(0xFFC62828.toInt())
        }
    }

    override fun getItemCount(): Int = data.size

    fun updateData(newList: List<StockHistory>) {
        data.clear()
        data.addAll(newList)
        notifyDataSetChanged()
    }
}
