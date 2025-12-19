package com.example.project_map

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirestoreService {

    private val db = Firebase.firestore

    fun createUserInFirestore(email: String, name: String) {
        val userDoc = Firebase.firestore.collection("users").document(email)

        val data = hashMapOf(
            "email" to email,
            "name" to name,
            "photoUrl" to "",
            "photoBase64" to "",
            "createdAt" to System.currentTimeMillis()
        )

        userDoc.set(data)
    }

    fun getUserId(context: Context): String? {
        val sp = context.getSharedPreferences(
            PrefConstants.PREF_NAME,
            AppCompatActivity.MODE_PRIVATE
        )
        return sp.getString(PrefConstants.KEY_EMAIL, null)
    }

    fun listenProducts(
        context: Context,
        onChanged: (List<Product>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration? {
        val userId = getUserId(context) ?: return null

        return db.collection("products")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    onError(e)
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.mapNotNull { doc ->
                    val p = doc.toObject(Product::class.java)
                    p?.apply { id = doc.id }
                } ?: emptyList()

                onChanged(list)
            }
    }

    fun saveProduct(
        context: Context,
        product: Product,
        onComplete: (Boolean, Exception?) -> Unit
    ) {
        val userId = getUserId(context) ?: run {
            onComplete(false, IllegalStateException("User belum login"))
            return
        }

        product.userId = userId

        val col = db.collection("products")

        val docRef = if (product.id == null) {
            col.document()
        } else {
            col.document(product.id!!)
        }

        product.id = docRef.id

        docRef.set(product)
            .addOnSuccessListener { onComplete(true, null) }
            .addOnFailureListener { e -> onComplete(false, e) }
    }

    fun deleteProduct(
        productId: String,
        onComplete: (Boolean, Exception?) -> Unit
    ) {
        db.collection("products")
            .document(productId)
            .delete()
            .addOnSuccessListener { onComplete(true, null) }
            .addOnFailureListener { e -> onComplete(false, e) }
    }

    fun updateProductStock(
        productId: String,
        newStock: Int
    ) {
        db.collection("products")
            .document(productId)
            .update("stok", newStock)
    }

    fun listenStockHistory(
        context: Context,
        onChanged: (List<StockHistory>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration? {
        val userId = getUserId(context) ?: return null

        return db.collection("stock_history")
            .whereEqualTo("userId", userId)
            .orderBy("tanggal")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    onError(e)
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.mapNotNull { doc ->
                    val h = doc.toObject(StockHistory::class.java)
                    h?.apply { id = doc.id }
                } ?: emptyList()

                onChanged(list)
            }
    }

    fun addStockHistory(context: Context, history: StockHistory) {
        val userId = getUserId(context) ?: return

        history.userId = userId
        history.createdAt = System.currentTimeMillis()

        val docRef = db.collection("stock_history").document()
        history.id = docRef.id

        docRef.set(history)
    }

}
