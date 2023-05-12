package space.mrandika.gitbook.ui.repositories.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import space.mrandika.gitbook.R
import space.mrandika.gitbook.model.remote.repository.Repository

class RepositoriesAdapter(private val repositories: List<Repository>, private val context: Context) : RecyclerView.Adapter<RepositoriesAdapter.ViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_repository, viewGroup, false))

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(repositories[position].owner.avatarUrl)
            .into(viewHolder.image)

        viewHolder.userName.text = repositories[position].owner.login + "/"
        viewHolder.repositoryName.text = repositories[position].name
        viewHolder.repositoryDescription.text = repositories[position].description

        val repo = repositories[viewHolder.adapterPosition]

        viewHolder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(repo.owner.login, repo.name) }
    }

    override fun getItemCount() = repositories.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.name_textView)
        val repositoryName: TextView = view.findViewById(R.id.repositoryName_textView)
        val repositoryDescription: TextView = view.findViewById(R.id.repositoryDesc_textView)
        val image: ShapeableImageView = view.findViewById(R.id.user_imageView)
    }

    interface OnItemClickCallback {
        fun onItemClicked(username: String, repoName: String)
    }
}