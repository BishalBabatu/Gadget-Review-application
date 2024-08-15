package com.example.gadgetrate.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gadgetrate.databinding.FragmentRecentlyReviewedProductsBinding
import com.example.gadgetrate.user.adapter.LatestReviewAdapter
import com.example.gadgetrate.user.model.LatestReviews
import com.google.firebase.firestore.FirebaseFirestore

class RecentlyReviewedProducts : Fragment() {

    private var _binding: FragmentRecentlyReviewedProductsBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private val reviewRatingsCollection = firestore.collection("review_ratings")
    private lateinit var latestReviewAdapter: LatestReviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecentlyReviewedProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        latestReviewAdapter = LatestReviewAdapter()
        binding.recyclerView.adapter = latestReviewAdapter

        fetchRecentReviews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchRecentReviews() {
        reviewRatingsCollection.orderBy("reviewNo", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(5)
            .get()
            .addOnSuccessListener { documents ->
                val reviews = mutableListOf<LatestReviews>()
                for (document in documents) {
                    val review = document.toObject(LatestReviews::class.java)
                    reviews.add(review)
                }
                latestReviewAdapter.submitList(reviews)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to fetch reviews: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
