package com.bitnays.otobsngeldi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitnays.otobsngeldi.databinding.RecylerRow2Binding

class DurakAdapter(val durakListesi: ArrayList<Durak>) : RecyclerView.Adapter<DurakAdapter.ViewHolder>() {
    class ViewHolder(val binding: RecylerRow2Binding ) : RecyclerView.ViewHolder(binding.root) {

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecylerRow2Binding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.DURAKADI.text = durakListesi.get(position).DURAKADI
        holder.binding.YON.text = durakListesi.get(position).YON
        holder.binding.siraNo.text = durakListesi.get(position).SIRANO
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int{
        return durakListesi.size
    }

}