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
import bonch.dev.networking.InternetCheck
import bonch.dev.networking.models.Photo
import bonch.dev.networking.models.SavedPhoto
import com.bumptech.glide.request.target.CustomTarget
import com.google.gson.Gson
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
            .name("NetworkingDB")
            .build()
        realm = Realm.getInstance(config)

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        InternetCheck(object : InternetCheck.Consumer {
            override fun accept(internet: Boolean?) {
                if(internet!!) download()
                else loadFromRealm()
            }
        })




    }

    private fun download(){
        val service = RetrofitFactory.makeRetrofitService()

        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getPhotos()

            try {
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {

                        recycler.adapter = PhotoAdapter(response.body()!!, this@PhotosActivity, true)
                        Toast.makeText(applicationContext, "from internet", Toast.LENGTH_SHORT).show()
                        response.body()!!.forEach{
                            saveToRealm(it.url)
                        }

                    } else {
                        Toast.makeText(applicationContext, "${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (err: HttpException) {
                Log.e("Retrofit", "${err.printStackTrace()}")
            }
        }
    }

//    suspend fun glideDownload(url : String, list : MutableList<Bitmap>){
//        val bitmap = Glide.with(this).asBitmap().load(url).submit().get()
//        list.add(bitmap)
//        val json = Gson.toJson(bitmap)
//        Log.i("ADEPT", json)
//    }

    private fun loadFromRealm(){
        val allPhotos = realm.where(Photo::class.java).findAll()

        if(allPhotos.isNotEmpty()){
            recycler.adapter = PhotoAdapter(allPhotos, this, false)
            Toast.makeText(this, "from Realm", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "No data stored. Internet connection required!", Toast.LENGTH_LONG).show()
        }

    }

    private fun saveToRealm(url : String?){
        realm.executeTransactionAsync({ bgRealm ->

            val photo = bgRealm.createObject(Photo::class.java)
            photo.url = url

        }, {
            //success
            Log.i("ADEPT", "successful save")
        }, {
            //fail

            Log.i("ADEPT", "failed save ${it.message}")
        })
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

