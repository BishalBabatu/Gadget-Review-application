package com.example.gadgetrate.user.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gadgetrate.admin.model.Review
import com.example.gadgetrate.databinding.ItemReviewBySingleUserBinding


class SingleUserReviewAdapter(
    private val reviews: List<Review>
) : RecyclerView.Adapter<SingleUserReviewAdapter.SingleUserReviewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleUserReviewViewHolder {
        val binding = ItemReviewBySingleUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SingleUserReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SingleUserReviewViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun getItemCount() = reviews.size

    class SingleUserReviewViewHolder(private val binding: ItemReviewBySingleUserBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review) {
            binding.tvProductName.text = review.productName
            binding.tvProductCategory.text = review.productCategory
            binding.tvReview.text = review.productReview
            binding.tvRating.text = review.productRating.toString()
            binding.tvReviewDate.text = review.reviewDate
        }
    }
}
