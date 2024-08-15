package com.example.gadgetrate.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gadgetrate.databinding.FragmentUserDashboardBinding
import com.example.gadgetrate.user.adapter.LatestReviewAdapter
import com.example.gadgetrate.user.adapter.TopRatedProductsAdapter
import com.example.gadgetrate.user.adapter.UserCommentsAdapter
import com.example.gadgetrate.user.model.LatestReviews
import com.example.gadgetrate.user.model.TopRatedProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class UserDashboard : Fragment() {

    private var _binding: FragmentUserDashboardBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private val reviewRatingsCollection = firestore.collection("review_ratings")
    private val auth = FirebaseAuth.getInstance()

    private lateinit var latestReviewAdapter: LatestReviewAdapter
    private lateinit var topRatedProductsAdapter: TopRatedProductsAdapter
    private lateinit var userCommentsAdapter: UserCommentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        fetchRecentReviews()
        fetchTopRatedProducts()
        fetchUserComments()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerViews() {
        binding.rvRecentlyReviewdProducts.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        latestReviewAdapter = LatestReviewAdapter()
        binding.rvRecentlyReviewdProducts.adapter = latestReviewAdapter

        binding.rvTopRatedProducts.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        topRatedProductsAdapter = TopRatedProductsAdapter()
        binding.rvTopRatedProducts.adapter = topRatedProductsAdapter

        binding.rvRecentCommentByYou.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        userCommentsAdapter = UserCommentsAdapter()
        binding.rvRecentCommentByYou.adapter = userCommentsAdapter
    }

    private fun fetchRecentReviews() {
        reviewRatingsCollection.orderBy("reviewDate", Query.Direction.DESCENDING)
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

    private fun fetchTopRatedProducts() {
        reviewRatingsCollection.get()
            .addOnSuccessListener { documents ->
                val productRatingsMap = mutableMapOf<String, MutableList<Pair<Double, LatestReviews>>>()
                for (document in documents) {
                    val productId = document.getString("productId") ?: continue
                    val rating = document.getDouble("productRating") ?: continue
                    val product = document.toObject(LatestReviews::class.java)
                    if (!productRatingsMap.containsKey(productId)) {
                        productRatingsMap[productId] = mutableListOf()
                    }
                    productRatingsMap[productId]?.add(Pair(rating, product))
                }

                val topRatedProducts = productRatingsMap.map { entry ->
                    val averageRating = entry.value.map { it.first }.average()
                    val productInfo = entry.value.first().second
                    TopRatedProduct(
                        productId = entry.key,
                        productName = productInfo.productName,
                        productCategory = productInfo.productCategory,
                        productDescription = productInfo.productDescription,
                        productImageUrl = productInfo.productImageUrl,
                        averageRating = averageRating
                    )
                }.sortedByDescending { it.averageRating }.take(5)

                topRatedProductsAdapter.submitList(topRatedProducts)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to fetch top-rated products: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun fetchUserComments() {
        val userId = auth.currentUser?.uid ?: return

        reviewRatingsCollection.whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                val userReviews = mutableListOf<LatestReviews>()
                for (document in documents) {
                    val review = document.toObject(LatestReviews::class.java)
                    userReviews.add(review)
                }
                userCommentsAdapter.submitList(userReviews)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to fetch user comments: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
