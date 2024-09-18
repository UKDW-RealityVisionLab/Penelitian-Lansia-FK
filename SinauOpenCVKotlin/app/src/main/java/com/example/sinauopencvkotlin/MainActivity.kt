package com.example.sinauopencvkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.opencv.android.OpenCVLoader

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(OpenCVLoader.initLocal()) Log.d("LOADED", "Success")
        else Log.d("LOADED", "Failed")
    }
}