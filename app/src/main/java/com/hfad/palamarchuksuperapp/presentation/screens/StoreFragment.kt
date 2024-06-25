package com.hfad.palamarchuksuperapp.presentation.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hfad.palamarchuksuperapp.appComponent
import com.hfad.palamarchuksuperapp.databinding.FragmentStoreBinding
import com.hfad.palamarchuksuperapp.domain.models.AppVibrator
import com.hfad.palamarchuksuperapp.presentation.viewModels.GenericViewModelFactory
import com.hfad.palamarchuksuperapp.presentation.viewModels.RepoResult
import com.hfad.palamarchuksuperapp.presentation.viewModels.StoreViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class StoreFragment : Fragment() {
    private var _binding: FragmentStoreBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "_binding = null"
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentStoreBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}