package bonch.dev.networking.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import bonch.dev.networking.R
import bonch.dev.networking.adapters.PhotoAdapter
import bonch.dev.networking.networking.RetrofitFactory
import kotlinx.android.synthetic.main.fragment_post_create.*
import kotlinx.android.synthetic.main.fragment_post_create.view.*
import kotlinx.android.synthetic.main.recycler_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class PostCreateDialogFragment : DialogFragment() {

    lateinit var titleEditText: EditText
    lateinit var bodyEditText: EditText
    lateinit var createButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_post_create, container, false)
        initializeViews(view)
        setListeners()
        return view
    }

    private fun initializeViews(view: View){
        titleEditText = view.title_edit_text
        bodyEditText = view.body_edit_text
        createButton = view.create_button
    }

    private fun setListeners() {
        createButton.setOnClickListener {
            val service = RetrofitFactory.makeRetrofitService()

            CoroutineScope(Dispatchers.IO).launch {
                val response = service.postPost(
                    titleEditText.text.toString(),
                    bodyEditText.text.toString()
                )

                try {
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Posted. Code : ${response.code()}", Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(context, "${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                    dismiss()
                } catch (err: HttpException) {
                    Log.e("Retrofit", "${err.printStackTrace()}")
                }
            }


        }
    }
}