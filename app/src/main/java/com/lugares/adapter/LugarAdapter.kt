package com.lugares.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lugares.databinding.FragmentAddLugarBinding
import com.lugares.databinding.LugarRowBinding
import com.lugares.model.Lugar

class LugarAdapter: RecyclerView.Adapter<LugarAdapter.LugarViewHolder>() {

    private var listLugares = emptyList<Lugar>()

    inner class LugarViewHolder(private val itemBinding: LugarRowBinding):
        RecyclerView.ViewHolder(itemBinding.root) {
            fun bind(lugar: Lugar) {
                itemBinding.tvPhone.text = lugar.phone
                itemBinding.tvEmail.text = lugar.email
                itemBinding.tvName.text = lugar.name
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LugarViewHolder {
        val itemBinding = LugarRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LugarViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: LugarViewHolder, position: Int) {
        val currentLugar = listLugares[position]
        holder.bind(currentLugar)
    }

    override fun getItemCount(): Int {
        return listLugares.size
    }

    fun setData(lugares: List<Lugar>) {
        this.listLugares = lugares
        notifyDataSetChanged()
    }
}