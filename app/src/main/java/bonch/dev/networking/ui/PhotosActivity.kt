package bonch.dev.networking.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.networking.R
import bonch.dev.networking.adapters.PhotoAdapter
import bonch.dev.networking.networking.RetrofitFactory
import kotlinx.android.synthetic.main.recycler_layout.*
import kotlinx.coroutines.*
import retrofit2.HttpException

class PhotosActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recycler_layout)

        supportActionBar!!.title="Photos"

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.addItemDecoration(DividerItemDecoration(this@PhotosActivity, DividerItemDecoration.VERTICAL))

        val service = RetrofitFactory.makeRetrofitService()

        CoroutineScope(Dispatchers.IO).launch{
            val response = service.getPhotos()

            try {
                withContext(Dispatchers.Main){
                    if(response.isSuccessful){

                        recycler.adapter = PhotoAdapter(response.body()!!, this@PhotosActivity)

                    }else{
                        Toast.makeText(applicationContext, "${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }catch (err : HttpException){
                Log.e("Retrofit", "${err.printStackTrace()}")
            }
        }


    }


}

