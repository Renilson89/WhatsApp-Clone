package com.example.whatsappclone.adaptes


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.whatsappclone.databinding.ItemContatosBinding
import com.example.whatsappclone.databinding.ItemMensagensDestinatarioBinding
import com.example.whatsappclone.databinding.ItemMensagensRemetenteBinding
import com.example.whatsappclone.model.Mensagem
import com.example.whatsappclone.model.Usuario
import com.example.whatsappclone.utils.Constantes
import com.google.firebase.auth.FirebaseAuth


class ConversasAdpter: Adapter<ViewHolder>() {

    private var listaMensagens = emptyList<Mensagem>()
    fun adicionarLista(lista: List<Mensagem>){
        listaMensagens = lista
        notifyDataSetChanged()
    }


    class MensagensRemetenteViewHolder(
        private val binding : ItemMensagensRemetenteBinding
    ) : ViewHolder(binding.root){
        fun bind(mensagem: Mensagem){
            binding.textMensgaemRemetente.text = mensagem.mensagem
        }
        companion object{
            fun inflaterLayout(parent: ViewGroup): MensagensRemetenteViewHolder{
                val inflate = LayoutInflater.from(parent.context)
                val itemView = ItemMensagensRemetenteBinding.inflate(
                    inflate, parent,false
                )
                return MensagensRemetenteViewHolder( itemView)
            }
        }
    }

    class MensagensDestinatarioViewHolder(
        private val binding : ItemMensagensDestinatarioBinding
    ): ViewHolder(binding.root){
        fun bind(mensagem: Mensagem){
            binding.textMensagemDestinatario.text = mensagem.mensagem
        }

        companion object{
            fun inflaterLayout(parent: ViewGroup): MensagensDestinatarioViewHolder{
                val inflate = LayoutInflater.from(parent.context)
                val itemView = ItemMensagensDestinatarioBinding.inflate(
                    inflate, parent,false
                )
                return MensagensDestinatarioViewHolder( itemView)
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        val mensagem = listaMensagens[position]
        val idUsurrio = FirebaseAuth.getInstance().currentUser?.uid.toString()
        return if (idUsurrio == mensagem.idUsario){
            Constantes.TIPO_REMETENTE
        }else{
            Constantes.TIPO_DESTINATARIO
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        if (viewType == Constantes.TIPO_REMETENTE)
            return MensagensRemetenteViewHolder.inflaterLayout(parent)

            return MensagensDestinatarioViewHolder.inflaterLayout(parent)

    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mensagem = listaMensagens[position]
        when(holder){
            is MensagensRemetenteViewHolder -> holder.bind(mensagem)
            is MensagensDestinatarioViewHolder -> holder.bind(mensagem)
        }

    }

    override fun getItemCount(): Int {
        return listaMensagens.size
    }
}