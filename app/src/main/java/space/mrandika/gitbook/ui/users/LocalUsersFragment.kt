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
import space.mrandika.gitbook.databinding.FragmentLocalUsersBinding
import space.mrandika.gitbook.model.local.user.UserLocal
import space.mrandika.gitbook.ui.users.adapter.LocalUsersAdapter
import space.mrandika.gitbook.viewmodel.ViewModelFactory
import space.mrandika.gitbook.viewmodel.users.UsersViewModel

class LocalUsersFragment: Fragment() {
    private var _binding: FragmentLocalUsersBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: UsersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLocalUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.findViewById<SearchBar>(R.id.search_bar)?.hint = resources.getString(R.string.user_search_hint)

        binding.localUsersState.loading.loadingState.visibility = View.GONE
        binding.localUsersState.error.errorState.visibility = View.GONE
        binding.localUsersState.empty.emptyState.visibility = View.GONE

        viewModel = obtainViewModel(activity as AppCompatActivity)

        // Get data
        viewModel.getAllFavorites().observe(viewLifecycleOwner) { users ->
            setUsersData(users)
        }

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

        viewModel.getAllFavorites().value?.let { users -> setUsersData(users) }
    }

    private fun obtainViewModel(activity: AppCompatActivity): UsersViewModel {
        val factory = ViewModelFactory.getInstance(activity.application, null)
        return ViewModelProvider(activity, factory)[UsersViewModel::class.java]
    }

    private fun setUsersData(users: List<UserLocal>) {
        val adapter = LocalUsersAdapter(requireContext())
        adapter.setUsersList(users)
        binding.usersRecyclerView.adapter = adapter

        if (users.isEmpty()) {
            binding.localUsersState.empty.emptyState.visibility = View.VISIBLE
            binding.usersRecyclerView.visibility = View.GONE
        }

        adapter.setOnItemClickCallback(object : LocalUsersAdapter.OnItemClickCallback {
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