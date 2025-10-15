package com.example.project_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProdukAdapter

    companion object {
        val productList = mutableListOf<Produk>()
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
        adapter = ProdukAdapter(productList, findNavController())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val sharedPref = requireActivity().getSharedPreferences(
            PrefConstants.PREF_NAME,
            AppCompatActivity.MODE_PRIVATE
        )
        val userName = sharedPref.getString(PrefConstants.KEY_USERNAME, "Pengguna")

        tvWelcome.text = getString(R.string.home_welcome_message, userName)

        val productCount = productList.size
        tvTotalProduk.text = resources.getQuantityString(R.plurals.home_product_count, productCount, productCount)

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
        view?.findViewById<TextView>(R.id.tvTotalProduk)?.text =
            "Kamu memiliki ${productList.size} produk yang siap diprediksi"

        adapter.notifyDataSetChanged()
    }
}
data class Produk(
    var nama: String,
    var harga: Double,
    var stok: Int,
    var diskon: Double,
    var kategori: String,
    var promoAktif: Boolean
)
class ProdukAdapter(
    private val data: MutableList<Produk>,
    private val navController: androidx.navigation.NavController
) : RecyclerView.Adapter<ProdukAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvNamaProduk)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_produk, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val produk = data[position]
        holder.tvNama.text = "${produk.nama} - Rp${produk.harga}"

        holder.btnEdit.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("index", position)
                putString("nama", produk.nama)
                putDouble("harga", produk.harga)
            }
            navController.navigate(R.id.produkFormFragment, bundle)
        }

        holder.btnDelete.setOnClickListener {
            val context = holder.itemView.context

            AlertDialog.Builder(context)
                .setTitle("Konfirmasi Hapus")
                .setMessage("Apakah Anda yakin ingin menghapus produk '${produk.nama}'?")
                .setPositiveButton("Ya") { _, _ ->
                    val currentPosition = holder.adapterPosition
                    if (currentPosition != RecyclerView.NO_POSITION) {
                        data.removeAt(currentPosition)
                        notifyItemRemoved(currentPosition)
                    }
                }
                .setNegativeButton("Tidak", null)
                .show()
        }
    }
    override fun getItemCount() = data.size
}
