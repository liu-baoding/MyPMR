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
import com.example.todolist.model.ProfilListeToDo
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    val CAT: String = "TODO_MAIN"
    private lateinit var sp: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    val requestCode = 1
    lateinit var profil: ProfilListeToDo
    private lateinit var spJson: SharedPreferences
    private lateinit var editorJson: SharedPreferences.Editor
    private lateinit var login: String
    private lateinit var password: String
    private lateinit var token: String
    private var isSuccess: Boolean = false
    val gson = Gson()

    // TODO: API
    private val activityScope = CoroutineScope(
        SupervisorJob() +
                Dispatchers.Main
    )
    var job : Job? = null
//    private lateinit var postAdapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(CAT, "onCreate") // trace d'execution

        sp = PreferenceManager.getDefaultSharedPreferences(this)
        editor = sp.edit()

        cbRemember.isChecked=false // don't remember the user in default

        // a SP object to pass messenger between activities
        spJson = getSharedPreferences("SP_Data_List", MODE_PRIVATE)
        editorJson = spJson.edit()

        btnOK.setOnClickListener {
            login = edtPseudo.text.toString()
            password = edtPassword.text.toString()
            // login can't be empty
            if (login==""||login==null){
                ToastUtil.newToast(this, "Please enter your login!")
            } else {
                // get authentication
                signInGetToken("liu", "baoding")

                // if authenticated
                if (isSuccess){
                    // save settings
                    if (cbRemember.isChecked) {
                        editor.putString("login", login)
                    }
                    // save token of user
                    editor.putString("edtToken", token)
                    editor.commit()

                    // TODO to be changed
                    var jsonStr: String? = spJson.getString(login, "")

                    // create the profil of this login if not exists, load if exists
                    if (jsonStr == null || jsonStr == "") {
                        profil = ProfilListeToDo(login)
                    } else {
                        profil = gson.fromJson(jsonStr, ProfilListeToDo::class.java)
                    }

                    // start activity
                    val intent = Intent(this, ChoixListActivity::class.java)
                    intent.putExtra("profil", this.profil)
//                    startActivityForResult(intent, requestCode)
                    Log.i(CAT,"start activity!")
                }
            }

        }

        cbRemember.setOnClickListener {
            ToastUtil.newToast(this, "click on cb")
            editor.putBoolean("remember", cbRemember.isChecked)
            editor.commit()
            if (!cbRemember.isChecked) {
                editor.putString("login", "")
                editor.commit()
            }
        }

        btnTest.setOnClickListener {
            loadAndDisplayPosts()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.menu_settings -> {
                ToastUtil.newToast(this, "Menu : click on preferences")
                val iGP = Intent(this, SettingsActivity::class.java)
                iGP.apply {
//                    putExtra("URL","http://tomnab.fr/fixture/")
                    startActivity(this)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        // relire les preferences partagees de l'app
        val cbR = sp.getBoolean("remember", false)

        // actualiser l'etat de la case a cocher
        cbRemember.isChecked = cbR

        // si la case est cochee, on utilise les preferences pour definir le login
        if (cbRemember.isChecked) {
            val l = sp.getString("login", "login inconnu")
            edtPseudo.setText(l)
        } else {
            // sinon, le champ doit etre vide
            edtPseudo.setText("")
        }

    }

    override fun onResume() {
        super.onResume()
        // S'il n'y a pas de reseau, on désactive le bouton
        btnOK.isEnabled = verifReseau()
    }

    override fun onRestart() {
        super.onRestart()
    }

    // receive data from ChoixListActivity after press "back"
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            requestCode -> if (resultCode === RESULT_OK) {
                val returnData: ProfilListeToDo = data!!.getSerializableExtra("profil") as ProfilListeToDo
                this.profil = returnData
            }
        }
        Log.i(CAT, this.profil.toString())

        // stock changes
        val response = gson.toJson(this.profil)
        editorJson.putString(login, java.lang.String.valueOf(response)) // save Json
        editorJson.commit() // stock
    }

    // TEST: get data from API
    private fun loadAndDisplayPosts() {

        activityScope.launch {
//            showProgress(true)
            try {
                val posts = DataProvider.getPostFromApi()
                Log.i(CAT, posts.toString())

            } catch (e: Exception) {
                ToastUtil.newToast(this@MainActivity, "${e.message}")
                Log.i(CAT, "${e.message}")
            }
//            showProgress(false)
        }
    }

    // get token of user
    private fun signInGetToken(login: String, password: String) {

        activityScope.launch {
//            showProgress(true)
            try {
                val gets = DataProvider.signIn(login, password)
                val success = gets.success
                this@MainActivity.isSuccess = success.toBoolean()
                val token = gets.token
                Log.i(CAT, success.toString())
                if (success=="false") {
                    ToastUtil.newToast(this@MainActivity, "Incorrect password!")
                } else {
                    ToastUtil.newToast(this@MainActivity, "Welcome $login!")
                    this@MainActivity.token = token
                }
            } catch (e: Exception) {
                ToastUtil.newToast(this@MainActivity, "ERROR!"+"\n"+"${e.message}")
                Log.i(CAT, "${e.message}")
            }
//            showProgress(false)
        }
    }

    fun verifReseau(): Boolean {
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
                val netType = netInfo.type
                when (netType) {
                    ConnectivityManager.TYPE_MOBILE -> sType = "Réseau mobile détecté"
                    ConnectivityManager.TYPE_WIFI -> sType = "Réseau wifi détecté"
                }
            }
        }
        Log.i(CAT, sType)
        return bStatut
    }

}