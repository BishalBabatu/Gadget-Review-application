package com.example.gadgetrate.user.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gadgetrate.R
import com.example.gadgetrate.databinding.ItemRecentlyReviewedBinding
import com.example.gadgetrate.user.model.LatestReviews

class LatestReviewAdapter : ListAdapter<LatestReviews, LatestReviewAdapter.LatestReviewViewHolder>(LatestReviewDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatestReviewViewHolder {
        val binding = ItemRecentlyReviewedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LatestReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LatestReviewViewHolder, position: Int) {
        val review = getItem(position)
        holder.bind(review)
    }

    class LatestReviewViewHolder(private val binding: ItemRecentlyReviewedBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(review: LatestReviews) {
            binding.tvProductName.text = review.productName
            binding.tvCategory.text = "Category: ${review.productCategory}"
            binding.tvDescription.text = "Description: ${review.productDescription}"
            binding.tvReviewedBy.text = "Reviewed By: ${review.userName}"
            binding.tvReviewDate.text = "Date: ${review.reviewDate}"
            binding.tvUserReview.text = "Review: ${review.productReview}"

            Glide.with(binding.ivProductImage.context)
                .load(review.productImageUrl)
                .placeholder(R.drawable.placeholder)
                .into(binding.ivProductImage)
        }
    }

    class LatestReviewDiffCallback : DiffUtil.ItemCallback<LatestReviews>() {
        override fun areItemsTheSame(oldItem: LatestReviews, newItem: LatestReviews): Boolean {
            return oldItem.reviewNo == newItem.reviewNo
        }

        override fun areContentsTheSame(oldItem: LatestReviews, newItem: LatestReviews): Boolean {
            return oldItem == newItem
        }
    }
}
