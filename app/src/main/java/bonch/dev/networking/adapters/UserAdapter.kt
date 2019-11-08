package bonch.dev.networking.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.networking.R
import bonch.dev.networking.models.Photo
import bonch.dev.networking.models.User
import com.bumptech.glide.Glide

class UserAdapter(val list: List<User>, val context: Context) :
    RecyclerView.Adapter<ItemUserHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemUserHolder {
        return ItemUserHolder(
            LayoutInflater.from(context).inflate(R.layout.item_user, parent, false)
        )
    }

    override fun getItemCount() = list.size


    override fun onBindViewHolder(holder: ItemUserHolder, position: Int) {

        holder.bind(list[position])
    }

}

class ItemUserHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val idTextView = itemView.findViewById<TextView>(R.id.id_text_view)
    private val nameTextView = itemView.findViewById<TextView>(R.id.name_text_view)
    private val usernameTextView = itemView.findViewById<TextView>(R.id.username_text_view)
    private val emailTextView = itemView.findViewById<TextView>(R.id.email_text_view)


    fun bind(user: User) {
        idTextView.text = "id: " + user.id.toString()
        nameTextView.text = "name: " + user.name
        usernameTextView.text = "username: " + user.username
        emailTextView.text = "email: " + user.email

    }
}