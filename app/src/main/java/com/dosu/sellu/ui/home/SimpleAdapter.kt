package com.dosu.sellu.ui.home

import com.dosu.sellu.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView


class SimpleAdapter(private val context: Context, private var data: MutableList<String>) :
    RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.simple_item, parent, false)
        return SimpleViewHolder(view)
    }


    fun add(s: String, position: Int) {
        var p = position
        p = if (p == -1) itemCount else p
        data.add(p, s)
        notifyItemInserted(p)
    }

    fun remove(position: Int) {
        if (position < itemCount) {
            data.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    class SimpleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.simple_text)
    }

    override fun onBindViewHolder(holder: SimpleViewHolder, position: Int) {
        holder.title.text = data[position]
        holder.title.setOnClickListener{
                Toast.makeText(context, "Position =$position", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}