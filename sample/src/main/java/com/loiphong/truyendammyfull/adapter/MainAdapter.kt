package com.loiphong.truyendammyfull.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import com.loiphong.truyendammyfull.R
import com.loiphong.truyendammyfull.entity.CategoryEntity

class MainAdapter(private val itemClick: (CategoryEntity) -> Unit) :
    RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    private var dataList: ArrayList<CategoryEntity> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_main, parent, false)
        return ViewHolder(view, itemClick)
    }

    override fun getItemCount(): Int = dataList.count()

    override fun onBindViewHolder(holder: MainAdapter.ViewHolder, position: Int) =
        holder.bindItemView()

    fun submitList(list: ArrayList<CategoryEntity>) {
        dataList = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View, val itemClick: (CategoryEntity) -> Unit) :
        RecyclerView.ViewHolder(itemView) {
        private val imgMain: CircleImageView = itemView.findViewById(R.id.imgMain)
        private val tvMain: TextView = itemView.findViewById(R.id.tvMain)

        fun bindItemView() {
            Glide.with(itemView.context).load(dataList[adapterPosition].avatar).into(imgMain)
            tvMain.text = dataList[adapterPosition].title
            itemView.setOnClickListener { itemClick(dataList[adapterPosition]) }
        }
    }
}