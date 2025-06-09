package com.Developers.fastchat.Perfil

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.Developers.fastchat.Modelo.Usuario
import com.Developers.fastchat.R
import com.Developers.fastchat.R.*
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PerfilActivity : AppCompatActivity() {

    private lateinit var P_imagen : ImageView
    private lateinit var P_n_usuario : TextView
    private lateinit var P_email : TextView
    private lateinit var P_proveedor : TextView
    private lateinit var P_nombres : EditText
    private lateinit var P_apellidos : EditText
    private lateinit var P_edad : EditText
    private lateinit var P_telefono : EditText
    private lateinit var Btn_guardar : Button
    private lateinit var Editar_imagen : ImageView

    private lateinit var Btn_verificar : MaterialButton

    var user : FirebaseUser?=null
    var reference : DatabaseReference?=null

    private lateinit var progressDialog : ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_perfil)
        InicializarVariables()
        ObtenerDatos()
        EstadoCuenta()
        Btn_guardar.setOnClickListener {
            ActualizarInformacion()
        }

        Editar_imagen.setOnClickListener {
            val intent = Intent(applicationContext, EditarImagenPerfil::class.java)
            startActivity(intent)
        }

        Btn_verificar.setOnClickListener {
            if (user!!.isEmailVerified){
                //El usuario ya verifico su correo
                //Toast.makeText(applicationContext, "Usuario verificado", Toast.LENGTH_SHORT).show()
                CuentaVerificada()
            }else{
                //El usuario no verifico su correo
                ConfirmarEnvio()
            }

        }
    }

    private fun ConfirmarEnvio() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Verificar cuenta")
            .setMessage("¿Desea enviar un correo de verificación a su cuenta? ${user!!.email}")
            .setPositiveButton("Enviar"){d,e ->
                EnviarEmailConfirmacion()

            }
            .setNegativeButton("Cancelar"){d,e ->
                d.dismiss()
            }
            .show()
    }

    private fun EnviarEmailConfirmacion() {
        progressDialog.setMessage("Enviando correo de verificación a ${user!!.email}")
        progressDialog.show()

        user!!.sendEmailVerification()
            .addOnSuccessListener {
            //Correo enviado
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Correo enviado, revise su bandeja de entrada ${user!!.email}", Toast.LENGTH_SHORT).show()

             }
            .addOnFailureListener { e->
                //Envio fallido
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "La operación falló debido a ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }

    private fun EstadoCuenta(){
        if (user!!.isEmailVerified){
            Btn_verificar.text = "Verificado"
        }else{
            Btn_verificar.text = "No verificado"
        }
    }

    private fun InicializarVariables(){
        P_imagen = findViewById(R.id.P_imagen)
        P_n_usuario = findViewById(R.id.P_n_usuario)
        P_proveedor = findViewById(R.id.P_proveedor)
        P_email = findViewById(R.id.P_email)
        P_nombres = findViewById(R.id.P_nombres)
        P_apellidos = findViewById(R.id.P_apellidos)
        P_edad = findViewById(R.id.P_edad)
        P_telefono = findViewById(R.id.P_telefono)
        Btn_guardar = findViewById(R.id.Btn_guardar)
        Editar_imagen = findViewById(R.id.Editar_imagen)
        Btn_verificar = findViewById(R.id.Btn_verificar)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        user = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().reference.child("Usuarios").child(user!!.uid)


    }

    private fun ObtenerDatos(){
        reference?.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    //Obtener datos de nuestra base de datos
                    val usuario : Usuario?= snapshot.getValue(Usuario::class.java)
                    val str_n_usuario = usuario!!.getN_Usuario()
                    val str_email = usuario.getEmail()
                    val str_proveedor = usuario.getProveedor()
                    val str_nombres = usuario.getNombres()
                    val str_apellidos = usuario.getApellidos()
                    val str_edad = usuario.getEdad()
                    val str_telefono = usuario.getTelefono()

                    //Se setea la informacion en nuestras vistas de la app
                    P_n_usuario.text = str_n_usuario
                    P_email.text = str_email
                    P_proveedor.text = str_proveedor
                    P_nombres.setText(str_nombres)
                    P_apellidos.setText(str_apellidos)
                    P_edad.setText(str_edad)
                    P_telefono.setText(str_telefono)
                    Glide.with(applicationContext).load(usuario.getImagen()).placeholder(R.drawable.ic_item_usuario).into(P_imagen)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun ActualizarInformacion(){
        val str_nombres = P_nombres.text.toString()
        val str_apellidos = P_apellidos.text.toString()
        val str_edad = P_edad.text.toString()
        val str_telefono = P_telefono.text.toString()

        val hashmap = HashMap<String, Any>()
        hashmap["nombres"] = str_nombres
        hashmap["apellidos"] = str_apellidos
        hashmap["edad"] = str_edad
        hashmap["telefono"] = str_telefono

        reference!!.updateChildren(hashmap).addOnCompleteListener{task->
            if(task.isSuccessful){
                Toast.makeText(applicationContext, "Se han actualizado los datos", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(applicationContext, "No se han actualizado los datos", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener{e->
            Toast.makeText(applicationContext, "Ha ocurrido un error ${e.message}", Toast.LENGTH_SHORT).show()
        }

    }

    private fun CuentaVerificada(){
        val BtnEntendidoVerificado : MaterialButton
        val dialog = Dialog(this@PerfilActivity)

        dialog.setContentView(R.layout.cuadro_d_cuenta_verificada)

        BtnEntendidoVerificado = dialog.findViewById(R.id.BtnEntendidoVerificado)
        BtnEntendidoVerificado.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.setCanceledOnTouchOutside(false)
    }


}