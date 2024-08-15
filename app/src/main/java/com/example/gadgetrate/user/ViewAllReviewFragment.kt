package com.example.gadgetrate.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gadgetrate.databinding.FragmentViewAllReviewBinding
import com.example.gadgetrate.user.adapter.ReviewAdapter
import com.example.gadgetrate.user.model.Review
import com.google.firebase.firestore.FirebaseFirestore

class ViewAllReviewFragment : Fragment() {

    private var _binding: FragmentViewAllReviewBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private val reviewRatingsCollection = firestore.collection("review_ratings")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewAllReviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategorySpinner()
        binding.btnShowAllReviews.setOnClickListener {
            val selectedCategory = binding.spCategories.selectedItem.toString()
            fetchReviews(selectedCategory)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupCategorySpinner() {
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
                binding.spCategories.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to fetch categories: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchReviews(category: String) {
        reviewRatingsCollection.whereEqualTo("productCategory", category).get()
            .addOnSuccessListener { documents ->
                val reviews = mutableListOf<Review>()
                for (document in documents) {
                    val review = document.toObject(Review::class.java)
                    reviews.add(review)
                }
                setupRecyclerView(reviews)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to fetch reviews: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupRecyclerView(reviews: List<Review>) {
        val adapter = ReviewAdapter(reviews)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }
}
