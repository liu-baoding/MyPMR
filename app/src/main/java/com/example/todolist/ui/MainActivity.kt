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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.todolist.R
import com.example.todolist.data.DataProvider
import com.example.todolist.data.DbDataProvider
import com.example.todolist.data.model.ItemChangeDb
import com.example.todolist.data.model.ItemDb
import com.example.todolist.data.model.OneItem
import com.example.todolist.data.model.UserDb
import kotlinx.android.synthetic.main.activity_choix_list.*
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

    private val ONLINE = true
    private val OFFLINE = false
    private var mode = false

    private lateinit var dbDataProvider: DbDataProvider

    private lateinit var itemChanges: List<ItemChangeDb>
    private lateinit var itemAdds: List<ItemChangeDb>

    // coroutine
    private val activityScope = CoroutineScope(
        SupervisorJob() +
                Dispatchers.Main
    )

    // initialize onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showProgress(false)
        Log.i(CAT, "onCreate") // trace d'execution

        sp = PreferenceManager.getDefaultSharedPreferences(this)
        editor = sp.edit()

        dbDataProvider = DbDataProvider(application)

        cbRemember.isChecked = false // don't remember the user in default
        success = false // status in default

        chooseRunMode()
    }


    private fun activityInitOnline() {
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

                // save settings
                if (cbRemember.isChecked) {
                    editor.putString("login", login)
                    editor.putString("password", password)
                    editor.apply()

                    // get authentication, save token of user, login
                    getAuthenLoginOnline(intent)
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

    private fun activityInitOffline() {
        // press OK to get authentication and start next activity
        btnOK.setOnClickListener {
            // when offline, token will not be used
            login = edtPseudo.text.toString()
            password = edtPassword.text.toString()

            // login can't be empty
            if (login == "") {
                ToastUtil.newToast(this, "Please enter your login!")
            } else {
                val intent = Intent(this, ChoixListActivity::class.java)
                intent.putExtra("pseudo", login)

                // save settings
                if (cbRemember.isChecked) {
                    editor.putString("login", login)
                    editor.putString("password", password)
                    editor.apply()
                }

                // get authentication, block Main thread before finished, save token of user
                getAuthenLoginOffline(intent)
            }
        }

        // cb for remember or not
        cbRemember.setOnClickListener {
            ToastUtil.newToast(this, "click on cb")
            editor.putBoolean("remember", cbRemember.isChecked)
            editor.apply()
        }
    }

    private fun getAuthenLoginOnline(intent: Intent) {
        activityScope.launch {
            showProgress(true)
            try {
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
            } finally {
                if (!success) {
                    ToastUtil.newToast(this@MainActivity, "Incorrect password!")
                } else {
                    // if authenticated
                    ToastUtil.newToast(this@MainActivity, "Welcome $login!")

                    // add the user in db
                    val newUserDb = UserDb(login, password, token)
                    addUserInDb(newUserDb)

                    // apply changes in API
                    try {
                        // items to change status
                        itemChanges = dbDataProvider.getAllItemChanges().filter {
                            it.operation == "change" && it.token == token
                        }
                        itemAdds = dbDataProvider.getAllItemChanges().filter {
                            it.operation == "add" && it.token == token
                        }
                    } catch (e: Exception) {
                        // reset the field success
                        this@MainActivity.success = false
                        ToastUtil.newToast(this@MainActivity, "ERROR!" + "\n" + "${e.message}")
                        Log.i(CAT, "${e.message}")
                    }
                    if (itemChanges.isNotEmpty() || itemAdds.isNotEmpty()) {
                        ToastUtil.newToast(
                            this@MainActivity,
                            "Uploading your changes stored locally..."
                        )
                        // record mapping between id_in_db and id_in_API for the added items
                        val map = mutableMapOf<String, String>()

                        Log.i(CAT, "add: $itemAdds")
                        itemAdds.forEach {
                            try {
                                DataProvider.addItem(
                                    it.listContainerId,
                                    it.label,
                                    it.token
                                ).aItem.also { it1: OneItem ->
                                    dbDataProvider.addUpdateNewItem(
                                        ItemDb(
                                            it1.id,
                                            it1.label,
                                            it1.url,
                                            it1.checkedStr,
                                            it.listContainerId
                                        )
                                    )
                                    // delete the locally stocked item
                                    dbDataProvider.deleteItemById(it.itemId)
                                    map[it.itemId] = it1.id
                                }
                            } catch (e: Exception) {
                                Log.i(CAT, "add error: $it, $e")
                            } finally {
                                // delete record if successfully applied
                                dbDataProvider.deleteItemRecord(it.itemId)
                            }
                        }
                        Log.i(CAT, "Apply adds done")
                        Log.i(CAT, "map: $map")
                        Log.i(CAT, "change: $itemChanges")
                        itemChanges.forEach {
                            try {
                                // replace id for new added item
                                var newId = it.itemId
                                // id of changes for new added items begin with "new_"
                                if (!it.itemId[0].isDigit()) {
                                    map.filter { it1 ->
                                        it.itemId.contains(it1.key)
                                    }.forEach { it2 ->
                                        // should have only one matched term
                                        newId = it2.value
                                    }
                                }
                                DataProvider.changeItem(
                                    it.listContainerId,
                                    newId,
                                    it.checked,
                                    it.token
                                )
                            } catch (e: Exception) {
                                Log.i(CAT, "change error: $it, $e")
                            } finally {
                                // delete record if successfully applied
                                dbDataProvider.deleteItemRecord(it.itemId)
                            }
                        }
                        Log.i(CAT, "Apply changes done")
                        delay(1000L)
                        ToastUtil.newToast(this@MainActivity, "Apply changes done.")
                    }

                    // start activity
                    intent.putExtra("mode", this@MainActivity.mode)
                    startActivity(intent)
                    Log.i(CAT, "start activity!")
                }
            }
            showProgress(false)
        }
    }

    private fun getAuthenLoginOffline(intent: Intent) {
        activityScope.launch {
            try {
                val gets = dbDataProvider.getUserByPseudo(login)
                when {
                    gets.isEmpty() -> {
                        // no match
                        ToastUtil.newToast(this@MainActivity, "Never stored user!")
                        this@MainActivity.success = false
                    }
                    gets[0].password != password -> {
                        // have match, and only one since pseudo primary key
                        ToastUtil.newToast(this@MainActivity, "Password incorrect!")
                        this@MainActivity.success = false
                    }
                    else -> {
                        ToastUtil.newToast(this@MainActivity, "Welcome $login!")
                        this@MainActivity.success = true
                        // get token of the user
                        editor.putString("edtToken", gets[0].token)
                        editor.apply()
                    }
                }
            } catch (e: Exception) {
                // reset the field success
                this@MainActivity.success = false
                ToastUtil.newToast(this@MainActivity, "ERROR!" + "\n" + "${e.message}")
                Log.i(CAT, "${e.message}")
            } finally {
                if (success) {
                    // start activity
                    intent.putExtra("mode", this@MainActivity.mode)
                    startActivity(intent)
                    Log.i(CAT, "start activity!")
                }
            }
        }
    }

    private fun addUserInDb(newUserDb: UserDb) {
        activityScope.launch {
            dbDataProvider.addNewUser(newUserDb)
            Log.i(CAT, "Add new user in db")
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

    // choose run mode according to the network
    private fun chooseRunMode() {
        // for test use
//        if (!verifReseau()) {
        if (verifReseau()) {
            mode = ONLINE
            btnOK.isEnabled = true
            activityInitOnline()
        } else {
            mode = OFFLINE
            showAlertDialogOffline()
            activityInitOffline()
        }
    }

    // a dialog to inform user when offline
    private fun showAlertDialogOffline() {
        with(AlertDialog.Builder(this)) {
            setTitle("Attention!")
            setMessage(
                "The network is now unavailable, do you want to continue?\n" +
                        "You can only check/uncheck todo items, and changes will be applied when application restart with network available."
            )
            setPositiveButton("Yes") { dialog, which ->
                btnOK.isEnabled = true
            }
            setNegativeButton("No") { dialog, which ->
                btnOK.isEnabled = false
            }
            create()
        }.show()
    }

    // display progress bar when loading data
    private fun showProgress(show: Boolean) {
        val progress = progressMain
        progress.isVisible = show
    }
}