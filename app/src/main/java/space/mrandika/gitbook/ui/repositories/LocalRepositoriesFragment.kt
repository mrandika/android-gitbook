package space.mrandika.gitbook.ui.repositories

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.search.SearchBar
import space.mrandika.gitbook.R
import space.mrandika.gitbook.databinding.FragmentLocalRepositoriesBinding
import space.mrandika.gitbook.model.local.repository.RepositoryLocal
import space.mrandika.gitbook.ui.repositories.adapter.LocalRepositoriesAdapter
import space.mrandika.gitbook.viewmodel.ViewModelFactory
import space.mrandika.gitbook.viewmodel.repositories.RepositoriesViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LocalRepositoriesFragment: Fragment() {
    private var _binding: FragmentLocalRepositoriesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RepositoriesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLocalRepositoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.findViewById<SearchBar>(R.id.search_bar)?.hint = resources.getString(R.string.repo_search_hint)

        binding.localRepositoriesState.loading.loadingState.visibility = View.GONE
        binding.localRepositoriesState.error.errorState.visibility = View.GONE
        binding.localRepositoriesState.empty.emptyState.visibility = View.GONE

        viewModel = obtainViewModel(activity as AppCompatActivity)

        viewModel.getAllFavorites().observe(viewLifecycleOwner) { repositories ->
            setRepositoriesData(repositories)
        }

        val layoutManager = LinearLayoutManager(activity)
        binding.repositoriesRecyclerView.layoutManager = layoutManager

        val itemDecoration = DividerItemDecoration(activity, layoutManager.orientation)
        binding.repositoriesRecyclerView.addItemDecoration(itemDecoration)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

        viewModel.getAllFavorites().value?.let { repos -> setRepositoriesData(repos) }
    }

    private fun obtainViewModel(activity: AppCompatActivity): RepositoriesViewModel {
        val factory = ViewModelFactory.getInstance(activity.application, null)
        return ViewModelProvider(activity, factory)[RepositoriesViewModel::class.java]
    }

    private fun setRepositoriesData(repositories: List<RepositoryLocal>) {
        val adapter = LocalRepositoriesAdapter(requireContext())
        adapter.setRepositoriesList(repositories)
        binding.repositoriesRecyclerView.adapter = adapter

        if (repositories.isEmpty()) {
            binding.localRepositoriesState.empty.emptyState.visibility = View.VISIBLE
            binding.repositoriesRecyclerView.visibility = View.GONE
        }

        adapter.setOnItemClickCallback(object : LocalRepositoriesAdapter.OnItemClickCallback {
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