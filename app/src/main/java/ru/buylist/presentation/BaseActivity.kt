package ru.buylist.presentation

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import ru.buylist.R

abstract class BaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())
        setupBottomNavigation()
        setupDrawerMenu()
    }

    @LayoutRes
    private fun getLayoutResId(): Int {
        return R.layout.activity_fragment
    }

    protected abstract fun setupBottomNavigation()

    protected abstract fun setupDrawerMenu()
}