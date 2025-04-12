package com.bitnays.otobsngeldi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bitnays.otobsngeldi.databinding.RecylerRowBinding

class OtobusAdapter(val otobusListesi: ArrayList<OtoHatKonum>) : RecyclerView.Adapter<OtobusAdapter.ViewHolder>() {
    class ViewHolder(val binding: RecylerRowBinding ) : RecyclerView.ViewHolder(binding.root) {

    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecylerRowBinding.inflate(LayoutInflater.from(parent.context),parent as ViewGroup?,false)
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.KapiNo.text = otobusListesi.get(position).kapino.toString()
        holder.binding.bulunduguDurak.text = otobusListesi.get(position).yakinDurakKodu.toString()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int{
        return otobusListesi.size
    }

}