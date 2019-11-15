package bonch.dev.networking

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import java.lang.reflect.Type

class SPrefController(context: Context) {

    private val sharedPref = context.getSharedPreferences("MY_SHARED_PREF", Context.MODE_PRIVATE)
    private val prefsEditor = sharedPref.edit()
    private val gson = Gson()


    fun save(key: String, list: List<Any>){
        val json = gson.toJson(list)
        prefsEditor.putString(key, json).commit()
    }

    fun load(key: String, type: Type) : List<Any>{
        val json = sharedPref.getString(key, "")
        return gson.fromJson(json, type)
    }

    fun check(key: String) : Boolean{
        return sharedPref.contains(key)
    }

}