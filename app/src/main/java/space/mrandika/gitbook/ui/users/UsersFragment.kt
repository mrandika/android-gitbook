package space.mrandika.gitbook.ui.users

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.search.SearchBar
import space.mrandika.gitbook.R
import space.mrandika.gitbook.databinding.FragmentUsersBinding
import space.mrandika.gitbook.model.remote.user.User
import space.mrandika.gitbook.ui.users.adapter.UsersAdapter
import space.mrandika.gitbook.viewmodel.ViewModelFactory
import space.mrandika.gitbook.viewmodel.users.UsersViewModel


class UsersFragment: Fragment() {
    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: UsersAdapter
    private lateinit var viewModel: UsersViewModel

    companion object {
        /*
        A menu_type consist of following possible Int's:
        1 - Following
        2 - Follower
        else - All Users
         */
        const val TYPE = "menu_type"
        const val USERNAME = "user_login"

        fun new(type: Int?, username: String?): UsersFragment {
            val fragment = UsersFragment()
            val bundle = Bundle()

            if (type != null) {
                bundle.apply {
                    putInt(TYPE, type)
                }
            }

            if (!username.isNullOrEmpty()) {
                bundle.apply {
                    putString(USERNAME, username)
                }

                fragment.arguments = bundle
            }

            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val searchBar: SearchBar? = activity?.findViewById(R.id.search_bar)
        searchBar?.visibility = View.VISIBLE
        searchBar?.hint = resources.getString(R.string.user_search_hint)

        viewModel = obtainViewModel(activity as AppCompatActivity)

        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.isError.observe(viewLifecycleOwner) {
            showError(it)
        }

        viewModel.users.observe(viewLifecycleOwner) { users ->
            setUsersData(users)
        }

        viewModel.followings.observe(viewLifecycleOwner) { users ->
            setUsersData(users)
        }

        viewModel.followers.observe(viewLifecycleOwner) { users ->
            setUsersData(users)
        }

        // Get data
        getData()

        val layoutManager = LinearLayoutManager(activity)
        binding.usersRecyclerView.layoutManager = layoutManager

        val itemDecoration = DividerItemDecoration(activity, layoutManager.orientation)
        binding.usersRecyclerView.addItemDecoration(itemDecoration)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

        when (arguments?.getInt(TYPE) ?: 0) {
            1 -> viewModel.followings.value?.let { users -> setUsersData(users) }
            2 -> viewModel.followers.value?.let { users -> setUsersData(users) }
            else -> viewModel.users.value?.let { users -> setUsersData(users) }
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): UsersViewModel {
        val factory = ViewModelFactory.getInstance(activity.application, null)
        return ViewModelProvider(activity, factory)[UsersViewModel::class.java]
    }

    private fun getData() {
        val type = arguments?.getInt(TYPE) ?: 0
        val username = arguments?.getString(USERNAME)

        when (type) {
            1 -> username?.let { user -> viewModel.getFollowings(user) }
            2 -> username?.let { user -> viewModel.getFollowers(user) }
            else -> viewModel.getUsers()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.usersState.loading.loadingState.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.usersState.error.errorState.visibility = View.GONE
        binding.usersState.empty.emptyState.visibility = View.GONE

        binding.usersRecyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showError(isError: Boolean) {
        binding.usersState.error.errorState.visibility = if (isError) View.VISIBLE else View.GONE
        binding.usersState.loading.loadingState.visibility = View.GONE
        binding.usersState.empty.emptyState.visibility = View.GONE

        binding.usersRecyclerView.visibility = if (isError) View.GONE else View.VISIBLE
    }

    private fun setUsersData(users: List<User>) {
        adapter = UsersAdapter(users, requireContext())
        binding.usersRecyclerView.adapter = adapter

        if (users.isEmpty()) {
            binding.usersState.empty.emptyState.visibility = View.VISIBLE
            binding.usersRecyclerView.visibility = View.GONE
        }

        adapter.setOnItemClickCallback(object : UsersAdapter.OnItemClickCallback {
            override fun onItemClicked(username: String) {
                showDetail(username)
            }
        })
    }

    private fun showDetail(username: String) {
        val intent = Intent(activity, UserDetailActivity::class.java).apply {
            putExtra("username", username)
        }

        startActivity(intent)
    }
}