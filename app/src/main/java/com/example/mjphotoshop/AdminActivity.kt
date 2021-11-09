package com.example.mjphotoshop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //binding bottom menu and fragment
        val navView: BottomNavigationView = findViewById(R.id.nav_view1)
        /*
        val navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home, R.id.nav_user, R.id.nav_history, R.id.nav_report, R.id.nav_logout))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        */
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment2, AdminFragment())
        transaction.commit()
        navView.setOnNavigationItemSelectedListener {
            var fm: Fragment = AdminFragment()
            when (it.itemId) {
                R.id.nav_admin -> fm = AdminFragment()
                R.id.nav_order -> fm = OrderAdminFragment()
                R.id.nav_logout -> fm = LogoutFragment()


            }
            //this.supportActionBar!!.title = "Home"

            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.nav_host_fragment2, fm)
            transaction.commit()
            return@setOnNavigationItemSelectedListener true
        }
    }
}
