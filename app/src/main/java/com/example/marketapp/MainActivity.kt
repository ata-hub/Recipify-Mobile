package com.example.marketapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.marketapp.Adapters.ViewPagerAdapter
import com.example.marketapp.Fragments.AllRecipesFragment
import com.example.marketapp.Fragments.HomeFragment
import com.example.marketapp.Fragments.MyRecipesFragment
import com.example.marketapp.Interfaces.RecipeService
import com.example.marketapp.Models.Recipe
import com.example.marketapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val ipaddress: String = "192.168.137.1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        //this 2 line is for login screen animation
        setContentView(view)
        replaceFragment(HomeFragment())

        // Create Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl("http://$ipaddress:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create service
        val service = retrofit.create(RecipeService::class.java)

        service.getAllRecipes().enqueue(object : Callback<List<Recipe>> {
            override fun onResponse(call: Call<List<Recipe>>, response: Response<List<Recipe>>) {
                if (response.isSuccessful) {
                    val recipes = response.body()
                    Toast.makeText(this@MainActivity,"Succssful Connection",Toast.LENGTH_LONG).show()

                    // Handle the list of recipes
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(this@MainActivity,"Failed Connection",Toast.LENGTH_LONG).show()

                }
            }

            override fun onFailure(call: Call<List<Recipe>>, t: Throwable) {
                // Handle network failure
            }
        })

        //this part handles navigation between fragments
        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home ->replaceFragment(HomeFragment())
                R.id.my_recipes ->replaceFragment(MyRecipesFragment())
                R.id.all_recipes ->replaceFragment(AllRecipesFragment())
                else ->{}
            }
            true
        }

    }
    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
    }
}