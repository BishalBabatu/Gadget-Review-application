package com.example.gadgetrate.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gadgetrate.R
import com.example.gadgetrate.admin.adapter.CategoriesAdapter
import com.example.gadgetrate.admin.adapter.Category
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects

class ViewAllCategories : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoriesAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val categoriesCollection = firestore.collection("categories")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val view = inflater.inflate(R.layout.fragment_view_all_categories, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        fetchCategories()

        return view
    }

    private fun fetchCategories() {
        categoriesCollection.get()
            .addOnSuccessListener { documents ->
                val categories = documents.toObjects<Category>()
                adapter = CategoriesAdapter(categories)
                recyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }
    }
}
