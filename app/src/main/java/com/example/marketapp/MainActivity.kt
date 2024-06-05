package com.example.marketapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
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
    private val ipaddress: String = "192.168.137.1"
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize bottom navigation
        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home -> replaceFragment(HomeFragment())
                R.id.my_recipes -> replaceFragment(MyRecipesFragment())
                R.id.all_recipes -> replaceFragment(AllRecipesFragment())
            }
            true
        }

        // Set the default selected item to Home
        binding.bottomNav.selectedItemId = R.id.home

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
                    Toast.makeText(this@MainActivity, "Successful Connection", Toast.LENGTH_LONG).show()
                    // Handle the list of recipes
                } else {
                    // Handle unsuccessful response
                    Toast.makeText(this@MainActivity, "Failed Connection", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Recipe>>, t: Throwable) {
                // Handle network failure
            }
        })

        // Handle initial fragment display
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        // Handle fragment changes in the back stack
        supportFragmentManager.addOnBackStackChangedListener {
            val fragment = supportFragmentManager.findFragmentById(R.id.frameLayout)
            currentFragment = fragment
            adjustLayoutParameters()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun adjustLayoutParameters() {
        val layoutParams = binding.frameLayout.layoutParams as RelativeLayout.LayoutParams
        if (currentFragment is HomeFragment) {
            layoutParams.addRule(RelativeLayout.ABOVE, binding.bottomNav.id)
        } else {
            layoutParams.height = RelativeLayout.LayoutParams.MATCH_PARENT
            layoutParams.removeRule(RelativeLayout.ABOVE)
        }
        binding.frameLayout.layoutParams = layoutParams
    }
}
