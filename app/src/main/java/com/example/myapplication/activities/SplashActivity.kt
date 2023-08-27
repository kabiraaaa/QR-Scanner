package com.example.myapplication.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.ScanActivity
import com.example.myapplication.use_case.PermissionScreen
import com.example.myapplication.utils.PermissionHelper

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            if(!PermissionHelper.checkGrantedPermission(this, PermissionHelper.CameraPermission)){
                intent = Intent(applicationContext, PermissionScreen::class.java)
                startActivity(intent)
            }
            else{
                intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            }


        },3000)

    }
}