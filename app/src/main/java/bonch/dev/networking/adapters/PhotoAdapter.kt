package bonch.dev.networking.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import bonch.dev.networking.R
import bonch.dev.networking.models.Photo
import com.bumptech.glide.Glide

class PhotoAdapter(val list: MutableList<Bitmap>, val context: Context) : RecyclerView.Adapter<ItemPhotoHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemPhotoHolder {
       return ItemPhotoHolder(
           LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false)
       )
    }

    override fun getItemCount() = list.size


    override fun onBindViewHolder(holder: ItemPhotoHolder, position: Int) {

        holder.bind(list[position])
    }

}

class ItemPhotoHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
    private val imageView = itemView.findViewById<ImageView>(R.id.image_view)


    fun bind (bitmap: Bitmap){
        //val drawable = Glide.with(itemView).asDrawable().load(photo.url).submit().get()
        imageView.setImageBitmap(bitmap)
    }
}