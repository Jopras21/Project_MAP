package com.example.project_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter

    private var productListener: ListenerRegistration? = null
    private var historyListener: ListenerRegistration? = null
    private var userListener: ListenerRegistration? = null   

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val tvTotalProduk = view.findViewById<TextView>(R.id.tvTotalProduk)
        val btnTambah = view.findViewById<Button>(R.id.btnTambah)
        val btnPrediksi = view.findViewById<Button>(R.id.btnPrediksi)

        recyclerView = view.findViewById(R.id.rvProduk)
        adapter = ProductAdapter(LocalData.productList, findNavController()) {
            updateProductCount()
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        updateProductCount()

        btnTambah.setOnClickListener {
            findNavController().navigate(R.id.produkFormFragment)
        }

        val bottomNav = activity?.findViewById<BottomNavigationView>(R.id.bottom_nav)
        btnPrediksi.setOnClickListener {
            bottomNav?.selectedItemId = R.id.prediksiFragment
        }

        startFirestoreListeners()

        startUserListener()
    }

    private fun startUserListener() {
        val sharedPref = requireActivity().getSharedPreferences(
            PrefConstants.PREF_NAME,
            AppCompatActivity.MODE_PRIVATE
        )
        val email = sharedPref.getString(PrefConstants.KEY_EMAIL, "") ?: ""

        if (email.isEmpty()) return

        userListener = Firebase.firestore
            .collection("users")
            .document(email)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(User::class.java)
                    if (user != null) {
                        view?.findViewById<TextView>(R.id.tvWelcome)?.text =
                            "Halo, ${user.name}"

                        sharedPref.edit()
                            .putString(PrefConstants.KEY_USERNAME, user.name)
                            .apply()
                    }
                }
            }
    }

    private fun startFirestoreListeners() {
        productListener = FirestoreService.listenProducts(
            requireContext(),
            onChanged = { list ->
                LocalData.productList.clear()
                LocalData.productList.addAll(list)
                adapter.notifyDataSetChanged()
                updateProductCount()
            },
            onError = { e ->
                Toast.makeText(requireContext(),
                    "Gagal memuat produk: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )

        historyListener = FirestoreService.listenStockHistory(
            requireContext(),
            onChanged = { list ->
                LocalData.stockHistoryList.clear()
                LocalData.stockHistoryList.addAll(list)
            },
            onError = { e ->
                Toast.makeText(requireContext(),
                    "Gagal memuat riwayat: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        productListener?.remove()
        historyListener?.remove()
        userListener?.remove()
    }

    private fun updateProductCount() {
        view?.findViewById<TextView>(R.id.tvTotalProduk)?.text =
            "Kamu memiliki ${LocalData.productList.size} produk yang siap diprediksi"
    }
}
