package com.example.whatsappclone.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.activitys.MensagemActivity
import com.example.whatsappclone.adaptes.ContatosAdpter
import com.example.whatsappclone.adaptes.ConversasAdapter
import com.example.whatsappclone.databinding.FragmentConversasBinding
import com.example.whatsappclone.model.Conversa
import com.example.whatsappclone.model.Usuario
import com.example.whatsappclone.utils.Constantes
import com.example.whatsappclone.utils.exibirMensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query


class ConversasFragment : Fragment() {

    private lateinit var binding: FragmentConversasBinding
    private lateinit var eventosSnapshot: ListenerRegistration
    private lateinit var conversasAdapter: ConversasAdapter
    private val firebaseAut by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentConversasBinding.inflate(
           inflater, container, false
       )
        conversasAdapter = ConversasAdapter { conversa ->
            val intent = Intent(context, MensagemActivity::class.java)
            val usuario = Usuario(
                id = conversa.idUsuarioDestinatario,
                nome = conversa.nome,
                foto = conversa.foto
            )
            intent.putExtra("dadosDestinatario", usuario)
            intent.putExtra("origem", Constantes.ORIGEM_CONVERSA)
            startActivity(intent)

        }
        binding.rvConversas.adapter = conversasAdapter
        binding.rvConversas.layoutManager = LinearLayoutManager(context)
        binding.rvConversas.addItemDecoration(
            DividerItemDecoration(
                context, LinearLayoutManager.VERTICAL
            )
        )
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adicionarListenerConversas()
    }

    override fun onDestroy() {
        super.onDestroy()
        eventosSnapshot.remove()
    }

    private fun adicionarListenerConversas() {
       val idUsuarioRemetente = firebaseAut.currentUser?.uid
       if (idUsuarioRemetente != null) {
           eventosSnapshot = firestore
               .collection(Constantes.CONVERSAS)
               .document(idUsuarioRemetente)
               .collection(Constantes.ULTIMAS_CONVERSAS)
               .orderBy("data", Query.Direction.DESCENDING)
               .addSnapshotListener{ querySnapshot, erro ->
                   if (erro != null){
                       activity?.exibirMensagem("Erro ao recuperar a Conversas")
                   }
                   val listaConversas = mutableListOf<Conversa>()
                   val documentos = querySnapshot?.documents
                   documentos?.forEach { documentSnaphot ->
                       val conversa = documentSnaphot.toObject(Conversa::class.java)
                       if(conversa != null){
                           listaConversas.add(conversa)
                       }
                   }
                   if(listaConversas.isNotEmpty()){
                       conversasAdapter.adicionarLista(listaConversas)

                   }

               }
       }
    }


}