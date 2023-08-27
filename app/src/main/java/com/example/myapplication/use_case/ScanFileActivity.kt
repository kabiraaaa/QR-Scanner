package com.example.myapplication.use_case

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.myapplication.R
import com.example.myapplication.activities.ResultActivity
import com.example.myapplication.databinding.ActivityScanFileBinding
import com.example.myapplication.utils.PermissionHelper
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.io.IOException

class ScanFileActivity : AppCompatActivity() {

    private var imageUri: Uri? = null
    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_AZTEC,
            Barcode.FORMAT_ALL_FORMATS
        )
        .build()
    lateinit var scanner: BarcodeScanner

    lateinit var binding: ActivityScanFileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanFileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        scanner = BarcodeScanning.getClient()

        binding.animationView.visibility = View.VISIBLE
        Toast.makeText(this,"Opening Gallery",Toast.LENGTH_SHORT).show()

        if (showImageFromIntent().not()) {
            Handler(Looper.getMainLooper()).postDelayed({
                startChooseImageActivity(savedInstanceState)
            }, 1500)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((requestCode == PermissionHelper.IMAGE_CHOOSE_FILE_REQUEST_CODE || requestCode == PermissionHelper.IMAGE_CHOOSE_FILE_AGAIN_REQUEST_CODE) && resultCode == RESULT_OK) {
            data?.data?.apply(::scanCode)
            return
        }

        if (requestCode == PermissionHelper.IMAGE_CHOOSE_FILE_REQUEST_CODE) {
            finish()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PermissionHelper.STORAGE_PERMISSIONS_REQUEST_CODE && PermissionHelper.areAllPermissionsGranted(
                grantResults
            )
        ) {
            imageUri?.apply(::scanCode)
        } else {
            finish()
        }
    }

    private fun showImageFromIntent(): Boolean {
        var uri: Uri? = null

        if (intent?.action == Intent.ACTION_SEND && intent.type.orEmpty().startsWith("image/")) {
            uri = intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri
        }

        if (intent?.action == Intent.ACTION_VIEW && intent.type.orEmpty().startsWith("image/")) {
            uri = intent.data
        }

        if (uri == null) {
            return false
        }

        scanCode(uri)


        return true
    }

    private fun scanCode(uri: Uri) {
        binding.animationView.visibility = View.GONE
        binding.LoadingAnimationView.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            val image: InputImage
            try {
                image = InputImage.fromFilePath(this, uri)
                val result = scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        onSuccess(barcodes)
                    }
                    .addOnFailureListener {
                        // Task failed with an exception
                        // ...
                    }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }, 1500)
    }

    private fun onSuccess(barcodes: List<Barcode>) {
        for (barcode in barcodes) {
            val rawValue = barcode.rawValue
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("codeValue", rawValue)
            startActivity(intent)
        }
    }

    private fun startChooseImageActivity(savedInstanceState: Bundle?) {
        startChooseImageActivity(
            PermissionHelper.IMAGE_CHOOSE_FILE_REQUEST_CODE,
            savedInstanceState
        )
    }

    private fun startChooseImageActivity(requestCode: Int, savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            return
        }

        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            }

        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, requestCode)
        }
    }
}