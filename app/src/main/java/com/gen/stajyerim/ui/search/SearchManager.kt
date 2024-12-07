package com.gen.stajyerim.ui.search

import com.gen.stajyerim.model.Job
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class SearchManager(private val db: FirebaseFirestore) {
    fun searchJobs(query: String, onResult: (List<Job>) -> Unit, onError: (Exception) -> Unit) {
        if (query.isNotBlank()) {
            db.collection("posts")
                .orderBy("title")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .addOnSuccessListener { snapshot ->
                    val jobs = snapshot.documents.mapNotNull { it.toObject(Job::class.java) }
                    onResult(jobs)
                }
                .addOnFailureListener { exception ->
                    onError(exception)
                }
        } else {
            db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { snapshot ->
                    val jobs = snapshot.documents.mapNotNull { it.toObject(Job::class.java) }
                    onResult(jobs)
                }
                .addOnFailureListener { exception ->
                    onError(exception)
                }
        }
    }
}