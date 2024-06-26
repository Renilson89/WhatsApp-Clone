package com.example.whatsappclone.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import com.example.whatsappclone.R
import com.example.whatsappclone.adaptes.viewPagerAdapter
import com.example.whatsappclone.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        inicializarToolbar()
        inicializarNavegaçãoAbas()
    }

    private fun inicializarNavegaçãoAbas() {
        val tabLayout = binding.tabLayyoutPrincipal
        val viewPager = binding.viewPagePrincipal
        val abas = listOf("CONVERSAS", "CONTATOS")

        viewPager.adapter = viewPagerAdapter(
            abas,supportFragmentManager, lifecycle
        )

        tabLayout.isTabIndicatorFullWidth = true
        TabLayoutMediator(tabLayout, viewPager){aba, posicao ->
            aba.text = abas[posicao]
        }.attach()
    }

    private fun inicializarToolbar() {
        val toolbar = binding.includeMainToolbar.tbPrincipal
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            title = "WhatsApp Clone"
        }
        addMenuProvider(
            object : MenuProvider{
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                   menuInflater.inflate(R.menu.menu_principal, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when(menuItem.itemId){
                        R.id.item_Perfil -> {
                            startActivity(Intent(
                                applicationContext, PerfilActivity::class.java
                            ))
                        }
                        R.id.item_sair -> {
                            deslogarUsuario()
                        }
                    }
                    return true
                }
            }
        )
    }

    private fun deslogarUsuario() {
        AlertDialog.Builder(this)
            .setMessage("Deslogar")
            .setMessage("Deseja realmente sair?")
            .setNegativeButton("Cancelar"){dialog, posicao ->}
            .setPositiveButton("sim"){dialog, posicao ->
                firebaseAuth.signOut()
                startActivity(Intent(
                    applicationContext, loginActivity::class.java
                ))
            }
            .create()
            .show()
    }
}