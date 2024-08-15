package com.example.gadgetrate.admin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.gadgetrate.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddNewProduct : Fragment() {

    private lateinit var ivProductImage: ImageView
    private lateinit var spProductCategory: Spinner
    private lateinit var etProductName: TextInputEditText
    private lateinit var etProductDescription: TextInputEditText
    private lateinit var btnSaveProductDetails: Button
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    private var imageUri: Uri? = null

    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
            ivProductImage.setImageURI(imageUri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_add_new_product, container, false)
        ivProductImage = view.findViewById(R.id.ivProductImage)
        spProductCategory = view.findViewById(R.id.spProductCategory)
        etProductName = view.findViewById(R.id.etProductName)
        etProductDescription = view.findViewById(R.id.etProductDescription)
        btnSaveProductDetails = view.findViewById(R.id.btnSaveProductDetails)

        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        ivProductImage.setOnClickListener {
            openImagePicker()
        }

        btnSaveProductDetails.setOnClickListener {
            saveProductDetails()
        }

        fetchCategories()

        return view
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
                spProductCategory.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to fetch categories: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectImageLauncher.launch(intent)
    }

    private fun saveProductDetails() {
        val productName = etProductName.text.toString().trim()
        val productCategory = spProductCategory.selectedItem.toString()
        val productDescription = etProductDescription.text.toString().trim()

        if (productName.isEmpty()) {
            etProductName.error = "Product name is required"
            etProductName.requestFocus()
            return
        }
        if (productDescription.isEmpty()) {
            etProductDescription.error = "Product description is required"
            etProductDescription.requestFocus()
            return
        }


        val productId = UUID.randomUUID().toString()
        if (imageUri != null) {
            val storageRef = storage.reference.child("product_images/$productId")
            storageRef.putFile(imageUri!!)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        saveProductToFirestore(productId, productName, productCategory, productDescription, uri.toString())
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(context, "Failed to upload image: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            saveProductToFirestore(productId, productName, productCategory, productDescription, null)
        }
    }

    private fun saveProductToFirestore(productId: String, productName: String, productCategory: String, productDescription: String, imageUrl: String?) {
        val product = hashMapOf(
            "productId" to productId,
            "productName" to productName,
            "productCategory" to productCategory,
            "productDescription" to productDescription,
            "imageUrl" to imageUrl
        )

        firestore.collection("products").document(productId).set(product)
            .addOnSuccessListener {
                Toast.makeText(context, "Product added successfully", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to add product: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        etProductName.text?.clear()
        etProductDescription.text?.clear()
        spProductCategory.setSelection(0)
        ivProductImage.setImageResource(R.drawable.placeholder)
        imageUri = null
        etProductName.requestFocus()
    }
}
