package com.example.gadgetrate

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.gadgetrate.databinding.ActivityUploadImageBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class UploadImage : AppCompatActivity() {

    private lateinit var binding: ActivityUploadImageBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var imageUri: Uri? = null
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        userId = intent.getStringExtra("userId")

        binding.btnUploadImage.setOnClickListener {
            selectImage()
        }

        binding.btnSave.setOnClickListener {
            uploadImageToStorage()
        }
    }

    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            binding.ivProfileImage.setImageURI(it)
        }
    }

    private fun selectImage() {
        getImage.launch("image/*")
    }

    private fun uploadImageToStorage() {
        imageUri?.let {
            val storageRef = storage.reference.child("profile/${userId}.jpg")
            val uploadTask = storageRef.putFile(it)

            uploadTask.addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    saveImageUrlToFirestore(uri.toString())
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
            }
        } ?: Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
    }

    private fun saveImageUrlToFirestore(imageUrl: String) {
        userId?.let {
            val userRef = firestore.collection("users").document(it)
            userRef.update("profileImageUrl", imageUrl)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile image saved successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save profile image URL", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
