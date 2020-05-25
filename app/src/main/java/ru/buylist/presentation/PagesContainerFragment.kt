package ru.buylist.presentation

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.fragment_pages_container.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.buylist.R

class PagesContainerFragment : BaseFragment() {

    private lateinit var toggle: ActionBarDrawerToggle

    override val layoutResId = R.layout.fragment_pages_container

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupDrawerContent()
    }

    private fun setupToolbar() {
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(toolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        toggle = ActionBarDrawerToggle(activity, drawer_layout, toolbar, R.string.open, R.string.close)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()
        drawer_layout.addDrawerListener(toggle)
    }

    private fun setupDrawerContent() {
        nav_drawer.setNavigationItemSelectedListener {
            selectDrawerItem(it)
            true
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    private fun selectDrawerItem(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.nav_login -> Toast.makeText(context, "login", Toast.LENGTH_LONG).show()
            R.id.nav_settings -> Toast.makeText(context, "settings", Toast.LENGTH_LONG).show()
            R.id.nav_about -> Toast.makeText(context, "about", Toast.LENGTH_LONG).show()
            R.id.nav_feedback -> Toast.makeText(context, "feedback", Toast.LENGTH_LONG).show()
        }

        drawer_layout.closeDrawers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item)
    }

}