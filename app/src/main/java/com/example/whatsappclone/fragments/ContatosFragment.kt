package com.example.whatsappclone.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.R
import com.example.whatsappclone.activitys.MensagemActivity
import com.example.whatsappclone.adaptes.ContatosAdpter
import com.example.whatsappclone.databinding.FragmentContatosBinding
import com.example.whatsappclone.model.Usuario
import com.example.whatsappclone.utils.Constantes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration


class ContatosFragment : Fragment() {

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private lateinit var binding: FragmentContatosBinding
    private lateinit var eventoSnapshot: ListenerRegistration
    private lateinit var contatosAdapter: ContatosAdpter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContatosBinding.inflate(
            inflater, container, false
        )
        contatosAdapter = ContatosAdpter{usuario ->
            val intent = Intent(context, MensagemActivity::class.java)
            intent.putExtra("dadosDestinatario", usuario)
            intent.putExtra("origem", Constantes.ORIGEM_CONTATO)
            startActivity(intent)
        }
        binding.rvContatos.adapter = contatosAdapter
        binding.rvContatos.layoutManager = LinearLayoutManager(context)
        binding.rvContatos.addItemDecoration(
            DividerItemDecoration(
                context, LinearLayoutManager.VERTICAL
            )
        )
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adicionarListenerContatos()
    }

    private fun adicionarListenerContatos() {
         eventoSnapshot = firestore.collection(Constantes.USUARIOS)
            .addSnapshotListener { querySnaphot, erro ->
                val listaContatos = mutableListOf<Usuario>()
                val documento = querySnaphot?.documents
                documento?.forEach { documentSnapshot ->
                    val usuario = documentSnapshot.toObject(Usuario::class.java)
                    if (usuario != null) {
                       val idUsuarioLogado = firebaseAuth.currentUser?.uid
                        if (idUsuarioLogado != null){
                           if (idUsuarioLogado != usuario.id){
                               listaContatos.add(usuario)
                           }
                        }
                    }
                    if (listaContatos.isNotEmpty()) {
                        contatosAdapter.adicionarLista(listaContatos)
                    }
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        eventoSnapshot.remove()
    }


}