package bonch.dev.networking.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.networking.R
import bonch.dev.networking.models.Album
import bonch.dev.networking.networking.RetrofitFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class AlbumAdapter(val list: MutableList<Album>, val context: Context) :
    RecyclerView.Adapter<AlbumAdapter.ItemAlbumHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemAlbumHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_album, parent, false)
        return ItemAlbumHolder(view)
    }

    override fun getItemCount() = list.size


    override fun onBindViewHolder(holder: ItemAlbumHolder, position: Int) {
        holder.bind(list[position])
    }


    inner class ItemAlbumHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        private val userIdTextView = itemView.findViewById<TextView>(R.id.userId_text_view)
        private val idTextView = itemView.findViewById<TextView>(R.id.id_text_view)
        private val titleTextView = itemView.findViewById<TextView>(R.id.title_text_view)


        fun bind(album: Album) {
            itemView.setOnClickListener(this)
            userIdTextView.text = "userId: " + album.userId.toString()
            idTextView.text = "id: " + album.id.toString()
            titleTextView.text = "title: "+ album.title
        }


        override fun onClick(v: View?) {
            val service = RetrofitFactory.makeRetrofitService()

            CoroutineScope(Dispatchers.IO).launch {
                val response = service.deleteAlbum(adapterPosition)
                try {
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            removeItem()
                            Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (err: HttpException) {
                    Log.e("Retrofit", "${err.printStackTrace()}")
                }
            }
        }

        private fun removeItem() {
            list.removeAt(adapterPosition)
            notifyItemRemoved(adapterPosition)
            notifyItemRangeChanged(adapterPosition, list.size)
        }


    }

}

