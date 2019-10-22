package com.inexture.baseproject.ui


import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import com.inexture.baseproject.BR
import com.inexture.baseproject.R
import com.inexture.baseproject.databinding.FragmentHomeBinding
import com.inexture.baseproject.model.Repo
import com.inexture.baseproject.network.resources.Resource
import com.inexture.baseproject.network.resources.Success
import com.livinglifetechway.k4kotlin.core.TAG
import com.livinglifetechway.k4kotlin.core.androidx.hideKeyboard
import com.livinglifetechway.k4kotlin.core.onClick

/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : Fragment() {

    private lateinit var mBinding: FragmentHomeBinding
    private lateinit var mHomeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mHomeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        mBinding.lifecycleOwner = viewLifecycleOwner
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()
    }

    private fun initialize() {

//        initSearchInputListener()
//
//        doSearch()

        // set resource for progress bar state and empty view state
//        @Suppress("UNCHECKED_CAST")
//        mBinding.resource = mHomeViewModel.results as LiveData<Resource<Any>>

        /*mBinding.setRetryCallBack {
            doSearch() // retry search
        }*/

        val diffCallback = object : DiffUtil.ItemCallback<Repo>() {
            override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean {
                return oldItem == newItem
            }
        }

        mHomeViewModel.getUsers().observe(viewLifecycleOwner, Observer {
            if (it.state == Resource.State.SUCCESS) {
                Log.d(TAG, "initialize: ${it.data?.data?.toString()}")
            }
        })


        /*val repoAdapter = RepoListAdapter(diffCallback)

        mBinding.repoList.adapter = repoAdapter

        mHomeViewModel.repoList.observe(viewLifecycleOwner, Observer { repoAdapter.submitList(it) })*/

        /*mBinding.btnRefresh.onClick {
            //            mHomeViewModel.refreshRepos()
        }*/
    }

    /*private fun initSearchInputListener() {
        mBinding.input.setOnEditorActionListener { view: View, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch()
                true
            } else {
                false
            }
        }
        mBinding.input.setOnKeyListener { view: View, keyCode: Int, event: KeyEvent ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                doSearch()
                true
            } else {
                false
            }
        }
    }*/

    /*private fun doSearch() {
        val query = mBinding.input.text.toString()
        // Dismiss keyboard
        hideKeyboard()
//        mHomeViewModel.setQuery(query)
    }*/
}
