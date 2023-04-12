package com.gyanHub.mymusic.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyanHub.mymusic.viewModel.MyMusicViewModel
import com.gyanHub.mymusic.R
import com.gyanHub.mymusic.adapter.FolderAdapter
import com.gyanHub.mymusic.adapter.FolderClick
import com.gyanHub.mymusic.databinding.FragmentFolderBinding
import com.gyanHub.mymusic.viewModel.ShareDataViewModel
import java.io.File

class FolderFragment : Fragment(), FolderClick {
    private var _binding: FragmentFolderBinding? = null
    private val binding get() = _binding!!
    private lateinit var backPressCallback: OnBackPressedCallback
    private lateinit var musicViewModel: MyMusicViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFolderBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        musicViewModel = ViewModelProvider(requireActivity())[MyMusicViewModel::class.java]
        musicViewModel.listOfFolder.observe(viewLifecycleOwner) {
            setFolder(it)
        }
        binding.txtFolderLocatation.text = getString(R.string.folderLocation, "")

        binding.txtFolderLocatation.setOnClickListener {
            backPressCallback.handleOnBackPressed()
        }
        backPressCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.rcFolder.visibility == View.GONE) {
                    binding.rcFolder.visibility = View.VISIBLE
                    binding.fragmentHolder.removeAllViews()
                    binding.fragmentHolder.visibility = View.GONE
                    binding.txtFolderLocatation.text = getString(R.string.folderLocation, "")
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressCallback)


    }

    private fun setFolder(list: Set<String>) {
        binding.rcFolder.layoutManager = LinearLayoutManager(context)
        binding.rcFolder.adapter = FolderAdapter(list, this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        backPressCallback.remove()
    }

    override fun onClick(filePath: String) {
        binding.rcFolder.visibility = View.GONE
        val file = File(filePath)
        musicViewModel.getMusicFromFolder(filePath)
        binding.txtFolderLocatation.text =
            getString(R.string.folderLocation, ">> " + file.parentFile?.name)
        setFragment(FolderMusicFragment(),filePath)
    }

    private fun setFragment(fragment: Fragment, path: String) {
        binding.fragmentHolder.visibility = View.VISIBLE

        val bundle = Bundle().apply {
            putString("FilePath", path)
        }
        fragment.arguments = bundle
        val fragmentTransient = childFragmentManager.beginTransaction()
        fragmentTransient.replace(R.id.fragmentHolder, fragment)
        fragmentTransient.commit()
    }
}