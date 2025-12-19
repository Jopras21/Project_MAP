package com.example.project_map

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log

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

    private val CHANNEL_ID = "prediction_channel"
    private val notifPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                Toast.makeText(
                    requireContext(),
                    "Notifikasi tidak diizinkan",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_prediksi, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkNotificationPermission()
        createNotificationChannel()

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
            btnPrediksi.isEnabled = false
            return
        }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            productList.map { it.nama }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spProduk.adapter = adapter

        selectedProduct = productList.first()
        tampilkanRingkasan()

        spProduk.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedProduct = productList[position]
                tampilkanRingkasan()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun tampilkanRingkasan() {
        val p = selectedProduct ?: return
        tvNama.text = p.nama
        tvHarga.text = "Harga: Rp${p.listedPrice.toInt()}"
        tvStok.text = "Stok: ${p.stok}"
        tvPromo.text = "Promo: ${if (p.promoAktif) "Aktif" else "Tidak"}"
    }

    private fun setupButton() {
        btnPrediksi.setOnClickListener {
            val p = selectedProduct ?: return@setOnClickListener

            val serviceIntent = Intent(requireContext(), PredictionService::class.java)
            ContextCompat.startForegroundService(requireContext(), serviceIntent)

            val request = PredictionRequest(
                listed_price = p.listedPrice,
                discounted_price = p.discountedPrice,
                price_gap = p.priceGap,
                stok = p.stok,
                promo_aktif = if (p.promoAktif) 1 else 0
            )

            tvHasil.text = "Mengirim data ke server..."
            btnPrediksi.isEnabled = false

            Log.d(
                "ML_PREDICTION",
                "Input ML -> listed_price=${p.listedPrice}, discounted_price=${p.discountedPrice}, " +
                        "price_gap=${p.priceGap}, stok=${p.stok}, promo_aktif=${if (p.promoAktif) 1 else 0}"
            )


            kirimPrediksi(request)
        }
    }

    private fun kirimPrediksi(request: PredictionRequest) {
        apiService.predictSales(request).enqueue(object : Callback<PredictionResponse> {

            override fun onResponse(
                call: Call<PredictionResponse>,
                response: Response<PredictionResponse>
            ) {
                stopPredictionService()
                btnPrediksi.isEnabled = true

                if (response.isSuccessful && response.body() != null) {
                    val hasil = response.body()!!.prediksi_penjualan
                    Log.d(
                        "ML_PREDICTION",
                        "Output ML <- prediksi_penjualan=$hasil"
                    )
                    val textHasil = "Prediksi penjualan: ${"%.0f".format(hasil)} unit"

                    tvHasil.text = textHasil

                    showResultNotification("Selesai", textHasil)

                } else {
                    val errorMsg = "Gagal mendapat hasil prediksi"
                    tvHasil.text = errorMsg
                    showResultNotification("Gagal", errorMsg)
                }
            }

            override fun onFailure(call: Call<PredictionResponse>, t: Throwable) {
                Log.e(
                    "ML_PREDICTION",
                    "Error saat request ML: ${t.message}"
                )
                stopPredictionService()
                btnPrediksi.isEnabled = true

                val errorMsg = "Error koneksi: ${t.message}"
                tvHasil.text = errorMsg
                showResultNotification("Error", "Koneksi gagal")
            }
        })
    }

    private fun stopPredictionService() {
        requireContext().stopService(
            Intent(requireContext(), PredictionService::class.java)
        )
    }

    private fun showResultNotification(title: String, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notificationId = System.currentTimeMillis().toInt()

        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val manager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, builder.build())
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Prediction Result"
            val descriptionText = "Menampilkan hasil prediksi"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}