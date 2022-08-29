package com.pegahjadidi.todoapp.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.pegahjadidi.todoapp.R
import com.pegahjadidi.todoapp.data.model.ToDoData
import com.pegahjadidi.todoapp.data.viewModel.SharedViewModel
import com.pegahjadidi.todoapp.data.viewModel.ToDoViewModel
import com.pegahjadidi.todoapp.databinding.FragmentListBinding
import com.pegahjadidi.todoapp.fragments.list.adapter.ListAdapter
import com.pegahjadidi.todoapp.hideKeyBoard
import com.pegahjadidi.todoapp.observeOnce
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import java.util.*

class ListFragment: Fragment() , SearchView.OnQueryTextListener {
    private var _binding : FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var mToDoViewModel: ToDoViewModel
    private lateinit var mSharedViewModel: SharedViewModel
    private val adapter : ListAdapter by lazy { ListAdapter(mToDoViewModel) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        val view = binding.root

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.fragment_list_title)


        //viewModel
        mToDoViewModel = ViewModelProvider(requireActivity())[ToDoViewModel::class.java]
        //sharedViewModel
        mSharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        binding.lifecycleOwner = this
        binding.mSharedViewModel = mSharedViewModel

        //recyclerView
        recyclerViewInit()

        //hide software keyboard
        hideKeyBoard(requireActivity())

        //observing LiveData
        mToDoViewModel.getAllData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            data -> adapter.setData(data)
            mSharedViewModel.checkIfDataBaseEmpty(data)
        })





        //menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.list_fragment_menu, menu)
                val search = menu.findItem(R.id.menu_search)
                val searchView = search?.actionView as? SearchView
                searchView?.isSubmitButtonEnabled = true
                searchView?.setOnQueryTextListener(this@ListFragment)

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                when(menuItem.itemId ) {
                    R.id.menu_deleteAll -> confirmRemoval()
                    R.id.menu_priority_high -> mToDoViewModel.sortByHighPriority.observe(viewLifecycleOwner,
                        androidx.lifecycle.Observer { adapter.setData(it) })
                    R.id.menu_priority_low -> mToDoViewModel.sortByLowPriority.observe(viewLifecycleOwner,
                        androidx.lifecycle.Observer { adapter.setData(it) })
                }
                if(menuItem.itemId == R.id.menu_language) {
                    if(Locale.getDefault().language == "en"){
                        changeApplicationLanguage("fa")
                    }else {
                        changeApplicationLanguage("en")
                    }
                }

                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


        return view


    }

    private fun changeApplicationLanguage(language : String) {
        val locale : Locale = Locale(language)
        val res = resources
        val dm = res.displayMetrics
        val config = res.configuration
        config.locale = locale
        res.updateConfiguration(config,dm)
        ActivityCompat.recreate(requireActivity())
    }


    //this function sets up recycler view
    private fun recyclerViewInit() {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2,
            StaggeredGridLayoutManager.VERTICAL )
        swipeToDelete(binding.recyclerView)
        binding.recyclerView.itemAnimator = SlideInUpAnimator().apply {
            addDuration = 300
        }
    }

    //what happens when user swipe a card view
    private fun swipeToDelete(recyclerView : RecyclerView) {
        val swipeToDeleteCallBack = object : SwipeToDelete(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //delete card view when swiped
                val itemToDelete = adapter.todoDataLists[viewHolder.adapterPosition]
                mToDoViewModel.deleteData(itemToDelete)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)

                //restore deleted item with Undo snack bar
                restoreDeletedItem(viewHolder.itemView,itemToDelete,viewHolder.adapterPosition)

            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallBack)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun restoreDeletedItem(view: View, deletedItem : ToDoData, position : Int) {
        val snackBar = Snackbar.make(view,"Deleted '${deletedItem.title}'", Snackbar.LENGTH_LONG)
        snackBar.setAction("Undo") {
            mToDoViewModel.insertData(deletedItem)
//            adapter.notifyItemChanged(position) this line of code cause array index -1 error
        }
        snackBar.show()
    }


    //function for delete all data
    private fun confirmRemoval() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton(R.string.yes){ _, _ ->
            mToDoViewModel.deleteAllData()
            Toast.makeText(requireContext(),
                R.string.successfully_removed_all
                , Toast.LENGTH_SHORT).show()

            findNavController().navigate(R.id.action_listFragment_self)

        }
        builder.setNegativeButton(R.string.no) { _, _ ->}

        builder.setTitle(R.string.delete_everything)
        builder.setMessage(R.string.are_you_sure_you_want_to_delete_everything)
        builder.create().show()
    }


    //for search widget
    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query != null) {
            searchThroughDB(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if(query != null) {
            searchThroughDB(query)
        }
        return true
    }

    private fun searchThroughDB(query : String) {
        var searchQuery: String = query
        searchQuery = "%$searchQuery%"
        mToDoViewModel.searchDataBase(searchQuery)
            .observeOnce(viewLifecycleOwner, androidx.lifecycle.Observer { List ->
                List?.let {
                    adapter.setData(it)
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}