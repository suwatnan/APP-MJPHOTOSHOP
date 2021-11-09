package com.example.mjphotoshop

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //binding bottom menu and fragment
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        /*
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home, R.id.nav_user, R.id.nav_history, R.id.nav_report, R.id.nav_logout))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        */
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment, HomeFragment())
        transaction.commit()
        navView.setOnNavigationItemSelectedListener {
            var fm: Fragment = HomeFragment()
            when (it.itemId) {
                R.id.nav_home -> fm = HomeFragment()
                R.id.nav_order -> fm = OrderFragment()
                R.id.nav_order2 -> fm = Order2Fragment()
                R.id.nav_user -> fm = UserFragment()
            }
            //this.supportActionBar!!.title = "Home"

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment, fm)
            transaction.commit()
            return@setOnNavigationItemSelectedListener true
        }
    }
}