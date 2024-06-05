package com.example.marketapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import androidx.appcompat.widget.SearchView

class AllRecipesFragment : Fragment() {
    private lateinit var binding: FragmentAllRecipesBinding
    private lateinit var recipeAdapter: RecipeAdapter
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.56.1:8080")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var allRecipes: MutableList<Recipe> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        // Initialize RecipeAdapter with an empty list
        recipeAdapter = RecipeAdapter(mutableListOf(), requireContext(), ActionType.LIKE)

        // Set up RecyclerView
        binding.recipeRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recipeAdapter
        }

        // Initialize Retrofit
        val recipeService = retrofit.create(RecipeService::class.java)
        recipeService.getAllRecipes().enqueue(object : Callback<List<Recipe>> {
            override fun onResponse(call: Call<List<Recipe>>, response: Response<List<Recipe>>) {
                if (response.isSuccessful) {
                    val recipes = response.body()
                    if (recipes != null) {
                        allRecipes = recipes.toMutableList()
                        recipeAdapter.updateRecipes(allRecipes)
                    }
                } else {
                    // Handle unsuccessful response
                }
            }

            override fun onFailure(call: Call<List<Recipe>>, t: Throwable) {
                // Handle network failure
            }
        })

        // Set up SearchView
        binding.recipeSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterRecipes(query, binding.categorySpinner.selectedItem.toString())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterRecipes(newText, binding.categorySpinner.selectedItem.toString())
                return true
            }
        })

        // Set up Spinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.category_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.categorySpinner.adapter = adapter
        }

        binding.categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                filterRecipes(binding.recipeSearchView.query.toString(), parent.getItemAtPosition(position).toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                filterRecipes(binding.recipeSearchView.query.toString(), "Hepsi")
            }
        }
    }

    private fun filterRecipes(query: String?, category: String) {
        val categoryMap = mapOf(
            "Hepsi" to "Hepsi",
            "Öğün" to "Ogun",
            "Salata" to "Salata",
            "Çorba" to "Corba",
            "Tatlı" to "Tatli",
            "İçecek" to "Icecek"
        )

        val mappedCategory = categoryMap[category] ?: category

        val filteredRecipes = allRecipes.filter { recipe ->
            val matchesQuery = query?.let {
                recipe.name?.contains(it, ignoreCase = true) ?: false
            } ?: true

            val matchesCategory = when (mappedCategory) {
                "Hepsi" -> true
                else -> recipe.category?.equals(mappedCategory, ignoreCase = true) ?: false
            }

            matchesQuery && matchesCategory
        }
        recipeAdapter.updateRecipes(filteredRecipes.toMutableList())
    }

}
