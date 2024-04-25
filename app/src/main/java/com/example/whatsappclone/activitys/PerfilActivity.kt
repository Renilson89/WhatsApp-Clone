package com.example.whatsappclone.activitys



import android.net.Uri

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.example.whatsappclone.databinding.ActivityPerfilBinding
import com.example.whatsappclone.utils.exibirMensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class PerfilActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPerfilBinding.inflate(layoutInflater)
    }
    private val firebaseAut by lazy {
        FirebaseAuth.getInstance()
    }
    private val storage by lazy {
        FirebaseStorage.getInstance()
    }
    private val firebaseStorage by lazy {
        FirebaseFirestore.getInstance()
    }

    private var temPermissaoCamera = false
    private var temPermissaoGaleria = false
    //private var idUsuario: String? = null
    private val gerenciadorGaleria = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){uri ->
        if (uri != null){
            binding.imagePerfil.setImageURI(uri)
            uploadImagemStorage(uri)
        }else {
            exibirMensagem("Nenhuma Imagem Selecionada!")
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarToolbar()
        //solicitarPermissoes()
        inicializarEventosClique()
    }

    override fun onStart() {
        super.onStart()
        recuperarDadosIniciaisUsuarios()
    }

    private fun recuperarDadosIniciaisUsuarios() {
        val idUsuario = firebaseAut.currentUser?.uid
        if(idUsuario != null) {
            firebaseStorage.collection("usuarios")
                .document(idUsuario)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    val dadosUsuarios = documentSnapshot.data
                    if(dadosUsuarios != null){
                        val nome = dadosUsuarios["nome"] as String
                        val foto = dadosUsuarios["foto"] as String
                        binding.editPerfil.setText(nome)
                        if (foto.isNotEmpty()){
                            Picasso.get()
                                .load(foto)
                                .into(binding.imagePerfil)
                        }
                    }
                }
        }
    }


    private fun uploadImagemStorage(uri: Uri) {
        val idUsuario = firebaseAut.currentUser?.uid
        if (idUsuario != null){
            storage.getReference("fotos")
                .child("usuarios")
                .child(idUsuario)
                .child("perfil.jpg")
                .putFile(uri)
                .addOnSuccessListener {task ->
                    exibirMensagem("Sucesso ao fazer upload da imagem")
                    task.metadata?.reference?.downloadUrl
                        ?.addOnSuccessListener {uri ->
                            val dados = mapOf(
                                "foto" to uri.toString()
                            )
                            atualizarDadosPerfil(idUsuario, dados)
                        }
                }.addOnFailureListener{
                    exibirMensagem("Erro ao fazer upload da imagem")
                }
        }

    }

    private fun atualizarDadosPerfil(idUsuario: String, dados: Map<String, String>) {

        firebaseStorage.collection("usuarios")
            .document(idUsuario)
            .update(dados)
            .addOnSuccessListener {
                exibirMensagem("Sucesso ao Atualizar Perfil")
            }.addOnFailureListener {
                exibirMensagem("Erro ao Atualizar Perfil")
            }
    }

    private fun inicializarEventosClique() {
        binding.fabSelecionar.setOnClickListener{
            if (temPermissaoGaleria){
                gerenciadorGaleria.launch("image/*")
            }else{
             exibirMensagem("Não tem permissão")
                //solicitarPermissoes()
            }
        }
        binding.btnSalvar.setOnClickListener {
            val nomeUsuario = binding.editPerfil.text.toString()
            if (nomeUsuario.isNotEmpty()){
                val idUsuario = firebaseAut.currentUser?.uid
                if (idUsuario != null){
                    val dados = mapOf(
                        "nome" to nomeUsuario
                    )
                    atualizarDadosPerfil(idUsuario, dados)
                }
            }else {
                exibirMensagem("Preencha o nome para Atualizar")
            }
        }
    }

     /*private fun solicitarPermissoes() {
         temPermissaoCamera = ContextCompat.checkSelfPermission(
             this,
             Manifest.permission.CAMERA
         ) == PackageManager.PERMISSION_GRANTED

         temPermissaoGaleria = ContextCompat.checkSelfPermission(
             this,
             Manifest.permission.READ_MEDIA_IMAGES
         ) == PackageManager.PERMISSION_GRANTED

         val listaPermissoesNegadas = mutableListOf<String>()
         if(!temPermissaoCamera)
             listaPermissoesNegadas.add(Manifest.permission.CAMERA)
         if (!temPermissaoGaleria)
             listaPermissoesNegadas.add(Manifest.permission.READ_MEDIA_IMAGES)
            if( listaPermissoesNegadas.isNotEmpty()){
             val gerenciadorPermissoes = registerForActivityResult(
             ActivityResultContracts.RequestMultiplePermissions()
         ){permissoes ->
                 temPermissaoCamera = permissoes[Manifest.permission.CAMERA] ?: temPermissaoCamera
                 temPermissaoGaleria = permissoes[Manifest.permission.READ_MEDIA_IMAGES] ?: temPermissaoGaleria
         }
             gerenciadorPermissoes.launch(listaPermissoesNegadas.toTypedArray())
            }

     }*/

    private fun inicializarToolbar() {
        val toolbar = binding.includePerfil.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Editar Perfil"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}