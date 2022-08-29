package com.pegahjadidi.todoapp.fragments.add

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.pegahjadidi.todoapp.R
import com.pegahjadidi.todoapp.data.model.ToDoData
import com.pegahjadidi.todoapp.data.viewModel.SharedViewModel
import com.pegahjadidi.todoapp.data.viewModel.ToDoViewModel
import com.pegahjadidi.todoapp.databinding.FragmentAddBinding

class AddFragment : Fragment() {
    private lateinit var binding : FragmentAddBinding
    private lateinit var mToDoViewModel: ToDoViewModel
    private lateinit var mSharedViewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View { binding = FragmentAddBinding.inflate(layoutInflater)
        val view = binding.root

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.fragment_add_title)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)



        //viewModel init
        mToDoViewModel = ViewModelProvider(requireActivity())[ToDoViewModel::class.java]
        //SharedViewModel init
        mSharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        //spinner change color
        binding.prioritySpinner.onItemSelectedListener = mSharedViewModel.listener



        //menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.add_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                when(menuItem.itemId){
                    R.id.menu_add ->  insertDataToDB()
                    android.R.id.home -> findNavController().navigate(R.id.action_addFragment_to_listFragment)
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        return view
    }






    private fun insertDataToDB() {
        val mTitle = binding.titleEt.text.toString()
        val mPriority = binding.prioritySpinner.selectedItem.toString()
        val mDescription = binding.descriptionEt.text.toString()
        val validation = mSharedViewModel.verifyDataFromUser(mTitle,mDescription)

        if (validation) {
            val newToDoData = ToDoData (
                0,
                mTitle,
                mSharedViewModel.parsePriority(mPriority),
                mDescription ,
                System.currentTimeMillis(),false
            )
            mToDoViewModel.insertData(newToDoData)
            Toast.makeText(requireContext(),"successfully added", Toast.LENGTH_SHORT).show()
            //navigates user back to list fragment
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        } else {
            Toast.makeText(requireContext(),"Please fill out all fields", Toast.LENGTH_SHORT).show()
        }

    }

}