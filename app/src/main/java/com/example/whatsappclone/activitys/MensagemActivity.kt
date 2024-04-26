package com.example.whatsappclone.activitys

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsappclone.adaptes.ContatosAdpter
import com.example.whatsappclone.adaptes.ConversasAdpter
import com.example.whatsappclone.databinding.ActivityMensagemBinding
import com.example.whatsappclone.model.Mensagem
import com.example.whatsappclone.model.Usuario
import com.example.whatsappclone.utils.Constantes
import com.example.whatsappclone.utils.exibirMensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.squareup.picasso.Picasso

class MensagemActivity : AppCompatActivity() {

    private val fibaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val binding by lazy {
        ActivityMensagemBinding.inflate(layoutInflater)
    }

    private lateinit var coversasAdapter: ConversasAdpter
    private lateinit var listenerRegistration: ListenerRegistration
    private var dadosDestinatario: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        recuperarDadosUserDestinatario()
        inicializarToolbar()
        inicializarEventosClique()
        inicializarRecycleView()
        inicializarListener()
    }

    private fun inicializarRecycleView() {
        with(binding){
            coversasAdapter = ConversasAdpter()
            rvMensagem.adapter = coversasAdapter
            rvMensagem.layoutManager = LinearLayoutManager(applicationContext)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove()
    }

    private fun inicializarListener() {
        val idUsuarioRemetente = fibaseAuth.currentUser?.uid
        val idUsuarioDestinatario = dadosDestinatario?.id
        if (idUsuarioRemetente != null &&  idUsuarioDestinatario != null){
          listenerRegistration = firestore.collection(Constantes.BD_MENSAGENS)
                .document(idUsuarioRemetente)
                .collection(idUsuarioDestinatario)
                .orderBy("data", Query.Direction.ASCENDING)
                .addSnapshotListener{querySnapshot, erro ->
                    if(erro != null){
                        exibirMensagem("Erro ao Recupera Mensagem!")
                    }

                    val listaMensagens = mutableListOf<Mensagem>()
                    val documentos = querySnapshot?.documents
                    documentos?.forEach { documentSnapshot ->
                        val mensagem = documentSnapshot.toObject(Mensagem::class.java)
                        if (mensagem != null){
                            listaMensagens.add(mensagem)
                        }
                    }
                    if (listaMensagens.isNotEmpty()){
                            coversasAdapter.adicionarLista(listaMensagens)
                    }

                }
        }
    }

    private fun inicializarEventosClique() {
        binding.fabEnviar.setOnClickListener{
            val mensagem = binding.editMensagem.text.toString()
            salvarMensagem(mensagem)
        }
    }

    private fun salvarMensagem(textMensagem : String) {
        if (textMensagem.isNotEmpty()){
            val idUsuarioRemetente = fibaseAuth.currentUser?.uid
            val idUsuarioDestinatario = dadosDestinatario?.id
            if (idUsuarioRemetente != null &&  idUsuarioDestinatario != null){
                val mensagem = Mensagem(
                    idUsuarioRemetente , textMensagem
                )
                salvarMensagemFirestore(
                    idUsuarioRemetente, idUsuarioDestinatario, mensagem
                )
                salvarMensagemFirestore(
                     idUsuarioDestinatario,idUsuarioRemetente, mensagem
                )
                binding.editMensagem.setText("")
            }
        }
    }

    private fun salvarMensagemFirestore(
        idUsuarioRemetente: String, idUsuarioDestinatario: String, mensagem: Mensagem
    ) {
        firestore.collection(Constantes.BD_MENSAGENS)
            .document(idUsuarioRemetente)
            .collection(idUsuarioDestinatario)
            .add(mensagem)
            .addOnFailureListener {
                exibirMensagem("Erro ao Enviar a Mensagem!")
            }
    }

    private fun inicializarToolbar() {
        val toolbar = binding.tbMensagem
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = ""
            if (dadosDestinatario != null){
                binding.textNome.text = dadosDestinatario!!.nome
                Picasso.get()
                    .load(dadosDestinatario!!.foto)
                    .into(binding.imageFotoPerfil)
            }
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun recuperarDadosUserDestinatario() {
        val extras = intent.extras
        if (extras != null){
            val origem = extras.getString("origem")
            if (origem == Constantes.ORIGEM_CONTATO){
                dadosDestinatario = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras.getParcelable("dadosDestinatario", Usuario::class.java)
                }else{
                    extras.getParcelable("dadosDestinatario")
                }
            }else if(origem == Constantes.ORIGEM_CONVERSA){

            }
        }
    }
}