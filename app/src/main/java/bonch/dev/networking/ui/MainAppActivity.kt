package bonch.dev.networking.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import bonch.dev.networking.R

class MainAppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_app)
    }

     fun onClick(view: View) {

         if(view.id == R.id.add_post_button){
             PostCreateDialogFragment().show(supportFragmentManager, "create_fragment")
             return
         }

        val intent = Intent(
            this,
            when (view.id) {
                R.id.users_button -> UsersActivity::class.java
                R.id.albums_button -> AlbumsActivity::class.java
                R.id.photos_button -> PhotosActivity::class.java
                else -> null
            }
        )

        startActivity(intent)
    }

}
