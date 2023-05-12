package space.mrandika.gitbook.ui.repositories.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import space.mrandika.gitbook.R
import space.mrandika.gitbook.model.remote.repository.Commit

class CommitsAdapter(private val commits: List<Commit>) : RecyclerView.Adapter<CommitsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_commit, viewGroup, false))

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.userName.text = commits[position].detail?.author?.name
        viewHolder.userEmail.text = commits[position].detail?.author?.email
        viewHolder.commitMessage.text = commits[position].detail?.message
    }

    override fun getItemCount() = commits.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.name_textView)
        val userEmail: TextView = view.findViewById(R.id.email_textView)
        val commitMessage: TextView = view.findViewById(R.id.commitMessage_textView)
    }
}