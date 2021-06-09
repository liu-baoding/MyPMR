package com.example.todolist.ui

import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.R
import com.example.todolist.data.DataProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private val CAT: String = "TODO_MAIN"
    private lateinit var sp: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var login: String
    private lateinit var password: String
    private lateinit var token: String
    private var success: Boolean = false

    // coroutine
    private val activityScope = CoroutineScope(
        SupervisorJob() +
                Dispatchers.Main
    )

    // initialize onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(CAT, "onCreate") // trace d'execution

        sp = PreferenceManager.getDefaultSharedPreferences(this)
        editor = sp.edit()

        cbRemember.isChecked = false // don't remember the user in default

        // press OK to get authentication and start next activity
        btnOK.setOnClickListener {
            login = edtPseudo.text.toString()
            password = edtPassword.text.toString()

            // login can't be empty
            if (login == "") {
                ToastUtil.newToast(this, "Please enter your login!")
            } else {
                val intent = Intent(this, ChoixListActivity::class.java)
                intent.putExtra("pseudo", login)

                // get authentication, block Main thread before finished, save token of user
                runBlocking {
                    Log.d(CAT, "runBlocking...  currentThread：${Thread.currentThread().name}")
                    val job = launch {
                        try {
//                            // test use
//                            val gets = DataProvider.signIn("liu", "baoding")
                            val gets = DataProvider.signIn(login, password)
                            val success = gets.success.toBoolean()
                            this@MainActivity.success = success
                            val token = gets.token
                            this@MainActivity.token = token
                            // save token of user
                            // To save token for all users (check the cb or not), this
                            // can't be in the same PreferenceCategory in the XML file
                            editor.putString("edtToken", token)
                            editor.apply()
                        } catch (e: Exception) {
                            // reset the field success
                            this@MainActivity.success = false
                            ToastUtil.newToast(this@MainActivity, "ERROR!" + "\n" + "${e.message}")
                            Log.i(CAT, "${e.message}")
                        }
                    }
                    job.join()
                }

                // save settings
                if (cbRemember.isChecked) {
                    editor.putString("login", login)
                    editor.putString("password", password)
                    editor.apply()
                }

                if (!success) {
                    ToastUtil.newToast(this@MainActivity, "Incorrect password!")
                } else {
                    // if authenticated
                    ToastUtil.newToast(this@MainActivity, "Welcome $login!")

                    // start activity
                    startActivity(intent)
                    Log.i(CAT, "start activity!")
                }
            }
        }

        // cb for remember or not
        cbRemember.setOnClickListener {
            ToastUtil.newToast(this, "click on cb")
            editor.putBoolean("remember", cbRemember.isChecked)
            editor.apply()
        }
    }

    //generate menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    // select menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_settings -> {
                ToastUtil.newToast(this, "Menu : click on preferences")
                editor.putBoolean("remember", cbRemember.isChecked)
                editor.apply()
                if (!cbRemember.isChecked) {
                    editor.putString("login", "")
                    editor.putString("password", "")
                    editor.apply()
                } else {
                    editor.putString("login", edtPseudo.text.toString())
                    editor.putString("password", edtPassword.text.toString())
                    editor.apply()
                }
                val iGP = Intent(this, SettingsActivity::class.java)
                iGP.apply {
//                    putExtra("URL","http://tomnab.fr/fixture/")
                    startActivity(this)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // onStart, load users settings
    override fun onStart() {
        super.onStart()
        // relire les preferences partagees de l'app
        val cbR = sp.getBoolean("remember", false)

        // actualiser l'etat de la case a cocher
        cbRemember.isChecked = cbR

        // si la case est cochee, on utilise les preferences pour definir le login
        if (cbR) {
            val l = sp.getString("login", "login inconnu")
            val p = sp.getString("password", "")
            edtPseudo.setText(l)
            edtPassword.setText(p)
        } else {
            // sinon, le champ doit etre vide
            edtPseudo.setText("")
            edtPassword.setText("")
        }
    }

    // when restart MainActivity, clear the saved token
    override fun onRestart() {
        super.onRestart()
        editor.putString("edtToken", "")
        editor.commit()
    }

    // onResume, checkout network
    override fun onResume() {
        super.onResume()
        // S'il n'y a pas de reseau, on désactive le bouton
        btnOK.isEnabled = verifReseau()
    }

    // checkout network
    private fun verifReseau(): Boolean {
        // On vérifie si le réseau est disponible,
        // si oui on change le statut du bouton de connexion
        val cnMngr = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cnMngr.activeNetworkInfo
        var sType = "Aucun réseau détecté"
        var bStatut = false
        if (netInfo != null) {
            val netState = netInfo.state
            if (netState.compareTo(NetworkInfo.State.CONNECTED) == 0) {
                bStatut = true
                when (netInfo.type) {
                    ConnectivityManager.TYPE_MOBILE -> sType = "Réseau mobile détecté"
                    ConnectivityManager.TYPE_WIFI -> sType = "Réseau wifi détecté"
                }
            }
        }
        Log.i(CAT, sType)
        return bStatut
    }

}