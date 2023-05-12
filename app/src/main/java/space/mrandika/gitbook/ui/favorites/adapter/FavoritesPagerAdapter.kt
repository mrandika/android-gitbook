package space.mrandika.gitbook.ui.favorites.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import space.mrandika.gitbook.ui.repositories.LocalRepositoriesFragment
import space.mrandika.gitbook.ui.users.LocalUsersFragment

class FavoritesPagerAdapter(activity: AppCompatActivity): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null

        when (position) {
            0 -> fragment = LocalUsersFragment()
            1 -> fragment = LocalRepositoriesFragment()
        }

        return fragment as Fragment
    }

}