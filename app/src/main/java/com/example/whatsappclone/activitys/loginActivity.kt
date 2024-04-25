package com.example.whatsappclone.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.whatsappclone.databinding.ActivityLoginBinding
import com.example.whatsappclone.utils.exibirMensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class loginActivity : AppCompatActivity() {
    private lateinit var email: String
    private lateinit var senha: String
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarEventosClique()
        firebaseAuth.signOut()
    }

    override fun onStart() {
        super.onStart()
        verificarUsuarioLogado()
    }

    private fun verificarUsuarioLogado() {
        val usuarioAtual = firebaseAuth.currentUser
        if (usuarioAtual != null){
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private fun inicializarEventosClique() {
        binding.textCadastro.setOnClickListener {
            startActivity(
                Intent(this, CadastroActivity::class.java)
            )
        }
        binding.btnEntrar.setOnClickListener {
            if (validarCampos()){
                logarUsuario()
            }
        }
    }

    private fun logarUsuario() {
        firebaseAuth.signInWithEmailAndPassword(
            email, senha
        ).addOnSuccessListener {
            exibirMensagem("Logado com sucesso")
            startActivity(Intent(this, MainActivity::class.java))
        }.addOnFailureListener { erro ->
            try {
                throw erro
            }catch (erroUsuarioInvalido: FirebaseAuthInvalidUserException){
                erroUsuarioInvalido.printStackTrace()
                exibirMensagem("Email não cadastrado!!")
        }catch (erroCredencias: FirebaseAuthInvalidCredentialsException){
                erroCredencias.printStackTrace()
                exibirMensagem("Email ou senha Invalidos!!")
            }
    }
    }

    private fun validarCampos(): Boolean {
        email = binding.editLoginEmail.text.toString()
        senha = binding.editSenhaLogin.text.toString()
        if (email.isNotEmpty()){
            binding.textInputLayoutLoginEmail.error = null
            if (senha.isNotEmpty()){
                binding.textInputLayoutSenhaLogin.error = null
                return true
            }else{
                binding.textInputLayoutSenhaLogin.error = "Preencha senha"
                return false
            }
        }else{
            binding.textInputLayoutLoginEmail.error = "Preencha o e-mail"
            return false
        }
    }
}