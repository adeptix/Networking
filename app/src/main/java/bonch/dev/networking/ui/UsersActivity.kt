package bonch.dev.networking.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import bonch.dev.networking.InternetCheck
import bonch.dev.networking.R
import bonch.dev.networking.SPrefController
import bonch.dev.networking.adapters.PhotoAdapter
import bonch.dev.networking.adapters.UserAdapter
import bonch.dev.networking.networking.RetrofitFactory
import kotlinx.android.synthetic.main.recycler_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import com.google.gson.reflect.TypeToken

import bonch.dev.networking.models.User


class UsersActivity : AppCompatActivity() {

    lateinit var sPrefController : SPrefController
    val USERS_KEY = "USERS_KEY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recycler_layout)

        sPrefController = SPrefController(this)

        supportActionBar!!.title="Users"

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        InternetCheck(object : InternetCheck.Consumer {
            override fun accept(internet: Boolean?) {
                if(internet!!) download()
                else loadFromPrefs()
                //else Toast.makeText(this@UsersActivity, "no connection", Toast.LENGTH_SHORT).show()
            }
        })


    }

    private fun download(){
        val service = RetrofitFactory.makeRetrofitService()

        CoroutineScope(Dispatchers.IO).launch{
            val response = service.getUsers()
            try {
                withContext(Dispatchers.Main){
                    if(response.isSuccessful){
                        sPrefController.save(USERS_KEY, response.body()!!)
                        recycler.adapter = UserAdapter(response.body()!!, this@UsersActivity)
                        Toast.makeText(applicationContext, "from internet", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(applicationContext, "${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            }catch (err : HttpException){
                Log.e("Retrofit", "${err.printStackTrace()}")
            }
        }
    }

    private fun loadFromPrefs(){
        if (!sPrefController.check(USERS_KEY)) {
            Toast.makeText(this, "No data stored. Internet connection required!", Toast.LENGTH_LONG)
                .show()
            return
        }

        val type = object : TypeToken<List<User>>() {}.type

        recycler.adapter = UserAdapter(sPrefController.load(USERS_KEY, type) as List<User> , this@UsersActivity)
        Toast.makeText(this@UsersActivity, "from shared pref", Toast.LENGTH_SHORT).show()
    }


}
