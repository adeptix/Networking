package bonch.dev.networking.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.networking.InternetCheck
import bonch.dev.networking.R
import bonch.dev.networking.SPrefController
import bonch.dev.networking.adapters.AlbumAdapter
import bonch.dev.networking.adapters.PhotoAdapter
import bonch.dev.networking.adapters.UserAdapter
import bonch.dev.networking.models.Album
import bonch.dev.networking.models.User
import bonch.dev.networking.networking.RetrofitFactory
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.recycler_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class AlbumsActivity : AppCompatActivity() {

    lateinit var sPrefController: SPrefController
    val ALBUMS_KEY = "ALBUMS_KEY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recycler_layout)

        sPrefController = SPrefController(this)

        supportActionBar!!.title = "Albums"

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        InternetCheck(object : InternetCheck.Consumer {
            override fun accept(internet: Boolean?) {
                if (internet!!) download()
                else loadFromPrefs()
            }
        })


    }

    fun download() {
        val service = RetrofitFactory.makeRetrofitService()

        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getAlbums()

            try {
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        sPrefController.save(ALBUMS_KEY, response.body()!!)
                        recycler.adapter = AlbumAdapter(response.body()!!, this@AlbumsActivity)
                        Toast.makeText(applicationContext, "from internet", Toast.LENGTH_SHORT)
                            .show()
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

    private fun loadFromPrefs() {
        if (!sPrefController.check(ALBUMS_KEY)) {
            Toast.makeText(this, "No data stored. Internet connection required!", Toast.LENGTH_LONG)
                .show()
            return
        }

        val type = object : TypeToken<List<Album>>() {}.type

        recycler.adapter =
            AlbumAdapter(sPrefController.load(ALBUMS_KEY, type) as MutableList<Album>, this)
        Toast.makeText(this, "from shared pref", Toast.LENGTH_SHORT).show()
    }


}
