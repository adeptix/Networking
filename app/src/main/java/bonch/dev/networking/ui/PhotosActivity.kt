package bonch.dev.networking.ui

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.networking.R
import bonch.dev.networking.adapters.PhotoAdapter
import bonch.dev.networking.networking.RetrofitFactory
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.recycler_layout.*
import kotlinx.coroutines.*
import retrofit2.HttpException
import android.R.attr.bitmap
import android.graphics.Bitmap
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import androidx.annotation.Nullable
import bonch.dev.networking.models.SavedPhoto
import com.bumptech.glide.request.target.CustomTarget
import io.realm.Realm
import io.realm.RealmConfiguration


class PhotosActivity : AppCompatActivity() {

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recycler_layout)

        supportActionBar!!.title = "Photos"

        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("MyDatabase.realm")
            .build()
        realm = Realm.getInstance(config)

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.addItemDecoration(
            DividerItemDecoration(
                this@PhotosActivity,
                DividerItemDecoration.VERTICAL
            )
        )

        val service = RetrofitFactory.makeRetrofitService()

        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getPhotos()

            try {
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val list : MutableList<Bitmap> = arrayListOf()
                            response.body()!!.forEach{
                                glideDownload(it.url, list)
                            }
                            try{
                                withContext(Dispatchers.Main) {
                                    recycler.adapter = PhotoAdapter(list, this@PhotosActivity)
                                  //  saveData(list)
                                }
                            }catch (err: HttpException) {
                                Log.e("Retrofit", "${err.printStackTrace()}")
                            }

                        }


                    } else {
                        Toast.makeText(applicationContext, "${response.code()}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } catch (err: HttpException) {
                Log.e("Retrofit", "${err.printStackTrace()}")
            }
        }


    }

    suspend fun glideDownload(url : String, list : MutableList<Bitmap>){
        list.add(Glide.with(this).asBitmap().load(url).submit().get())
    }

//    fun saveData(list : List<Bitmap>) {
//        realm.executeTransactionAsync({ bgRealm ->
//
//            val sPhoto = bgRealm.createObject(SavedPhoto::class.java)
//             sPhoto.array = list
//
//        }, {
//            //success
//            Toast.makeText(applicationContext, "success", Toast.LENGTH_SHORT).show()
//        }, {
//            //fail
//
//            Log.i("ADEPT", "failed save ${it.message}")
//        })
//
//
//    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

}

