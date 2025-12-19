package com.example.project_map

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class ProductAdapter(
    private val data: MutableList<Product>,
    private val navController: NavController,
    private val onDataChanged: (() -> Unit)? = null
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvNamaProduk)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
        val btnMasuk: Button = view.findViewById(R.id.btnMasuk)
        val btnKeluar: Button = view.findViewById(R.id.btnKeluar)
    }

    private val formatter = DecimalFormat("#,###")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_produk, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val produk = data[position]

        holder.tvNama.text =
            "${produk.nama} | Rp${formatter.format(produk.listedPrice)} | Stok: ${produk.stok}"

        holder.btnEdit.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("index", position)
            }
            navController.navigate(R.id.produkFormFragment, bundle)
        }

        holder.btnDelete.setOnClickListener {
            val ctx: Context = holder.itemView.context
            AlertDialog.Builder(ctx)
                .setTitle("Konfirmasi Hapus")
                .setMessage("Hapus produk '${produk.nama}'?")
                .setPositiveButton("Ya") { _, _ ->
                    val productId = produk.id
                    if (productId != null) {
                        FirestoreService.deleteProduct(productId) { success, e ->
                            if (!success) {
                                android.widget.Toast.makeText(
                                    ctx,
                                    "Gagal hapus: ${e?.message}",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
                .setNegativeButton("Tidak", null)
                .show()
        }

        holder.btnMasuk.setOnClickListener {
            showInputDialog(holder.itemView.context, posisi = position, isMasuk = true)
        }

        holder.btnKeluar.setOnClickListener {
            showInputDialog(holder.itemView.context, posisi = position, isMasuk = false)
        }
    }

    private fun showInputDialog(context: Context, posisi: Int, isMasuk: Boolean) {
        val produk = data[posisi]

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_stok, null)
        val tvJudul = view.findViewById<TextView>(R.id.tvJudulDialog)
        val etJumlah = view.findViewById<TextInputEditText>(R.id.etJumlah)
        val btnBatal = view.findViewById<Button>(R.id.btnBatal)
        val btnSimpan = view.findViewById<Button>(R.id.btnSimpan)

        tvJudul.text = if (isMasuk) "Barang Masuk" else "Barang Keluar"

        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .create()

        btnBatal.setOnClickListener { dialog.dismiss() }

        btnSimpan.setOnClickListener {
            val jumlah = etJumlah.text.toString().toIntOrNull()

            if (jumlah == null || jumlah <= 0) {
                etJumlah.error = "Masukkan jumlah yang benar"
                return@setOnClickListener
            }

            if (!isMasuk && jumlah > produk.stok) {
                etJumlah.error = "Stok tidak mencukupi"
                return@setOnClickListener
            }

            if (isMasuk) produk.stok += jumlah else produk.stok -= jumlah

            produk.id?.let { id ->
                FirestoreService.updateProductStock(id, produk.stok)
            }

            val tanggal = SimpleDateFormat(
                "dd-MM-yyyy HH:mm",
                Locale.getDefault()
            ).format(Date())

            val history = StockHistory(
                productId = produk.id ?: "",
                namaProduk = produk.nama,
                jumlah = jumlah,
                jenis = if (isMasuk) "MASUK" else "KELUAR",
                tanggal = tanggal
            )

            history.createdAt = System.currentTimeMillis()

            FirestoreService.addStockHistory(context, history)

            notifyItemChanged(posisi)
            onDataChanged?.invoke()
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun getItemCount(): Int = data.size
}
