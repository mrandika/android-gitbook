package space.mrandika.gitbook.ui.setting

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import space.mrandika.gitbook.R
import space.mrandika.gitbook.databinding.ActivitySettingBinding
import space.mrandika.gitbook.prefs.setting.SettingPreferences
import space.mrandika.gitbook.viewmodel.ViewModelFactory
import space.mrandika.gitbook.viewmodel.setting.SettingViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingActivity : AppCompatActivity() {
    /**
     * ViewBinding
     */
    private lateinit var binding: ActivitySettingBinding

    private lateinit var viewModel: SettingViewModel
    private lateinit var themeSelection: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.topAppBar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = obtainViewModel(this@SettingActivity, dataStore)

        viewModel.getThemeSettings().observe(this) { themeMode ->
            setCheckedItem(themeMode)
        }

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.content.themeSetting.setOnClickListener {
            showAlertDialog()
        }
    }

    private fun obtainViewModel(activity: AppCompatActivity, dataStore: DataStore<Preferences>): SettingViewModel {
        val factory = ViewModelFactory.getInstance(activity.application, SettingPreferences.getInstance(dataStore))
        return ViewModelProvider(activity, factory)[SettingViewModel::class.java]
    }

    private fun setCheckedItem(value: Int) {
        themeSelection = when (value) {
            AppCompatDelegate.MODE_NIGHT_NO -> getString(R.string.theme_light)
            AppCompatDelegate.MODE_NIGHT_YES -> getString(R.string.theme_dark)
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> getString(R.string.theme_default)
            else -> "Unknown"
        }

        binding.content.themeValueTextView.text = themeSelection
    }

    private fun showAlertDialog() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@SettingActivity)
        alertDialog.setTitle(getString(R.string.theme))

        val items = arrayOf(getString(R.string.theme_light), getString(R.string.theme_dark), getString(R.string.theme_default))
        val checkedItem = if (themeSelection == getString(R.string.theme_light)) 0 else if (themeSelection == getString(R.string.theme_dark)) 1 else 2

        alertDialog.setSingleChoiceItems(items, checkedItem) { dialog, which ->
            when (which) {
                0 -> viewModel.saveThemeSetting(AppCompatDelegate.MODE_NIGHT_NO)
                1 -> viewModel.saveThemeSetting(AppCompatDelegate.MODE_NIGHT_YES)
                2 -> viewModel.saveThemeSetting(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }

            dialog.dismiss()
        }

        alertDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val alert: AlertDialog = alertDialog.create()
        alert.show()
    }
}