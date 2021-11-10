package com.beginner.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beginner.myapplication.features.main.MainActivity
import kotlinx.coroutines.*

class WelcomeScreen : AppCompatActivity() {
    private val screenLauncher = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_screen)

        supportActionBar?.hide()

        screenLauncher.launch {
            delay(1000)

            val mainAct = Intent(this@WelcomeScreen, MainActivity::class.java)
            startActivity(mainAct)
            finish()
        }
    }
}