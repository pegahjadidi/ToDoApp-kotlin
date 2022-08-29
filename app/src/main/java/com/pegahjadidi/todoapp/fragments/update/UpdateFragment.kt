package com.pegahjadidi.todoapp.fragments.update

import android.app.AlertDialog
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
import androidx.navigation.fragment.navArgs
import com.pegahjadidi.todoapp.R
import com.pegahjadidi.todoapp.data.model.ToDoData
import com.pegahjadidi.todoapp.data.viewModel.SharedViewModel
import com.pegahjadidi.todoapp.data.viewModel.ToDoViewModel
import com.pegahjadidi.todoapp.databinding.FragmentUpdateBinding

class UpdateFragment : Fragment() {
    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!
    private lateinit var mToDoViewModel: ToDoViewModel
    private lateinit var mSharedViewModel: SharedViewModel
    private val args by navArgs<UpdateFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // dataBinding
        _binding= FragmentUpdateBinding.inflate(inflater,container,false)
        val view = binding.root

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.fragment_update_title)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)


        //viewModel init
        mToDoViewModel = ViewModelProvider(requireActivity())[ToDoViewModel::class.java]
        //SharedViewModel init
        mSharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        //spinner change color
        binding.updatePrioritySpinner.onItemSelectedListener = mSharedViewModel.listener


        //safeargs
        binding.updateTitleEt.setText(args.currentItem.title)
        binding.updateDescriptionEt.setText(args.currentItem.description)
        binding.updatePrioritySpinner.setSelection(mSharedViewModel.parsePriorityToInt(args.currentItem.priority))



        //menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.update_fragment_menu, menu)
            }


            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                when(menuItem.itemId ) {
                    R.id.menu_save ->updateItem()
                    R.id.menu_delete -> confirmItemRemovalItem()
                    android.R.id.home -> findNavController().navigate(R.id.action_updateFragment_to_listFragment)
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


        return view
    }




    private fun updateItem() {
        val updatedTitle = binding.updateTitleEt.text.toString()
        val updatedDescription = binding.updateDescriptionEt.text.toString()
        val updatedPriority = binding.updatePrioritySpinner.selectedItem.toString()
        val validation = mSharedViewModel.verifyDataFromUser(updatedTitle,updatedDescription)

        if (validation) {
            val updatedItem = ToDoData (
                args.currentItem.id ,
                updatedTitle ,
                mSharedViewModel.parsePriority(updatedPriority),
                updatedDescription,System.currentTimeMillis(),false)
            mToDoViewModel.updateData(updatedItem)
            Toast.makeText(requireContext(),"Successfully Updated !!", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_updateFragment_to_listFragment)
        } else {
            Toast.makeText(requireContext(),"please fill out all fields", Toast.LENGTH_SHORT).show()
        }
    }

    //show alert  dialogue to confirm removal
    private fun confirmItemRemovalItem() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes"){_,_ ->
            mToDoViewModel.deleteData(args.currentItem)
            Toast.makeText(requireContext(),
                "Successfully Removed ${args.currentItem.title} !!"
                , Toast.LENGTH_SHORT).show()

            findNavController().navigate(R.id.action_updateFragment_to_listFragment)

        }
        builder.setNegativeButton("No") {_,_ ->}

        builder.setTitle("Delete ${args.currentItem.title}?")
        builder.setMessage("Are you sure you want to remove \" ${args.currentItem.title} \"?")
        builder.create().show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}