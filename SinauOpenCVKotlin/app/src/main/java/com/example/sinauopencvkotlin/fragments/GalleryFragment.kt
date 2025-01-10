package com.example.sinauopencvkotlin.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.sinauopencvkotlin.mediapipe.MainViewModel
import com.example.sinauopencvkotlin.mediapipe.PoseLandmarkerHelper
import com.example.sinauopencvkotlin.databinding.FragmentGalleryBinding
import com.example.sinauopencvkotlin.mediapipe.PoseLandmarkerHelper.Companion.TAG
import com.google.mediapipe.tasks.vision.core.RunningMode
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

class GalleryFragment : Fragment() {
    enum class MediaType {
        IMAGE,
        UNKNOWN
    }

    private var bitmap: Bitmap? = null

    var angle = 0.0

    companion object {
        private var SELECT_CODE = 0
    }

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private lateinit var poseLandmarkerHelper: PoseLandmarkerHelper
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var galleryExecutor: ScheduledExecutorService

    private val getImage =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let { imageUri ->
                when (val mediaType = loadMediaType(imageUri)) {
                    MediaType.IMAGE -> runDetectionOnImage(imageUri)
                    MediaType.UNKNOWN -> {
                        updateDisplayView(mediaType)
                        Toast.makeText(
                            requireContext(),
                            "Unsupported data type",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
//        pickImage()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.pickerButton.setOnClickListener {
            getImage.launch(arrayOf("image/*"))
        }
    }

    override fun onPause() {
        binding.overlay.clear()
        super.onPause()
    }

    private fun runDetectionOnImage(uri: Uri) {
        galleryExecutor = Executors.newSingleThreadScheduledExecutor()
        updateDisplayView(MediaType.IMAGE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(
                requireActivity().contentResolver,
                uri
            )
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(
                requireActivity().contentResolver,
                uri
            )
        }
            .copy(Bitmap.Config.ARGB_8888, true)
            ?.let { bitmap ->
                val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
                val width = 600
                val height = (600 / ratio).toInt()

                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)

                binding.imageView.setImageBitmap(scaledBitmap)

                // Run pose landmarker on the input image
                galleryExecutor.execute {

                    poseLandmarkerHelper =
                        PoseLandmarkerHelper(
                            context = requireContext(),
                            runningMode = RunningMode.IMAGE,
                            minPoseDetectionConfidence = viewModel.currentMinPoseDetectionConfidence,
                            minPoseTrackingConfidence = viewModel.currentMinPoseTrackingConfidence,
                            minPosePresenceConfidence = viewModel.currentMinPosePresenceConfidence,
                            currentDelegate = viewModel.currentDelegate
                        )

                    val detectionImage = centerCropBitmap(binding.imageView.drawable.toBitmap(), binding.imageView.width, binding.imageView.height)

                    poseLandmarkerHelper.detectImage(detectionImage)?.let { result ->
                            activity?.runOnUiThread {
                                binding.overlay.setResults(
                                    result.results[0],
                                    detectionImage.height,
                                    detectionImage.width,
                                    RunningMode.IMAGE
                                )
                            } ?: run { Log.e(TAG, "Error running pose landmarker.") }

                            poseLandmarkerHelper.clearPoseLandmarker()
                        }

                    angle = binding.overlay.getAngle()
                    binding.textAngle.text = angle.toString()
                    binding.textPercentage.text = String.format("%.2f", angle/1.35) + "%"
                }
            }
    }

    private fun centerCropBitmap(source: Bitmap, width: Int, height: Int): Bitmap {
        val sourceWidth = source.width
        val sourceHeight = source.height

        val xOffset: Int
        val yOffset: Int
        val scaledWidth: Int
        val scaledHeight: Int

        // Calculate aspect ratios
        if (sourceWidth * height > width * sourceHeight) {
            scaledHeight = sourceHeight
            scaledWidth = (width.toFloat() / height.toFloat() * sourceHeight).toInt()
            xOffset = (sourceWidth - scaledWidth) / 2
            yOffset = 0
        } else {
            scaledWidth = sourceWidth
            scaledHeight = (height.toFloat() / width.toFloat() * sourceWidth).toInt()
            xOffset = 0
            yOffset = (sourceHeight - scaledHeight) / 2
        }

        return Bitmap.createBitmap(source, xOffset, yOffset, scaledWidth, scaledHeight)
    }


    private fun updateDisplayView(mediaType: MediaType) {
        binding.imageView.visibility =
            if (mediaType == MediaType.IMAGE) View.VISIBLE else View.GONE
    }

    private fun loadMediaType(uri: Uri): MediaType {
        val mimeType = context?.contentResolver?.getType(uri)
        mimeType?.let {
            if (mimeType.startsWith("image")) return MediaType.IMAGE
        }

        return MediaType.UNKNOWN
    }

    fun classifyingError() {
        activity?.runOnUiThread {
            binding.progress.visibility = View.GONE
            updateDisplayView(MediaType.IMAGE)
        }
    }

    fun onResults(resultBundle: PoseLandmarkerHelper.ResultBundle) {
        // no-op
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_CODE && resultCode == Activity.RESULT_OK) {
            try {
                val imageUri: Uri? = data?.data
                bitmap = MediaStore.Images.Media.getBitmap(
                    requireContext().contentResolver,
                    imageUri
                )
                binding.imageView.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}