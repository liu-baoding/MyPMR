package com.example.todolist

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    val CAT: String = "TODO_MAIN"
    private lateinit var sp: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    // TODO: API

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i(CAT, "onCreate") // trace d'execution

        sp = PreferenceManager.getDefaultSharedPreferences(this)
        editor = sp.edit()

        btnOK.setOnClickListener {
            alerter("click on btnOK")
            if (cbRemember.isChecked) {
                editor.putString("login", pseudo.text.toString())
                editor.commit()
            }
            Intent(this, ChoixListActivity::class.java).apply {
                putExtra("pseudo", pseudo.text.toString())
                startActivity(this)
            }
        }

        cbRemember.setOnClickListener {
            alerter("click on cb")
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
                alerter("Menu : click on preferences")
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

    private fun alerter(s: String) {
        Log.i(CAT, s)
        var t = Toast.makeText(this, s, Toast.LENGTH_SHORT)
        t.show()
    }

}