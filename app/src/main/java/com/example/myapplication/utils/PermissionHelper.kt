package com.example.myapplication.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionHelper {

    val CameraPermission = arrayOf(Manifest.permission.CAMERA)
    val CAMERA_PERMISSION_REQUEST_CODE = 101
    val IMAGE_CHOOSE_FILE_REQUEST_CODE = 12
    val IMAGE_CHOOSE_FILE_AGAIN_REQUEST_CODE = 13
    val STORAGE_PERMISSIONS_REQUEST_CODE = 14
    val GalleryPermission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    fun requestNotGrantedPermissions(activity: AppCompatActivity, permissions: Array<out String>, requestCode: Int) {
        val notGrantedPermissions = permissions.filterNot { isPermissionGranted(activity, it) }
        if (notGrantedPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, notGrantedPermissions.toTypedArray(), requestCode)
        }
    }

    fun checkGrantedPermission(context: Context, permissions: Array<out String>): Boolean {
        permissions.forEach {
            if (ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED) {
                return true
            }
        }
        return false
    }

    fun areAllPermissionsGranted(grantResults: IntArray): Boolean {
        grantResults.forEach { result ->
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

}