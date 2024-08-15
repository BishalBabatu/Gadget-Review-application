package com.example.gadgetrate.user.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gadgetrate.R
import com.example.gadgetrate.databinding.ItemTopRatedProductsBinding
import com.example.gadgetrate.user.model.TopRatedProduct

class TopRatedProductsAdapter : ListAdapter<TopRatedProduct, TopRatedProductsAdapter.TopRatedProductViewHolder>(TopRatedProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopRatedProductViewHolder {
        val binding = ItemTopRatedProductsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TopRatedProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopRatedProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
    }

    class TopRatedProductViewHolder(private val binding: ItemTopRatedProductsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: TopRatedProduct) {
            binding.tvProductName.text = product.productName
            binding.tvCategory.text = "Category: ${product.productCategory}"
            binding.tvDescription.text = "Description: ${product.productDescription}"
            binding.tvRating.text = "Rating: ${product.averageRating}"

            Glide.with(binding.ivProductImage.context)
                .load(product.productImageUrl)
                .placeholder(R.drawable.placeholder)
                .into(binding.ivProductImage)
        }
    }

    class TopRatedProductDiffCallback : DiffUtil.ItemCallback<TopRatedProduct>() {
        override fun areItemsTheSame(oldItem: TopRatedProduct, newItem: TopRatedProduct): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(oldItem: TopRatedProduct, newItem: TopRatedProduct): Boolean {
            return oldItem == newItem
        }
    }
}
