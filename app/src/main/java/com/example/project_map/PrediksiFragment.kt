package com.example.project_map

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import retrofit2.*

class PrediksiFragment : Fragment() {

    private lateinit var spProduk: Spinner
    private lateinit var btnPrediksi: MaterialButton
    private lateinit var tvHasil: TextView

    private lateinit var tvNama: TextView
    private lateinit var tvHarga: TextView
    private lateinit var tvStok: TextView
    private lateinit var tvPromo: TextView

    private var selectedProduct: Product? = null
    private val apiService by lazy { ApiClient.apiService }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_prediksi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        spProduk = view.findViewById(R.id.spProduk)
        btnPrediksi = view.findViewById(R.id.btnPrediksiSekarang)
        tvHasil = view.findViewById(R.id.tvHasilPrediksi)

        tvNama = view.findViewById(R.id.tvRingkasanNama)
        tvHarga = view.findViewById(R.id.tvRingkasanHarga)
        tvStok = view.findViewById(R.id.tvRingkasanStok)
        tvPromo = view.findViewById(R.id.tvRingkasanPromo)

        setupSpinner()
        setupButton()
    }

    private fun setupSpinner() {
        val productList = LocalData.productList

        if (productList.isEmpty()) {
            Toast.makeText(requireContext(), "Belum ada produk", Toast.LENGTH_SHORT).show()
            return
        }

        val namaList = productList.map { it.nama }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, namaList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spProduk.adapter = adapter

        selectedProduct = productList[0]
        tampilkanRingkasan()

        spProduk.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedProduct = productList[position]
                tampilkanRingkasan()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun tampilkanRingkasan() {
        val p = selectedProduct ?: return

        tvNama.text = p.nama
        tvHarga.text = "Harga: Rp${p.discountedPrice.toInt()}"
        tvStok.text = "Stok: ${p.stok}"
        tvPromo.text = "Promo: ${if (p.promoAktif) "Aktif" else "Tidak"}"
    }

    private fun setupButton() {
        btnPrediksi.setOnClickListener {
            val p = selectedProduct ?: return@setOnClickListener

            val request = PredictionRequest(
                listed_price = p.listedPrice,
                discounted_price = p.discountedPrice,
                price_gap = p.priceGap,
                stok = p.stok,
                promo_aktif = if (p.promoAktif) 1 else 0
            )

            tvHasil.text = "Mengirim data ke server..."
            kirimPrediksi(request)
        }
    }

    private fun kirimPrediksi(request: PredictionRequest) {
        apiService.predictSales(request).enqueue(object : Callback<PredictionResponse> {

            override fun onResponse(call: Call<PredictionResponse>, response: Response<PredictionResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val hasil = response.body()!!.prediksi_penjualan
                    tvHasil.text = "Prediksi penjualan: ${"%.0f".format(hasil)} unit"
                } else {
                    tvHasil.text = "Gagal mendapat hasil"
                }
            }

            override fun onFailure(call: Call<PredictionResponse>, t: Throwable) {
                tvHasil.text = "Error koneksi: ${t.message}"
            }
        })
    }
}
