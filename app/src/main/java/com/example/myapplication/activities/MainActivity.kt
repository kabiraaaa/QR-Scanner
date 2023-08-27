package com.example.myapplication.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.use_case.CreateFragment
import com.example.myapplication.use_case.ScanCameraFragment
import com.example.myapplication.utils.CommonFunctionality

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CommonFunctionality.fullScreen(window, window.decorView)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        loadFragment(ScanCameraFragment())
        binding.scanBottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.scan -> {
                    Toast.makeText(this, "Scan", Toast.LENGTH_SHORT).show()
                    loadFragment(ScanCameraFragment())
                    true
                }

                R.id.create -> {
                    Toast.makeText(this, "Create", Toast.LENGTH_SHORT).show()
                    loadFragment(CreateFragment())
                    true
                }

                R.id.history -> {
                    Toast.makeText(this, "History", Toast.LENGTH_SHORT).show()
//                    loadFragment(SettingFragment())
                    true
                }

                R.id.Settings -> {
                    Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
//                    loadFragment(SettingFragment())
                    true
                }

                else -> {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
                    true
                }
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(binding.scanFrame.id, fragment)
        transaction.commit()
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}