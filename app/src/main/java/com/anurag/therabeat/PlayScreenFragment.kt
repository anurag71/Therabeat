package com.anurag.therabeat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.anurag.therabeat.databinding.FragmentVideoBinding

class PlayScreenFragment: Fragment() {
    companion object {
        const val TAG = "PlayScreenFragment"
        fun newInstance(): PlayScreenFragment {
            val args = Bundle()
            val playScreenFragment = PlayScreenFragment()
            playScreenFragment.arguments = args
            return playScreenFragment
        }
    }
    private val binding get() = _binding!!

    private var _binding: FragmentVideoBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
}