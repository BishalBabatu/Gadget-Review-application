package com.example.gadgetrate.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gadgetrate.R


import com.example.gadgetrate.databinding.FragmentViewAllProductsUserBinding
import com.example.gadgetrate.user.adapter.ProductsAdapter
import com.example.gadgetrate.user.model.Product
import com.google.firebase.firestore.FirebaseFirestore


class ViewAllProductsUser : Fragment() {

    private var _binding: FragmentViewAllProductsUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProductsAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val productsCollection = firestore.collection("products")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewAllProductsUserBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

        fetchCategories()

        binding.btnShowAllProducts.setOnClickListener {
            val selectedCategory = binding.spinner.selectedItem.toString()
            fetchProducts(selectedCategory)
        }
        return binding.root
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
                adapter = ProductsAdapter(products) { productId ->
                    // Navigate to ProductRatingFragment
                    val action = ViewAllProductsUserDirections.actionViewAllProductsUserToProductRatingFragment(productId)
                    findNavController().navigate(action)
                }
                binding.recyclerView.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to fetch products: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}