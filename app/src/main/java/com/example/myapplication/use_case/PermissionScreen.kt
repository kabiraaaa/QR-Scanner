package com.example.myapplication.use_case

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.activities.MainActivity
import com.example.myapplication.databinding.ActivityPermissionScreenBinding
import com.example.myapplication.utils.PermissionHelper


class PermissionScreen : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        window.statusBarColor = ContextCompat.getColor(this, R.color.color_352F44);

        binding.cameraPermissionButton.setOnClickListener {
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        PermissionHelper.requestNotGrantedPermissions(
            this,
            PermissionHelper.CameraPermission,
            PermissionHelper.CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionHelper.CAMERA_PERMISSION_REQUEST_CODE && areAllPermissionsGranted(
                grantResults
            )
        ) {
            intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
        } else
            checkUserRequestedDontAskAgain()
    }

    private fun checkUserRequestedDontAskAgain() {
        val rationalFalgREAD =
            shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)
        if (!rationalFalgREAD) {
            showPermissionFromSettings()
        }
    }

    private fun showPermissionFromSettings() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Permission Required")
        builder.setMessage("Camera Permission Required. Please Allow!!")

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }

        builder.setNegativeButton(android.R.string.no) { dialog, which ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun areAllPermissionsGranted(grantResults: IntArray): Boolean {
        return PermissionHelper.areAllPermissionsGranted(grantResults)
    }
}