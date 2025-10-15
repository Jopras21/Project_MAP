package com.example.project_map

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

class ProdukFormFragment : Fragment() {

    private var editIndex: Int? = null
    private val decimalFormat: DecimalFormat = NumberFormat.getInstance(Locale("in", "ID")) as DecimalFormat

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_produk_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etNama = view.findViewById<EditText>(R.id.etNamaProduk)
        val etHarga = view.findViewById<EditText>(R.id.etHargaProduk)
        val etStok = view.findViewById<EditText>(R.id.etStokProduk)
        val etDiskon = view.findViewById<EditText>(R.id.etDiskonProduk)
        val spKategori = view.findViewById<Spinner>(R.id.spKategori)
        val cbPromo = view.findViewById<CheckBox>(R.id.cbPromo)
        val btnSimpan = view.findViewById<Button>(R.id.btnSimpan)
        val btnKembali = view.findViewById<Button>(R.id.btnKembali)
        val kategoriList = listOf("Makanan", "Minuman", "Lainnya")
        val adapterSpinner = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, kategoriList)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spKategori.adapter = adapterSpinner

        etHarga.addTextChangedListener(object : TextWatcher {
            private var current = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    etHarga.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("[^\\d]".toRegex(), "")

                    if (cleanString.isNotEmpty()) {
                        val parsed = cleanString.toDouble()
                        val formatted = decimalFormat.format(parsed)
                        current = formatted
                        etHarga.setText(formatted)
                        etHarga.setSelection(formatted.length)
                    } else {
                        current = ""
                        etHarga.setText("")
                    }

                    etHarga.addTextChangedListener(this)
                }
            }
        })
        arguments?.let { args ->
            editIndex = args.getInt("index", -1).takeIf { it != -1 }
            val produk = editIndex?.let { HomeFragment.productList[it] }

            produk?.let {
                etNama.setText(it.nama)
                etHarga.setText(decimalFormat.format(it.harga))
                etStok.setText(it.stok.toString())
                etDiskon.setText(it.diskon.toString())
                spKategori.setSelection(kategoriList.indexOf(it.kategori))
                cbPromo.isChecked = it.promoAktif
            }
        }

        btnSimpan.setOnClickListener {
            val nama = etNama.text.toString().trim()
            val hargaString = etHarga.text.toString().replace(".", "").replace(",", "")
            val harga = hargaString.toDoubleOrNull()
            val stok = etStok.text.toString().toIntOrNull()
            val diskon = etDiskon.text.toString().toDoubleOrNull()
            val kategori = spKategori.selectedItem.toString()
            val promoAktif = cbPromo.isChecked

            if (nama.isEmpty() || harga == null || stok == null || diskon == null) {
                Toast.makeText(requireContext(), "Harap isi semua data dengan benar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi")
                .setMessage("Apakah Anda yakin ingin menyimpan data produk ini?")
                .setPositiveButton("Ya") { _, _ ->
                    if (editIndex != null) {
                        HomeFragment.productList[editIndex!!] = Produk(nama, harga, stok, diskon, kategori, promoAktif)
                        Toast.makeText(requireContext(), "Produk berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                    } else {
                        HomeFragment.productList.add(Produk(nama, harga, stok, diskon, kategori, promoAktif))
                        Toast.makeText(requireContext(), "Produk berhasil disimpan!", Toast.LENGTH_SHORT).show()
                    }
                    findNavController().popBackStack() // Kembali ke halaman sebelumnya
                }
                .setNegativeButton("Tidak", null)
                .show()
        }
        btnKembali.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}