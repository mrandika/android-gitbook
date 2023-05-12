package space.mrandika.gitbook.ui.users.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import space.mrandika.gitbook.ui.users.UsersFragment

class UserConnectionsPagerAdapter(activity: AppCompatActivity, val username: String): FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null

        when (position) {
            0 -> fragment = UsersFragment.new(1, username)
            1 -> fragment = UsersFragment.new(2, username)
        }

        return fragment as Fragment
    }

}