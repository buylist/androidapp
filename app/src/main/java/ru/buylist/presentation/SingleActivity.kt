package ru.buylist.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.buylist.R

class SingleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)
    }

}