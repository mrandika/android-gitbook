package space.mrandika.gitbook.ui.users.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import space.mrandika.gitbook.R
import space.mrandika.gitbook.model.local.user.UserLocal

class LocalUsersAdapter(private val context: Context) : RecyclerView.Adapter<LocalUsersAdapter.ViewHolder>() {
    private val users = ArrayList<UserLocal>()
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    fun setUsersList(users: List<UserLocal>) {
        this.users.clear()
        this.users.addAll(users)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_user, viewGroup, false))

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(users[position].avatarUrl)
            .into(viewHolder.image)

        viewHolder.userName.text = users[position].login

        viewHolder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(users[viewHolder.adapterPosition].login) }
    }

    override fun getItemCount() = users.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.name_textView)
        val image: ShapeableImageView = view.findViewById(R.id.user_imageView)
    }

    interface OnItemClickCallback {
        fun onItemClicked(username: String)
    }
}