package space.mrandika.gitbook

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import space.mrandika.gitbook.databinding.ActivityMainBinding
import space.mrandika.gitbook.prefs.setting.SettingPreferences
import space.mrandika.gitbook.ui.setting.SettingActivity
import space.mrandika.gitbook.ui.users.UserDetailActivity
import space.mrandika.gitbook.viewmodel.ViewModelFactory
import space.mrandika.gitbook.viewmodel.repositories.RepositoriesViewModel
import space.mrandika.gitbook.viewmodel.setting.SettingViewModel
import space.mrandika.gitbook.viewmodel.users.UsersViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    /**
     * ViewBinding
     */
    private lateinit var binding: ActivityMainBinding

    private val usersViewModel: UsersViewModel by viewModels()
    private val repositoriesViewModel: RepositoriesViewModel by viewModels()
    private lateinit var settingViewModel: SettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingViewModel = obtainViewModel(this@MainActivity, dataStore)

        settingViewModel.getThemeSettings().observe(this) { themeMode ->
            applyTheme(themeMode)
        }

        setSupportActionBar(binding.toolbar)
        binding.searchView.setupWithSearchBar(binding.searchBar)
        binding.searchBar.inflateMenu(R.menu.searchbar_menu)
        binding.searchBar.setOnMenuItemClickListener {
            showBottomMenu()

            return@setOnMenuItemClickListener true
        }

        val navView: BottomNavigationView = binding.bottomNavigationBar

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.users_fragment, R.id.repositories_fragment, R.id.favorites_fragment
        ).build()

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            binding.searchBar.text = binding.searchView.text
            binding.searchView.hide()

            val fragmentId = navController.currentDestination?.id
            val query = binding.searchBar.text.toString()

            when(fragmentId) {
                R.id.users_fragment -> {
                    if (query.isEmpty()) {
                        usersViewModel.getUsers()
                    } else {
                        usersViewModel.searchUsers(query)
                    }
                }

                R.id.repositories_fragment -> {
                    if (query.isEmpty()) {
                        repositoriesViewModel.getRepositories()
                    } else {
                        repositoriesViewModel.searchRepositories(query)
                    }
                }
            }

            return@setOnEditorActionListener false
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity, dataStore: DataStore<Preferences>): SettingViewModel {
        val factory = ViewModelFactory.getInstance(activity.application, SettingPreferences.getInstance(dataStore))
        return ViewModelProvider(activity, factory)[SettingViewModel::class.java]
    }

    private fun applyTheme(theme: Int) {
        val currentTheme = AppCompatDelegate.getDefaultNightMode()

        if (currentTheme != theme) {
            settingViewModel.saveThemeSetting(theme)
        }
    }

    private fun showBottomMenu() {
        val bottomSheetDialog = BottomSheetDialog(this@MainActivity)
        bottomSheetDialog.setContentView(R.layout.item_bottom_menu)

        val accountMenu = bottomSheetDialog.findViewById<LinearLayout>(R.id.account_menu)
        val settingMenu = bottomSheetDialog.findViewById<LinearLayout>(R.id.setting_menu)

        bottomSheetDialog.show()

        accountMenu?.setOnClickListener {
            bottomSheetDialog.dismiss()
            showAccountDetail("mrandika")
        }

        settingMenu?.setOnClickListener {
            bottomSheetDialog.dismiss()
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showAccountDetail(username: String) {
        val intent = Intent(this, UserDetailActivity::class.java).apply {
            putExtra("username", username)
        }

        startActivity(intent)
    }
}