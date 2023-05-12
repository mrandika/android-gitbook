package space.mrandika.gitbook.ui.repositories

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import space.mrandika.gitbook.R
import space.mrandika.gitbook.databinding.ActivityRepositoryDetailBinding
import space.mrandika.gitbook.model.remote.repository.Commit
import space.mrandika.gitbook.model.remote.repository.RepositoryDetail
import space.mrandika.gitbook.ui.repositories.adapter.CommitsAdapter
import space.mrandika.gitbook.viewmodel.ViewModelFactory
import space.mrandika.gitbook.viewmodel.repositories.RepositoryViewModel

class RepositoryDetailActivity : AppCompatActivity() {
    /**
     * View binding
     */
    private lateinit var binding: ActivityRepositoryDetailBinding

    // View parameter
    private var username: String? = null
    private var repoName: String? = null
    private var repository: RepositoryDetail? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRepositoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel = obtainViewModel(this@RepositoryDetailActivity)

        val toolbar: Toolbar = binding.topAppBar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.isError.observe(this) {
            showError(it)
        }

        viewModel.repository.observe(this) { data ->
            setRepoData(data)

            viewModel.isRepoExist { result, _ ->
                checkIfUserIsFavorited(result)
            }
        }

        viewModel.commits.observe(this) { data ->
            setCommitsData(data)
        }

        // Get passed data
        getData()

        // Get detail data
        username?.let { login -> repoName?.let { repo -> viewModel.getRepository(login, repo) } }

        val layoutManager = LinearLayoutManager(this)
        binding.contentDetail.commitRecyclerView.layoutManager = layoutManager

        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.contentDetail.commitRecyclerView.addItemDecoration(itemDecoration)

        binding.contentDetail.commitRecyclerView.isNestedScrollingEnabled = true
        binding.favoriteUserFloatingActionButton.setOnClickListener {
            viewModel.onFavorited { result ->
                checkIfUserIsFavorited(result)

                this.runOnUiThread {
                    if (result) {
                        Toast.makeText(this, "Repository added to favorites", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "Repository removed from favorites", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setFabDrawable(drawable: Int) {
        binding.favoriteUserFloatingActionButton.setImageDrawable(ContextCompat.getDrawable(this@RepositoryDetailActivity, drawable))
    }

    private fun checkIfUserIsFavorited(result: Boolean) {
        this.runOnUiThread {
            when(result) {
                false -> {
                    setFabDrawable(R.drawable.ic_twotone_favorite_border_24)
                }

                true -> {
                    setFabDrawable(R.drawable.ic_twotone_favorite_24)
                }
            }
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity): RepositoryViewModel {
        val factory = ViewModelFactory.getInstance(activity.application, null)
        return ViewModelProvider(activity, factory)[RepositoryViewModel::class.java]
    }

    private fun getData() {
        username = intent.getStringExtra("username")
        repoName = intent.getStringExtra("repo_name")
    }

    private fun showLoading(isLoading: Boolean) {
        binding.repositoryState.loading.loadingState.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.repositoryState.error.errorState.visibility = View.GONE
        binding.repositoryState.empty.emptyState.visibility = View.GONE

        binding.favoriteUserFloatingActionButton.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.contentDetail.repoDetail.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.contentDetail.commitRecyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showError(isError: Boolean) {
        binding.repositoryState.error.errorState.visibility = if (isError) View.VISIBLE else View.GONE
        binding.repositoryState.loading.loadingState.visibility = View.GONE
        binding.repositoryState.empty.emptyState.visibility = View.GONE

        binding.favoriteUserFloatingActionButton.visibility = if (isError) View.GONE else View.VISIBLE
        binding.contentDetail.repoDetail.visibility = if (isError) View.GONE else View.VISIBLE
        binding.contentDetail.commitRecyclerView.visibility = if (isError) View.GONE else View.VISIBLE
    }

    private fun setRepoData(data: RepositoryDetail) {
        repository = data
        updateUI()
    }

    private fun setCommitsData(data: List<Commit>) {
        val adapter = CommitsAdapter(data)
        binding.contentDetail.commitRecyclerView.adapter = adapter
    }

    private fun updateUI() {
        Glide.with(this)
            .load(repository?.owner?.avatarUrl)
            .into(binding.contentDetail.repoProfile.userImageView)

        binding.contentDetail.repoProfile.repoNameTextView.text = repository?.name
        binding.contentDetail.repoProfile.userNameTextView.text = repository?.owner?.login
        binding.contentDetail.repoProfile.descriptionTextView.text = repository?.description
        binding.contentDetail.repoProfile.watcherTextView.text = "${repository?.watchers.toString()} watchers"
        binding.contentDetail.repoProfile.languageTextView.text = repository?.language
    }
}