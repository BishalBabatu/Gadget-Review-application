package com.example.gadgetrate.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.gadgetrate.R
import com.example.gadgetrate.databinding.FragmentAdminDashboardBinding


class AdminDashboard : Fragment() {

    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCreateNewCategory.setOnClickListener {
            findNavController().navigate(R.id.action_adminDashboard_to_createCategory)
        }

        binding.btnViewAllCategories.setOnClickListener {
            findNavController().navigate(R.id.action_adminDashboard_to_viewAllCategories)
        }

        binding.btnAddNewProduct.setOnClickListener {
            findNavController().navigate(R.id.action_adminDashboard_to_addNewProduct)
        }

        binding.btnViewAllProducts.setOnClickListener {
            findNavController().navigate(R.id.action_adminDashboard_to_viewAllProducts)
        }

        binding.btnViewAllReviews.setOnClickListener {
            findNavController().navigate(R.id.action_adminDashboard_to_viewAllReviewsAdmin)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}