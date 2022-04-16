package com.lugares

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.lugares.databinding.ActivityMainBinding
import java.security.Principal

class MainActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth

        binding.btnLogin.setOnClickListener{
            login();
        }

        binding.btnRegister.setOnClickListener{
            register();
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id_r))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btGoogle.setOnClickListener{
            googleSignIn()
        }
    }

    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        update(user)
                    } else {
                        update(null)
                    }
            }

    }

    private fun register() {
        val email = binding.txtEmail.text.toString()
        val password = binding.txtPassword.text.toString()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                task->
                if (task.isSuccessful) {
                    Log.d("Creating user", "Registered")
                    val user = auth.currentUser
                    update(user)
                } else {
                    Log.d("Creating user", "Failed")
                    Toast.makeText(baseContext, "Failed", Toast.LENGTH_LONG).show()
                    update(null)
                }
            }
    }


    private fun login() {
        val email = binding.txtEmail.text.toString()
        val password = binding.txtPassword.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                    task->
                if (task.isSuccessful) {
                    Log.d("Sign in user", "Authenticated")
                    val user = auth.currentUser
                    update(user)
                } else {
                    Log.d("Sign in user", "Failed")
                    Toast.makeText(baseContext, "Failed", Toast.LENGTH_LONG).show()
                    update(null)
                }
            }
    }

    private fun update(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, Main::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {

            }
        }
    }

    public override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        update(user)
    }
}