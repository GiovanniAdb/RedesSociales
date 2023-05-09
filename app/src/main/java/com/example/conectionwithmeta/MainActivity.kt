package com.example.conectionwithmeta
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import org.json.JSONObject
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.Request.Method.POST
import com.android.volley.RequestQueue

class MainActivity : AppCompatActivity() {

    private lateinit var loginButton: LoginButton
    private lateinit var callbackManager: CallbackManager
    private lateinit var accessTokenTracker: AccessTokenTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar el callback manager
        callbackManager = CallbackManager.Factory.create()

        // Asignar el botón de inicio de sesión
        loginButton = findViewById(R.id.login_button)
        loginButton.setPermissions(listOf("public_profile", "email"))

        // Registrar el callback del botón de inicio de sesión
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                // El usuario ha iniciado sesión correctamente
                val accessToken = loginResult.accessToken
                Log.d(TAG, "AccessToken: $accessToken")

                // Publicar una publicación en el muro de Facebook
                publishPost()
            }

            override fun onCancel() {
                // El usuario ha cancelado el inicio de sesión
                Log.d(TAG, "Login canceled")
            }

            override fun onError(error: FacebookException) {
                // Ha ocurrido un error durante el inicio de sesión
                Log.e(TAG, "Login error: ${error.message}")
            }
        })

        // Configurar el tracker para manejar los cambios en el token de acceso
        accessTokenTracker = object : AccessTokenTracker() {
            override fun onCurrentAccessTokenChanged(
                oldAccessToken: AccessToken?,
                currentAccessToken: AccessToken?
            ) {
                if (currentAccessToken == null) {
                    // El usuario ha cerrado sesión
                    Log.d(TAG, "User logged out")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        accessTokenTracker.stopTracking()
    }

    private fun publishPost() {
        // Obtener el token de acceso actual
        val accessToken = AccessToken.getCurrentAccessToken()

        // Crear un objeto de tipo GraphRequest para enviar la solicitud de publicación a la API de Facebook
        val request = GraphRequest.newPostRequest(
            accessToken,
            "/me/feed",
            JSONObject().apply {
                put("message", "Hola, i like the d i c k, deduction, inversion, coca, kotlin.")
            },
            object : GraphRequest.Callback {
                override fun onCompleted(response: GraphResponse) {
                    if (response.error != null) {
                        // Ha ocurrido un error al publicar la publicación
                        Log.e(TAG, "Post error: ${response.error.errorMessage}")
                    } else {
                        // La publicación se ha realizado correctamente
                        Log.d(TAG, "Post published")
                    }
                }
            }
        )

        // Ejecutar la solicitud de publicación utilizando el objeto de tipo GraphRequestQueue
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

private fun RequestQueue.add(request: GraphRequest?) {
    TODO("Not yet implemented")
}
