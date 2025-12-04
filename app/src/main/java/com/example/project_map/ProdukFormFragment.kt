package com.example.project_map

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class ProdukFormFragment : Fragment() {

    private var editIndex: Int? = null
    private val decimalFormat =
        NumberFormat.getInstance(Locale("in", "ID")) as DecimalFormat

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_produk_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val etNama = view.findViewById<EditText>(R.id.etNamaProduk)
        val etListedPrice = view.findViewById<EditText>(R.id.etListedPrice)
        val etDiscountedPrice = view.findViewById<EditText>(R.id.etDiscountedPrice)
        val etStok = view.findViewById<EditText>(R.id.etStokProduk)
        val cbPromo = view.findViewById<CheckBox>(R.id.cbPromo)
        val btnSimpan = view.findViewById<Button>(R.id.btnSimpan)
        val btnKembali = view.findViewById<Button>(R.id.btnKembali)

        fun setupFormatter(editText: EditText) {
            editText.addTextChangedListener(object : TextWatcher {
                private var current = ""
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s.toString() != current) {
                        editText.removeTextChangedListener(this)
                        val clean = s.toString().replace("[^\\d]".toRegex(), "")
                        if (clean.isNotEmpty()) {
                            val formatted = decimalFormat.format(clean.toDouble())
                            current = formatted
                            editText.setText(formatted)
                            editText.setSelection(formatted.length)
                        } else {
                            current = ""
                            editText.setText("")
                        }
                        editText.addTextChangedListener(this)
                    }
                }
            })
        }

        setupFormatter(etListedPrice)
        setupFormatter(etDiscountedPrice)

        fun parseHarga(input: String): Double? {
            val clean = input.replace(".", "").replace(",", "")
            return clean.toDoubleOrNull()
        }

        arguments?.let {
            editIndex = it.getInt("index", -1).takeIf { i -> i != -1 }
            val produk = editIndex?.let { i -> HomeFragment.productList[i] }

            produk?.let {
                etNama.setText(it.nama)
                etListedPrice.setText(decimalFormat.format(it.listedPrice))
                etDiscountedPrice.setText(decimalFormat.format(it.discountedPrice))
                etStok.setText(it.stok.toString())
                cbPromo.isChecked = it.promoAktif
            }
        }

        btnSimpan.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val listedPrice = parseHarga(etListedPrice.text.toString())
            val discountedPrice = parseHarga(etDiscountedPrice.text.toString())
            val stok = etStok.text.toString().toIntOrNull()
            val promoAktif = cbPromo.isChecked

            if (nama.isEmpty() || listedPrice == null || discountedPrice == null || stok == null) {
                Toast.makeText(requireContext(), "Harap isi semua data dengan benar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val priceGap = listedPrice - discountedPrice

            val newProduct = Product(
                nama = nama,
                listedPrice = listedPrice,
                discountedPrice = discountedPrice,
                priceGap = priceGap,
                stok = stok,
                promoAktif = promoAktif
            )

            AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi")
                .setMessage("Simpan data produk ini?")
                .setPositiveButton("Ya") { _, _ ->
                    if (editIndex != null) {
                        HomeFragment.productList[editIndex!!] = newProduct
                    } else {
                        HomeFragment.productList.add(newProduct)
                    }
                    findNavController().popBackStack()
                }
                .setNegativeButton("Tidak", null)
                .show()
        }

        btnKembali.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}
