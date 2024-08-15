package com.example.gadgetrate.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.gadgetrate.R
import com.example.gadgetrate.databinding.FragmentProductRatingBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class ProductRatingFragment : Fragment() {

    private var _binding: FragmentProductRatingBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private val productsCollection = firestore.collection("products")
    private val reviewRatingsCollection = firestore.collection("review_ratings")
    private val usersCollection = firestore.collection("users")
    private val countersCollection = firestore.collection("counters")
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductRatingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prodId = arguments?.getString("prodId") ?: return
        loadProductDetails(prodId)
        setupRatingSpinner()

        binding.btnSubmit.setOnClickListener {
            saveReview(prodId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadProductDetails(prodId: String) {
        productsCollection.document(prodId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val productName = "Product Name: ${document.getString("productName")}" ?: ""
                    val productCategory = "Category: ${document.getString("productCategory")}" ?: ""
                    val productDescription = "Description: ${document.getString("productDescription")}" ?: ""
                    val imageUrl = document.getString("imageUrl") ?: ""

                    binding.tvProductName.text = productName
                    binding.tvProductCategory.text = productCategory
                    binding.tvProductDescription.text = productDescription

                    Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder)
                        .into(binding.ivProductImage)
                } else {
                    Toast.makeText(context, "Product not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to load product: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupRatingSpinner() {
        val ratings = listOf("1", "2", "3", "4", "5")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ratings)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spRating.adapter = adapter
    }

    private fun saveReview(prodId: String) {
        val productReview = binding.etUserReview.text.toString().trim()
        val productRating = binding.spRating.selectedItem.toString().toInt()

        if (productReview.isEmpty()) {
            Toast.makeText(context, "Please enter a review", Toast.LENGTH_SHORT).show()
            return
        }

        val user = auth.currentUser
        val userId = user?.uid ?: return

        usersCollection.document(userId).get()
            .addOnSuccessListener { userDocument ->
                val userName = userDocument.getString("userName") ?: "Anonymous"

                productsCollection.document(prodId).get()
                    .addOnSuccessListener { productDocument ->
                        if (productDocument.exists()) {
                            val productName = productDocument.getString("productName") ?: ""
                            val productCategory = productDocument.getString("productCategory") ?: ""
                            val productDescription = productDocument.getString("productDescription") ?: ""
                            val productImageUrl = productDocument.getString("imageUrl") ?: ""

                            val reviewDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())

                            incrementReviewNumber { reviewNo ->
                                val review = hashMapOf(
                                    "productId" to prodId,
                                    "productName" to productName,
                                    "productCategory" to productCategory,
                                    "productDescription" to productDescription,
                                    "productReview" to productReview,
                                    "productRating" to productRating,
                                    "userId" to userId,
                                    "userName" to userName,
                                    "productImageUrl" to productImageUrl,
                                    "reviewDate" to reviewDate,
                                    "reviewNo" to reviewNo
                                )

                                reviewRatingsCollection.add(review)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Review saved successfully", Toast.LENGTH_SHORT).show()
                                        parentFragmentManager.popBackStack()
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(context, "Failed to save review: ${exception.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Toast.makeText(context, "Product not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, "Failed to load product: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to load user: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun incrementReviewNumber(onReviewNumberGenerated: (Long) -> Unit) {
        val reviewCounterDocRef = countersCollection.document("review_counter")

        reviewCounterDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    firestore.runTransaction { transaction ->
                        val snapshot = transaction.get(reviewCounterDocRef)
                        val currentReviewNo = snapshot.getLong("currentReviewNo") ?: 0L
                        val newReviewNo = currentReviewNo + 1
                        transaction.update(reviewCounterDocRef, "currentReviewNo", newReviewNo)
                        newReviewNo
                    }.addOnSuccessListener { newReviewNo ->
                        onReviewNumberGenerated(newReviewNo)
                    }.addOnFailureListener { exception ->
                        Toast.makeText(context, "Failed to generate review number: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    reviewCounterDocRef.set(hashMapOf("currentReviewNo" to 1L))
                        .addOnSuccessListener {
                            onReviewNumberGenerated(1L)
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(context, "Failed to initialize review counter: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to retrieve review counter: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
