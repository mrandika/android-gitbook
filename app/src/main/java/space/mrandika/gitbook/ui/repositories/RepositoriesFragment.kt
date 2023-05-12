package space.mrandika.gitbook.ui.repositories

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
import space.mrandika.gitbook.databinding.FragmentRepositoriesBinding
import space.mrandika.gitbook.model.remote.repository.Repository
import space.mrandika.gitbook.ui.repositories.adapter.RepositoriesAdapter
import space.mrandika.gitbook.viewmodel.ViewModelFactory
import space.mrandika.gitbook.viewmodel.repositories.RepositoriesViewModel

class RepositoriesFragment: Fragment() {
    private var _binding: FragmentRepositoriesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRepositoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val searchBar: SearchBar? = activity?.findViewById(R.id.search_bar)
        searchBar?.visibility = View.VISIBLE
        searchBar?.hint = resources.getString(R.string.repo_search_hint)

        val viewModel = obtainViewModel(activity as AppCompatActivity)

        viewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        viewModel.isError.observe(viewLifecycleOwner) {
            showError(it)
        }

        viewModel.repositories.observe(viewLifecycleOwner) { repositories ->
            setRepositoriesData(repositories)
        }

        // Get the data
        viewModel.getRepositories()

        val layoutManager = LinearLayoutManager(activity)
        binding.repositoriesRecyclerView.layoutManager = layoutManager

        val itemDecoration = DividerItemDecoration(activity, layoutManager.orientation)
        binding.repositoriesRecyclerView.addItemDecoration(itemDecoration)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun obtainViewModel(activity: AppCompatActivity): RepositoriesViewModel {
        val factory = ViewModelFactory.getInstance(activity.application, null)
        return ViewModelProvider(activity, factory)[RepositoriesViewModel::class.java]
    }

    private fun showLoading(isLoading: Boolean) {
        binding.repositoriesState.loading.loadingState.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.repositoriesState.error.errorState.visibility = View.GONE
        binding.repositoriesState.empty.emptyState.visibility = View.GONE

        binding.repositoriesRecyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showError(isError: Boolean) {
        binding.repositoriesState.error.errorState.visibility = if (isError) View.VISIBLE else View.GONE
        binding.repositoriesState.loading.loadingState.visibility = View.GONE
        binding.repositoriesState.empty.emptyState.visibility = View.GONE

        binding.repositoriesRecyclerView.visibility = if (isError) View.GONE else View.VISIBLE
    }

    private fun setRepositoriesData(repositories: List<Repository>) {
        val adapter = RepositoriesAdapter(repositories, requireContext())
        binding.repositoriesRecyclerView.adapter = adapter

        if (repositories.isEmpty()) {
            binding.repositoriesState.empty.emptyState.visibility = View.VISIBLE
            binding.repositoriesRecyclerView.visibility = View.GONE
        }

        adapter.setOnItemClickCallback(object : RepositoriesAdapter.OnItemClickCallback {
            override fun onItemClicked(username: String, repoName: String) {
                showDetail(username, repoName)
            }
        })
    }

    private fun showDetail(username: String, repoName: String) {
        val intent = Intent(activity, RepositoryDetailActivity::class.java).apply {
            putExtra("username", username)
            putExtra("repo_name", repoName)
        }

        startActivity(intent)
    }
}