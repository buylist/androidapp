package ru.buylist.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import kotlinx.android.synthetic.main.activity_fragment.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.buylist.R
import ru.buylist.presentation.about.AboutBuyListActivity
import ru.buylist.utils.changeFirstStart
import ru.buylist.utils.getFirstStart

class SingleActivity : AppCompatActivity() {
    private lateinit var appBarConfig: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)

        val navController = findNavController(R.id.fragment_container)

        // toolbar config
        setSupportActionBar(toolbar)
        appBarConfig = AppBarConfiguration(
                setOf(R.id.buy_lists_fragment, R.id.patterns_fragment, R.id.recipes_fragment),
                drawer_layout)
        setupActionBarWithNavController(navController, appBarConfig)

        // setup drawer and bottom navigation
        nav_drawer.setupWithNavController(navController)
        nav_bottom.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when(destination.id) {
                R.id.buy_lists_fragment -> showBottomMenu()
                R.id.patterns_fragment -> showBottomMenu()
                R.id.recipes_fragment -> showBottomMenu()
                else -> hideBottomMenu()
            }
        }

        nav_drawer.setNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.about_fragment) {
                showIntro()
                true
            }

            NavigationUI.onNavDestinationSelected(item, navController)
            drawer_layout.closeDrawers()
            true
        }

        checkFirstStart()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment_container)
        return navController.navigateUp(appBarConfig) || super.onSupportNavigateUp()
    }

    private fun checkFirstStart() {
        val isFirst = getFirstStart()
        if (isFirst) {
            showIntro()
            changeFirstStart()
        }
    }

    private fun showIntro() {
        val intent = Intent(this, AboutBuyListActivity::class.java)
        startActivity(intent)
    }

    private fun showBottomMenu() {
        nav_bottom.visibility = View.VISIBLE
    }

    private fun hideBottomMenu() {
        nav_bottom.visibility = View.GONE
    }

}