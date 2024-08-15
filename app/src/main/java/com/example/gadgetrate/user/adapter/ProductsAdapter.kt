package com.example.gadgetrate.user.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gadgetrate.R

import com.example.gadgetrate.databinding.ItemViewAllProductsBinding
import com.example.gadgetrate.user.model.Product

class ProductsAdapter(
    private val products: List<Product>,
    private val itemClickListener: (String) -> Unit
) : RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(private val binding: ItemViewAllProductsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.tvProductName.text = "Product Name: ${product.productName}"
            binding.tvProductDescription.text = "Description: ${product.productDescription}"
            binding.tvCategory.text = "Category: ${product.productCategory}"

            Glide.with(binding.root.context)
                .load(product.imageUrl)
                .placeholder(R.drawable.placeholder)
                .into(binding.ivProductImage)

            binding.root.setOnClickListener {
                itemClickListener(product.productId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemViewAllProductsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int {
        return products.size
    }
}
