package com.example.todolist

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.model.ProfilListeToDo
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*


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
    val gson = Gson()

    // TODO: API

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(CAT, "onCreate") // trace d'execution

        sp = PreferenceManager.getDefaultSharedPreferences(this)
        editor = sp.edit()

        cbRemember.isChecked=true // remember the user in default

        // a SP object to pass messenger between activities
        spJson = getSharedPreferences("SP_Data_List", MODE_PRIVATE)
        editorJson = spJson.edit()

        btnOK.setOnClickListener {
            login = pseudo.text.toString()
            if (login==""||login==null){
                ToastUtil.newToast(this,"Please enter your login!")
            } else {
                ToastUtil.newToast(this, "click on btnOK")
                if (cbRemember.isChecked) {
                    editor.putString("login", login)
                    editor.commit()
                }

                var jsonStr: String? = spJson.getString(login, "")

                // create the profil of this login if not exists, load if exists
                if (jsonStr == null || jsonStr == "") {
                    profil = ProfilListeToDo(login)
                } else {
                    profil = gson.fromJson(jsonStr, ProfilListeToDo::class.java)
                }

                val intent = Intent(this, ChoixListActivity::class.java)
                intent.putExtra("profil", this.profil)
                startActivityForResult(intent, requestCode)
            }
        }

        cbRemember.setOnClickListener {
            ToastUtil.newToast(this,"click on cb")
            editor.putBoolean("remember", cbRemember.isChecked)
            editor.commit()
            if (!cbRemember.isChecked) {
                editor.putString("login", "")
                editor.commit()
            }
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
                ToastUtil.newToast(this,"Menu : click on preferences")
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
            pseudo.setText(l)
        } else {
            // sinon, le champ doit etre vide
            pseudo.setText("")
        }

    }

    override fun onResume() {
        super.onResume()
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

}