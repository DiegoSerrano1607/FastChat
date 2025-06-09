package com.Developers.fastchat.Perfil

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.Developers.fastchat.Modelo.Usuario
import com.Developers.fastchat.R
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PerfilVisitado : AppCompatActivity() {

    private lateinit var PV_ImagenU : ImageView

    private lateinit var PV_NombreU : TextView
    private lateinit var PV_EmailU : TextView
    private lateinit var PV_Uid : TextView
    private lateinit var PV_nombres : TextView
    private lateinit var PV_apellidos : TextView
    private lateinit var PV_telefono : TextView
    private lateinit var PV_proveedor : TextView

    var uid_usuario_visitado = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_visitado)
        InicializarVistas()
        ObtenerUid()
        LeerInformacionUsuario()
    }

    private fun InicializarVistas(){

        PV_ImagenU = findViewById(R.id.PV_ImagenU)

        PV_NombreU = findViewById(R.id.PV_NombreU)
        PV_EmailU = findViewById(R.id.PV_EmailU)
        PV_Uid = findViewById(R.id.PV_Uid)
        PV_nombres = findViewById(R.id.PV_nombres)
        PV_apellidos = findViewById(R.id.PV_apellidos)
        PV_telefono = findViewById(R.id.PV_telefono)
        PV_proveedor = findViewById(R.id.PV_proveedor)

    }

    private fun ObtenerUid(){
        intent = intent
        uid_usuario_visitado = intent.getStringExtra("uid").toString()
    }

    private fun LeerInformacionUsuario(){
        val reference = FirebaseDatabase.getInstance().reference
            .child("Usuarios")
            .child(uid_usuario_visitado)

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val usuario : Usuario? = snapshot.getValue(Usuario::class.java)
                //Obtener la información en tiempo real
                PV_NombreU.text = usuario!!.getN_Usuario()
                PV_EmailU.text = usuario!!.getEmail()
                PV_Uid.text = usuario!!.getUid()
                PV_nombres.text = usuario!!.getNombres()
                PV_apellidos.text = usuario!!.getApellidos()
                PV_telefono.text = usuario!!.getTelefono()
                PV_proveedor.text = usuario!!.getProveedor()

                Glide.with(applicationContext).load(usuario.getImagen())
                    .placeholder(R.drawable.imagen_usuario_visitado)
                    .into(PV_ImagenU)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }













}