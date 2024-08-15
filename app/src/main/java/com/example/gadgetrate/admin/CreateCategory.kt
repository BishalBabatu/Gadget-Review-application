package com.example.gadgetrate.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.gadgetrate.databinding.FragmentCreateCategoryBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class CreateCategory : Fragment() {

    private var _binding: FragmentCreateCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAddCategory.setOnClickListener {
            if (validateFields()) {
                saveCategoryToFirestore()
            }
        }
    }

    private fun validateFields(): Boolean {
        if (binding.etCategoryName.text.toString().trim().isEmpty()) {
            Toast.makeText(context, "Please enter a category name.", Toast.LENGTH_SHORT).show()
            binding.etCategoryName.requestFocus()
            return false
        }

        if (binding.etCategoryDescription.text.toString().trim().isEmpty()) {
            Toast.makeText(context, "Please enter a category description.", Toast.LENGTH_SHORT).show()
            binding.etCategoryDescription.requestFocus()
            return false
        }

        return true
    }

    private fun saveCategoryToFirestore() {
        val categoryName = binding.etCategoryName.text.toString().trim()
        val categoryDescription = binding.etCategoryDescription.text.toString().trim()


        val newCategoryRef = FirebaseFirestore.getInstance().collection("categories").document()


        val categoryData = hashMapOf(
            "categoryId" to newCategoryRef.id,
            "categoryName" to categoryName,
            "categoryDescription" to categoryDescription,
            "createdDateTime" to FieldValue.serverTimestamp() 
        )

        newCategoryRef.set(categoryData)
            .addOnSuccessListener {
                Toast.makeText(context, "Category added successfully.", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to add category: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        binding.etCategoryName.text?.clear()
        binding.etCategoryDescription.text?.clear()
        binding.etCategoryName.requestFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
