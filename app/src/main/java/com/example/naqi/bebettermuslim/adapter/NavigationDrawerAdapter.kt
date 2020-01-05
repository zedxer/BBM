package com.example.naqi.bebettermuslim.adapter

import android.content.res.TypedArray
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.naqi.bebettermuslim.R

class NavAdapter(
    private val dataset: Array<String>, private val drawables: TypedArray,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<NavAdapter.NavItemViewHolder>() {

    interface OnItemClickListener {
        fun onNavItemClick(view: View, position: Int)
    }

    class NavItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconView = itemView.findViewById(R.id.iconView) as ImageView
        val textView = itemView.findViewById(R.id.textView) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavItemViewHolder {
        val layoutView = LayoutInflater.from(parent.context).inflate(
            R.layout.nav_list_adapter, parent, false
        )
        return NavItemViewHolder(layoutView)
    }

    override fun onBindViewHolder(holder: NavItemViewHolder, position: Int) {
        holder.iconView.setImageResource(drawables.getResourceId(position, -1))
        holder.textView.text = dataset[position]
        holder.textView.setOnClickListener { view ->
            listener.onNavItemClick(view, position)
        }
    }

    override fun getItemCount() = dataset.size
}