package space.mrandika.gitbook.ui.users

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import space.mrandika.gitbook.R
import space.mrandika.gitbook.databinding.ActivityUserDetailBinding
import space.mrandika.gitbook.model.remote.user.UserDetail
import space.mrandika.gitbook.ui.users.adapter.UserConnectionsPagerAdapter
import space.mrandika.gitbook.viewmodel.ViewModelFactory
import space.mrandika.gitbook.viewmodel.users.UserViewModel


class UserDetailActivity : AppCompatActivity() {
    /**
     * ViewBinding
     */
    private lateinit var binding: ActivityUserDetailBinding

    // View parameter
    private var username: String? = null
    private var user: UserDetail? = null

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.following,
            R.string.follower
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel = obtainViewModel(this@UserDetailActivity)

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

        viewModel.user.observe(this) { data ->
            setUserData(data)

            viewModel.isUserExist { result, _ ->
                checkIfUserIsFavorited(result)
            }
        }

        // Get passed data
        getUsername()

        // Setup viewpager
        setupPager()

        // Get detail data
        username?.let { viewModel.getUserDetail(it) }

        binding.favoriteUserFloatingActionButton.setOnClickListener {
            viewModel.onFavorited { result ->
                checkIfUserIsFavorited(result)

                this.runOnUiThread {
                    if (result) {
                        Toast.makeText(this, "User added to favorites", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "User removed from favorites", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setFabDrawable(drawable: Int) {
        binding.favoriteUserFloatingActionButton.setImageDrawable(ContextCompat.getDrawable(this@UserDetailActivity, drawable))
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

    private fun obtainViewModel(activity: AppCompatActivity): UserViewModel {
        val factory = ViewModelFactory.getInstance(activity.application, null)
        return ViewModelProvider(activity, factory)[UserViewModel::class.java]
    }

    private fun getUsername() {
        username = intent.getStringExtra("username")
    }

    private fun showLoading(isLoading: Boolean) {
        binding.userState.loading.loadingState.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.userState.error.errorState.visibility = View.GONE
        binding.userState.empty.emptyState.visibility = View.GONE

        binding.contentDetail.userProfile.userProfileLayout.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.contentDetail.connectionTabs.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.contentDetail.connectionPager.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.favoriteUserFloatingActionButton.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showError(isError: Boolean) {
        binding.userState.error.errorState.visibility = if (isError) View.VISIBLE else View.GONE
        binding.userState.loading.loadingState.visibility = View.GONE
        binding.userState.empty.emptyState.visibility = View.GONE

        binding.contentDetail.userProfile.userProfileLayout.visibility = if (isError) View.GONE else View.VISIBLE
        binding.contentDetail.connectionTabs.visibility = if (isError) View.GONE else View.VISIBLE
        binding.contentDetail.connectionPager.visibility = if (isError) View.GONE else View.VISIBLE
        binding.favoriteUserFloatingActionButton.visibility = if (isError) View.GONE else View.VISIBLE
    }

    private fun setupPager() {
        val userConnectionsPagerAdapter = username?.let { UserConnectionsPagerAdapter(this, it) }

        val viewPager: ViewPager2 = binding.contentDetail.connectionPager
        viewPager.adapter = userConnectionsPagerAdapter

        val tabs: TabLayout = binding.contentDetail.connectionTabs

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }

    private fun setUserData(data: UserDetail) {
        user = data
        updateUI()
    }

    private fun updateUI() {
        Glide.with(this)
            .load(user?.avatarUrl)
            .into(binding.contentDetail.userProfile.userImageView)

        binding.contentDetail.userProfile.nameTextView.text = if (user?.name.isNullOrEmpty()) "Github User" else user?.name
        binding.contentDetail.userProfile.userNameTextView.text = user?.login
        binding.contentDetail.userProfile.companyTextView.text = if (user?.company.isNullOrEmpty()) "No Company Info" else user?.company

        binding.contentDetail.userProfile.userConnection.locationTextView.text = if (user?.location.isNullOrEmpty()) "Not Available" else user?.location
        binding.contentDetail.userProfile.userConnection.emailTextView.text = if (user?.email.isNullOrEmpty()) "Not Available" else user?.email
        binding.contentDetail.userProfile.userConnection.repoTextView.text = "${user?.publicRepos.toString()} repositories"
        binding.contentDetail.userProfile.userConnection.connectionTextView.text = "${user?.following} Following, ${user?.followers} Followers"

        binding.contentDetail.userProfile.bioTextView.text = if (user?.bio == null) "This user haven't write their bio yet." else user?.bio
    }
}