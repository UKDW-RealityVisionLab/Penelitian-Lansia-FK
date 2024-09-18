package com.example.sinauopencvkotlin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.sinauopencvkotlin.databinding.FragmentGalleryBinding
import org.opencv.android.Utils
import org.opencv.core.Mat
import java.io.IOException

class GalleryFragment : Fragment() {
//    var bitmap: Bitmap? = null
//    var mat: Mat? = null

    companion object {
        private var SELECT_CODE = 0
    }
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        pickImage()
        return binding.root
    }

    fun pickImage() {
        binding.pickerButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, SELECT_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==SELECT_CODE && resultCode == Activity.RESULT_OK){
            try {
                val imageUri: Uri? = data?.data
                binding.imageView.setImageURI(imageUri)
//                mat = Mat()
//                Utils.bitmapToMat(bitmap, mat)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}

