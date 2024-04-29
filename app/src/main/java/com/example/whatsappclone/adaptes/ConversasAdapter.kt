package com.example.whatsappclone.adaptes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.whatsappclone.databinding.ItemContatosBinding
import com.example.whatsappclone.databinding.ItemConversaBinding
import com.example.whatsappclone.model.Conversa
import com.example.whatsappclone.model.Usuario
import com.squareup.picasso.Picasso

class ConversasAdapter (
    private val onClick: (Conversa) ->Unit
): Adapter<ConversasAdapter.ConversasViewHolder>(){
    private var listaConversa = emptyList<Conversa>()
    fun adicionarLista(lista: List<Conversa>){
        listaConversa = lista
        notifyDataSetChanged()
    }
    inner class ConversasViewHolder(
        private val binding: ItemConversaBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(conversa: Conversa){
            binding.textConversaNome.text = conversa.nome
            binding.textUltimaConverasa.text = conversa.ultimaMensagem
            Picasso.get()
                .load(conversa.foto)
                .into(binding.imageConversaFoto)
            binding.clItemConversa.setOnClickListener{
                onClick(conversa)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversasViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val itemView = ItemConversaBinding.inflate(
            inflate, parent,false
        )
        return ConversasViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ConversasViewHolder, position: Int) {
        val conversa = listaConversa[position]
        holder.bind(conversa)
    }

    override fun getItemCount(): Int {
       return listaConversa.size
    }
}


