package com.example.whatsappclone.activitys

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.whatsappclone.R
import com.example.whatsappclone.databinding.ActivityMensagemBinding
import com.example.whatsappclone.model.Usuario
import com.example.whatsappclone.utils.Constantes

class MensagemActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMensagemBinding.inflate(layoutInflater)
    }
    private var dadosDestinatario: Usuario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        recuperarDadosUserDestinatario()
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