package com.example.myapplication.use_case

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Size
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myapplication.activities.ResultActivity
import com.example.myapplication.databinding.FragmentScanCameraBinding
import com.example.myapplication.utils.QRCodeAnalyzer
import com.google.mlkit.vision.barcode.common.Barcode
import java.time.LocalDateTime
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanCameraFragment : Fragment() {

    private var _binding: FragmentScanCameraBinding? = null
    private val binding get() = _binding!!

    private lateinit var analysisExecutor: ExecutorService
    private var barcodeFormats = intArrayOf(Barcode.FORMAT_ALL_FORMATS)
    private var hapticFeedback = true
    private var showTorchToggle = true
    private var useFrontCamera = false

    lateinit var scanTime : LocalDateTime

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentScanCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        analysisExecutor = Executors.newSingleThreadExecutor()
        startCamera()
        binding.overlayView.setGalleryVisibilityAndOnClick(true){scanFromGallery()}
    }

    private fun startCamera() {
        val cameraProviderFuture = try {
            ProcessCameraProvider.getInstance(binding.root.context)
        } catch (e: Exception) {
            onFailure(e)
            return
        }

        cameraProviderFuture.addListener({
            val cameraProvider = try {
                cameraProviderFuture.get()
            } catch (e: Exception) {
                onFailure(e)
                return@addListener
            }

            val preview = Preview.Builder().build().also { it.setSurfaceProvider(binding.previewView.surfaceProvider) }
            val imageAnalysis = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .build()
                .also {
                    it.setAnalyzer(
                        analysisExecutor,
                        QRCodeAnalyzer(
                            barcodeFormats = barcodeFormats,
                            onSuccess = { barcode ->
                                it.clearAnalyzer()
                                onSuccess(barcode)
                                Toast.makeText(activity,barcode.rawValue,Toast.LENGTH_SHORT).show()
                            },
                            onFailure = { exception -> onFailure(exception) },
                            onPassCompleted = { failureOccurred -> onPassCompleted(failureOccurred) }
                        )
                    )
                }

            cameraProvider.unbindAll()

            val cameraSelector =
                if (useFrontCamera) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA

            try {
                val camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
                binding.overlayView.visibility = View.VISIBLE
                if (showTorchToggle && camera.cameraInfo.hasFlashUnit()) {
                    binding.overlayView.setTorchVisibilityAndOnClick(true) { camera.cameraControl.enableTorch(it) }
                    camera.cameraInfo.torchState.observe(viewLifecycleOwner) { binding.overlayView.setTorchState(it == TorchState.ON) }
                } else {
                    binding.overlayView.setTorchVisibilityAndOnClick(false)
                }
            } catch (e: Exception) {
                binding.overlayView.visibility = View.INVISIBLE
                onFailure(e)
            }
        }, ContextCompat.getMainExecutor(binding.root.context))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onSuccess(result: Barcode) {
        binding.overlayView.isHighlighted = true
        if (hapticFeedback) {
            @Suppress("DEPRECATION")
            val flags = HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING or HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
            binding.overlayView.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP, flags)
        }
        scanTime = LocalDateTime.now()
        val intent =Intent(activity, ResultActivity::class.java)
        intent.putExtra("codeValue", result.rawValue)
        intent.putExtra("codeTime", scanTime.toString())
        startActivity(intent)
        /*setResult(
            Activity.RESULT_OK,
            Intent().apply {
                putExtra("EXTRA_RESULT_BYTES", result.rawBytes)
                putExtra("EXTRA_RESULT_VALUE", result.rawValue)
                putExtra("EXTRA_RESULT_TYPE", result.valueType)
                putExtra("EXTRA_RESULT_PARCELABLE", result.toParcelableContentType())
            }
        )*/
    }

    private fun onFailure(e: Exception) {
        e.printStackTrace()
    }

    private fun onPassCompleted(failureOccurred: Boolean) {
    }

    private fun scanFromGallery() {
        val intent = Intent(activity, ScanFileActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        startCamera()
    }
}
