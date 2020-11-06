package ru.focusstart.kireev.homeworktimer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.focusstart.kireev.homeworktimer.fragments.TimerFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            addFragment()
        }
    }

    private fun addFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.container, TimerFragment.newInstance())
        transaction.commit()
    }
}