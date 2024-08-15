package com.example.gadgetrate.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gadgetrate.admin.adapter.AllUsersReviewAdapter
import com.example.gadgetrate.admin.model.Review
import com.example.gadgetrate.databinding.FragmentViewAllReviewsAdminBinding
import com.example.gadgetrate.user.adapter.SingleUserReviewAdapter


import com.google.firebase.firestore.FirebaseFirestore

class ViewAllReviewsAdmin : Fragment() {

    private var _binding: FragmentViewAllReviewsAdminBinding? = null
    private val binding get() = _binding!!
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")
    private val reviewRatingsCollection = firestore.collection("review_ratings")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentViewAllReviewsAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUserSpinner()
        binding.btnDisplayReviewSelectedUser.setOnClickListener {
            val selectedUser = binding.spUsers.selectedItem.toString()
            if (selectedUser == "Select User Name") {
                Toast.makeText(context, "Please select a user", Toast.LENGTH_SHORT).show()
            } else {
                fetchReviewsBySelectedUser()
            }
        }

        binding.btnDisplayReviewAllUser.setOnClickListener {
            fetchReviewsByAllUsers()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupUserSpinner() {
        usersCollection.get()
            .addOnSuccessListener { documents ->
                val users = mutableListOf("Select User Name")
                for (document in documents) {
                    val userName = document.getString("userName")
                    if (!userName.isNullOrEmpty()) {
                        users.add(userName)
                    }
                }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, users)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spUsers.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to fetch users: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchReviewsBySelectedUser() {
        val selectedUserName = binding.spUsers.selectedItem.toString()
        usersCollection.whereEqualTo("userName", selectedUserName).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val userId = document.id
                    reviewRatingsCollection.whereEqualTo("userId", userId).get()
                        .addOnSuccessListener { reviewDocuments ->
                            val reviews = mutableListOf<Review>()
                            for (reviewDocument in reviewDocuments) {
                                val review = reviewDocument.toObject(Review::class.java)
                                reviews.add(review)
                            }
                            setupSingleUserRecyclerView(reviews)
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(context, "Failed to fetch reviews: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to fetch user data: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchReviewsByAllUsers() {
        reviewRatingsCollection.get()
            .addOnSuccessListener { documents ->
                val reviews = mutableListOf<Review>()
                for (document in documents) {
                    val review = document.toObject(Review::class.java)
                    reviews.add(review)
                }
                setupAllUsersRecyclerView(reviews)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to fetch reviews: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupSingleUserRecyclerView(reviews: List<Review>) {
        val adapter = SingleUserReviewAdapter(reviews)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }

    private fun setupAllUsersRecyclerView(reviews: List<Review>) {
        val adapter = AllUsersReviewAdapter(reviews)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter
    }
}
