package com.Developers.fastchat

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.Developers.fastchat.Fragmentos.FragmentoChats
import com.Developers.fastchat.Fragmentos.FragmentoUsuarios
import com.Developers.fastchat.Modelo.Chat
import com.Developers.fastchat.Modelo.Usuario
import com.Developers.fastchat.Perfil.PerfilActivity
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    var reference : DatabaseReference?=null
    var firebaseUser : FirebaseUser?=null
    private lateinit var nombre_usuario : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        InicializarComponentes()
        ObtenerDatos()
    }

    fun InicializarComponentes(){

        val toolbar : Toolbar = findViewById(R.id.toolbarMain)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().reference.child("Usuarios").child(firebaseUser!!.uid)
        nombre_usuario = findViewById(R.id.Nombre_usuario)

        val tabLayout : TabLayout = findViewById(R.id.TabLayoutMain)
        val viewPager : ViewPager = findViewById(R.id.ViewPagerMain)

        /*val viewpagerAdapter = ViewPagerAdapter(supportFragmentManager)

        viewpagerAdapter.addItem(FragmentoUsuarios(), "Usuarios")
        viewpagerAdapter.addItem(FragmentoChats(), "Chats")

        viewPager.adapter = viewpagerAdapter
        tabLayout.setupWithViewPager(viewPager)*/

        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
                var contMensajesNoLeidos = 0
                for (dataSnapshot in snapshot.children){
                    val chat = dataSnapshot.getValue(Chat::class.java)
                    if (chat!!.getReceptor().equals(firebaseUser!!.uid) && !chat.isVisto()){
                        contMensajesNoLeidos += 1
                    }
                }
                if(contMensajesNoLeidos == 0){
                    viewPagerAdapter.addItem(FragmentoChats(), "Chats")
                }
                else{
                    viewPagerAdapter.addItem(FragmentoChats(), "[$contMensajesNoLeidos] Chats")
                }
                viewPagerAdapter.addItem(FragmentoUsuarios(), "Usuarios")
                viewPager.adapter = viewPagerAdapter
                tabLayout.setupWithViewPager(viewPager)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun ObtenerDatos(){
        reference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val usuario : Usuario? = snapshot.getValue(Usuario::class.java)
                    nombre_usuario.text = usuario!!.getN_Usuario()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    class ViewPagerAdapter(fragmentManager : FragmentManager):FragmentPagerAdapter(fragmentManager) {

        private val listaFragmentos : MutableList<Fragment> = ArrayList()
        private val listaTitulos : MutableList<String> = ArrayList()

        override fun getCount(): Int {
            return listaFragmentos.size
        }

        override fun getItem(position: Int): Fragment {
            return listaFragmentos[position]
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return listaTitulos[position]
        }

        fun addItem(fragment: Fragment, titulo:String){
            listaFragmentos.add(fragment)
            listaTitulos.add(titulo)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater : MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.menu_perfil -> {
                val intent = Intent(applicationContext, PerfilActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.menu_acerca_de->{
                InfoApp()
                return true
            }
            R.id.menu_salir -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this@MainActivity, Inicio::class.java)
                Toast.makeText(applicationContext, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                startActivity(intent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun InfoApp(){
        val EntendidoInfo : Button
        val dialog = Dialog(this@MainActivity)
        dialog.setContentView(R.layout.cuadro_d_info_app)
        EntendidoInfo = dialog.findViewById(R.id.EntendidoInfo)
        EntendidoInfo.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
        dialog.setCanceledOnTouchOutside(false)

    }
}