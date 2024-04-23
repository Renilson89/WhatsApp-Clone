package com.example.whatsappclone

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.widget.Toast
import com.example.whatsappclone.databinding.ActivityCadastroBinding
import com.example.whatsappclone.utils.exibirMensagem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWebException

class CadastroActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCadastroBinding.inflate(layoutInflater)
    }
    private lateinit var nome: String
    private lateinit var email: String
    private lateinit var senha: String
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarToolbar()
        inicializarEventoClique()
    }

    private fun inicializarEventoClique() {
        binding.btnCadastrar.setOnClickListener {
            if (validarCampos()){
                cadastrarUsuario(nome, email, senha)
            }
        }
    }

    private fun cadastrarUsuario(nome: String, email: String, senha: String) {
            firebaseAuth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener { resultado ->
               if (resultado.isSuccessful) {
                    exibirMensagem("Sucesso ao fazer seu cadastro")
                   startActivity(Intent(applicationContext, MainActivity::class.java))
               }
            }.addOnFailureListener{ erro ->
                try {
                    throw erro
                }catch (erroSenhaFraca: FirebaseAuthWebException){
                    erroSenhaFraca.printStackTrace()
                    exibirMensagem("Senha fraca, digite outra senha forte!!")
                }catch (erroUsuarioExistente: FirebaseAuthUserCollisionException){
                    erroUsuarioExistente.printStackTrace()
                    exibirMensagem("Email ja pertence a outro usuario!!")
                }catch (erroCredenciaisInvalidas: FirebaseAuthInvalidCredentialsException){
                    erroCredenciaisInvalidas.printStackTrace()
                    exibirMensagem("Email Invalido!!")
                }

            }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun validarCampos(): Boolean {
        nome = binding.editNome.text.toString()
        email = binding.editEmail.text.toString()
        senha = binding.editSenha.text.toString()

            if (nome.isNotEmpty()){
                binding.textInputLayoutnome.error = null
                if (email.isNotEmpty()){
                    binding.textInputLayoutEmail.error = null
                    if (senha.isNotEmpty()){
                        binding.textInputLayoutSenha.error = null
                        return true
                    }else{
                        binding.textInputLayoutSenha.error = "Preencha a Senha!"
                        return false
                    }
                }else{
                    binding.textInputLayoutEmail.error = "Preencha o seu email!"
                    return false
                }
            }else{
                binding.textInputLayoutnome.error = "Preencha o seu nome!"
                return false
            }
    }

    private fun inicializarToolbar() {
        val toolbar = binding.IncludeToolbar.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "Faça o seu cadastro"
            setDisplayHomeAsUpEnabled(true)
        }
    }
}