package com.example.sinauopencvkotlin

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.sinauopencvkotlin.fragments.CameraFragment
import com.example.sinauopencvkotlin.fragments.GalleryFragment

class CameraGalleryFragmentAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return if(position == 0) GalleryFragment()
        else CameraFragment()

    }


}