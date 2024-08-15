package com.example.gadgetrate.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gadgetrate.admin.adapter.ProductsAdapter
import com.example.gadgetrate.admin.model.Product
import com.example.gadgetrate.databinding.FragmentViewAllProductsBinding
import com.google.firebase.firestore.FirebaseFirestore

class ViewAllProducts : Fragment() {

    private var _binding: FragmentViewAllProductsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProductsAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val productsCollection = firestore.collection("products")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewAllProductsBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        fetchCategories()

        binding.btnShowAllProducts.setOnClickListener {
            val selectedCategory = binding.spinner.selectedItem.toString()
            fetchProducts(selectedCategory)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchCategories() {
        firestore.collection("categories").get()
            .addOnSuccessListener { documents ->
                val categories = mutableListOf<String>()
                for (document in documents) {
                    val categoryName = document.getString("categoryName")
                    if (!categoryName.isNullOrEmpty()) {
                        categories.add(categoryName)
                    }
                }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinner.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to fetch categories: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchProducts(category: String) {
        productsCollection.whereEqualTo("productCategory", category).get()
            .addOnSuccessListener { documents ->
                val products = mutableListOf<Product>()
                for (document in documents) {
                    val product = document.toObject(Product::class.java)
                    products.add(product)
                }
                adapter = ProductsAdapter(products)
                binding.recyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to fetch products: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
