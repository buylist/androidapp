package ru.buylist.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import kotlinx.android.synthetic.main.activity_fragment.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.buylist.R

class SingleActivity : AppCompatActivity() {
    private lateinit var appBarConfig: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)

        val navController = findNavController(R.id.fragment_container)

        // toolbar config
        setSupportActionBar(toolbar)
        appBarConfig = AppBarConfiguration(
                setOf(R.id.lists_fragment, R.id.pattern_fragment, R.id.recipe_fragment),
                drawer_layout)
        setupActionBarWithNavController(navController, appBarConfig)

        // setup drawer and bottom navigation
        nav_drawer.setupWithNavController(navController)
        nav_bottom.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment_container)
        return navController.navigateUp(appBarConfig) || super.onSupportNavigateUp()
    }

}