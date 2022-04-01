package com.lugares.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase
import com.lugares.model.Lugar


class LugarDao {

    private var userCode: String
    private var firestore: FirebaseFirestore

    init {
        val user = Firebase.auth.currentUser?.email
        // val user = Firebase.auth.currentUser?.id
        userCode = "$user"
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }

    fun getAllData(): MutableLiveData<List<Lugar>> {

        val lugarList = MutableLiveData<List<Lugar>>()

        firestore.collection("lugaresApp")
            .document()
            .collection("myLugares")
            .addSnapshotListener{
                snapshot, e ->
                if (e != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = ArrayList<Lugar>()
                    val lugares = snapshot.documents
                    lugares.forEach{
                        val lugar = it.toObject(Lugar::class.java)
                        if (lugar != null) {
                            list.add(lugar)
                        }
                    }
                    lugarList.value = list
                }
            }
        return lugarList
    }

    fun saveLugar(lugar: Lugar) {
        val document: DocumentReference
        if (lugar.id.isEmpty()){
            document = firestore
                .collection("lugaresApp")
                .document(userCode)
                .collection("myLugares")
                .document()
            lugar.id = document.id
        } else {
            document = firestore
                .collection("lugaresApp")
                .document(userCode)
                .collection("myLugares")
                .document()
        }
        val set = document.set(lugar)
        set.addOnSuccessListener {
            Log.d("AddLugar", "Lugar added")
        }
            .addOnCanceledListener {
                Log.e("AddLugar", "Lugar was not added")
            }

    }

//    suspend fun updateLugar(lugar: Lugar)

    fun deleteLugar(lugar: Lugar) {
        if (lugar.id.isNotEmpty()) {
            firestore
                .collection("lugaresApp")
                .document(userCode)
                .collection("misLugares")
                .document(lugar.id)
                .delete()
                .addOnSuccessListener {
                Log.d("DeleteLugar", "Lugar deleted")
            }
                .addOnCanceledListener {
                    Log.e("DeleteLugar", "Lugar was not deleted")
                }

        }
    }
}