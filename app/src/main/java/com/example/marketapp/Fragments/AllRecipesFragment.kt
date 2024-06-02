package com.example.marketapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.marketapp.Adapters.RecipeAdapter
import com.example.marketapp.Enums.ActionType
import com.example.marketapp.Interfaces.RecipeService
import com.example.marketapp.Models.Recipe
import com.example.marketapp.R
import com.example.marketapp.databinding.FragmentAllRecipesBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [AllRecipesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllRecipesFragment : Fragment() {
    private lateinit var binding: FragmentAllRecipesBinding
    private lateinit var recipeAdapter: RecipeAdapter
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.56.1:8080") //TODO Replace with your actual backend base URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllRecipesBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecipeAdapter with sample recipes
        //val sampleRecipes = getSampleRecipes()
        // Initialize RecipeAdapter with an empty list
        recipeAdapter = RecipeAdapter(mutableListOf(), requireContext(), ActionType.LIKE)
        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.56.1:8080") // Replace with your base URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create an instance of RecipeService
        val recipeService = retrofit.create(RecipeService::class.java)
        // Make the network request to fetch all recipes
        recipeService.getAllRecipes().enqueue(object : Callback<List<Recipe>> {
            override fun onResponse(call: Call<List<Recipe>>, response: Response<List<Recipe>>) {
                if (response.isSuccessful) {
                    // Handle successful response
                    val recipes = response.body()
                    if (recipes != null) {
                        // Initialize RecipeAdapter with the fetched recipes
                        recipeAdapter = RecipeAdapter(recipes as MutableList<Recipe>, requireContext(), ActionType.LIKE)

                        // Set up RecyclerView
                        binding.recipeRecyclerView.apply {
                            layoutManager = LinearLayoutManager(requireContext())
                            adapter = recipeAdapter
                        }
                    }
                } else {
                    // Handle unsuccessful response
                    // Maybe show an error message to the user
                }
            }

            override fun onFailure(call: Call<List<Recipe>>, t: Throwable) {
                // Handle network failure
                // Maybe show an error message to the user
            }
        })


        // Set up RecyclerView
        binding.recipeRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recipeAdapter
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AllRecipesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AllRecipesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}