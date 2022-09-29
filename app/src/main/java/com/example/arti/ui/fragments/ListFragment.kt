package com.example.arti.ui.fragments

import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.arti.R
import com.example.arti.databinding.ListFragmentBinding
import com.example.arti.ui.adapters.BooksListAdapter
import com.example.arti.ui.viewmodel.BooksApiStatus
import com.example.arti.ui.viewmodel.BooksViewModel
import com.example.arti.ui.viewmodel.BooksViewModelFactory


class ListFragment: Fragment() {
    private lateinit var binding: ListFragmentBinding
    private lateinit var recyclerView: RecyclerView
    private var isLinearLayoutManager = true // Keeps track of which LayoutManager is in use

    private val sharedViewModel: BooksViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, BooksViewModelFactory(activity.application))
            .get(BooksViewModel::class.java)
    }
    /*private val sharedViewModel: BooksViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(this, BooksViewModel.Factory(activity.application))
            .get(BooksViewModel::class.java)
        *//*BooksViewModelFactory(
            (activity?.application as BaseApplication).database.booksDao()
        )*//*
    }*/


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ListFragmentBinding.inflate(inflater, container, false)
        showLoadingImage()
        //setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()

        recyclerView = binding.recyclerView
        //chooseLayout()
        //recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        val adapter = BooksListAdapter { currentBook ->
            sharedViewModel.updateCurrentBook(currentBook) }
        recyclerView.adapter = adapter
        // observe the list of books from the view model and submit it the adapter

        sharedViewModel.books.observe(this.viewLifecycleOwner) { books ->
            books.let {
                adapter.submitList(it)
            }
        }

/*        sharedViewModel.status.observe(viewLifecycleOwner) { status ->
                sharedViewModel.updateCurrentStatus(status)
                showLoadingImage()
        }*/
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.menu_layout, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                when (menuItem.itemId) {
                    R.id.action_switch_layout -> {
                        // Sets isLinearLayoutManager (a Boolean) to the opposite value
                        isLinearLayoutManager = !isLinearLayoutManager
                        // Sets layout and icon
                        chooseLayout()
                        setIcon(menuItem)
                    }
                }
            return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

    }

    private fun showLoadingImage() {
        when(sharedViewModel.status.value) {
            BooksApiStatus.LOADING -> {
                binding.statusImage.visibility = VISIBLE
                binding.statusImage.setImageResource(R.drawable.loading_animation)
            }
            BooksApiStatus.DONE -> {
                binding.statusImage.visibility = GONE
            }
            BooksApiStatus.ERROR -> {
                binding.statusImage.visibility = VISIBLE
                binding.statusImage.setImageResource(R.drawable.ic_connection_error)
            }
            else -> {
                binding.statusImage.visibility = GONE
            }
        }
    }

    private fun setIcon(menuItem: MenuItem?) {
        if (menuItem == null)
            return
        menuItem.icon =
            if (isLinearLayoutManager)
                ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_baseline_view_module_24)
            else ContextCompat.getDrawable(this.requireContext(), R.drawable.ic_baseline_view_list_24)
    }

    /**
     * Sets the LayoutManager for the [RecyclerView] based on the desired orientation of the list.
     */
    private fun chooseLayout() {
        if (isLinearLayoutManager) {
            recyclerView.layoutManager = LinearLayoutManager(context)
        } else {
            recyclerView.layoutManager = GridLayoutManager(context, 2)
        }
    }

}


