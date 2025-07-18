package com.bitnays.otobsngeldi

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bitnays.otobsngeldi.databinding.RecylerRow2Binding
import androidx.core.graphics.toColorInt
import com.bitnays.otobsngeldi.model.Durak

class DurakAdapter(val durakListesi: ArrayList<Durak>, val enYakinDUrak: Durak?) : RecyclerView.Adapter<DurakAdapter.ViewHolder>() {
    var rowColor = 0
    class ViewHolder(val binding: RecylerRow2Binding ) : RecyclerView.ViewHolder(binding.root) {

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecylerRow2Binding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)

    }//FFFFFF4D
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (rowColor==0){
            holder.binding.linearLay.setBackgroundColor("#1A1A1B".toColorInt())
            rowColor = 1
        }
        else{
            holder.binding.linearLay.setBackgroundColor("#23262A".toColorInt())
            rowColor = 0
        }
        val drawableImage = when(durakListesi.get(position).YON.toString()){
            "G" -> R.drawable.gidis
            "D" -> R.drawable.donus
            else -> R.drawable.ic_launcher_foreground
        }
        //println(enYakinDUrak?.DURAKADI+" "+durakListesi.get(position).DURAKADI)
        if (enYakinDUrak?.DURAKADI == durakListesi.get(position).DURAKADI)
        {//#4CAF50
            holder.binding.yolcuYakinDurak.text = "Size en yakın durak"
            holder.binding.yolcuYakinDurak.setTextColor("#4CAF50".toColorInt())
        }
        else{
            holder.binding.yolcuYakinDurak.text = ""
        }
        holder.binding.DURAKADI.text = durakListesi.get(position).DURAKADI
        holder.binding.YON.text = durakListesi.get(position).YON
        holder.binding.siraNo.text = durakListesi.get(position).SIRANO
        if (durakListesi.get(position).otobusVar == true)
        {
            holder.binding.otobusImageView.setImageResource(drawableImage)
            holder.binding.otobusImageView.visibility = View.VISIBLE
        }
        else {
            holder.binding.otobusImageView.visibility = View.GONE
        }
        holder.binding.linearLay.setOnClickListener {

            Toast.makeText(holder.itemView.context,"Hedef Durak: "+durakListesi.get(position).DURAKADI, Toast.LENGTH_SHORT).show()

        }
    }
    override fun getItemCount(): Int{
        return durakListesi.size
    }

}