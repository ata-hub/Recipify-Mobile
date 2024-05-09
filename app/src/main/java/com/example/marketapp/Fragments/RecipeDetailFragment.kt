package com.example.marketapp.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.marketapp.Adapters.RecipeIngredientsAdapter
import com.example.marketapp.Models.Recipe
import com.example.marketapp.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecipeDetailFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecipeDetailFragment : Fragment() {
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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recipe_detail, container, false)

        // Retrieve the recipe details from arguments
        val recipe: Recipe? = arguments?.getParcelable("recipe")

        // Use the recipe object to populate the UI elements
        recipe?.let {
            // Use the recipe object to populate the UI elements
            Log.d("RecipeDetailFragment", "Recipe name: ${it.name}")
            Log.d("RecipeDetailFragment", "Number of ingredients: ${it.recipeIngredients?.size}")

            val recipeNameTextView: TextView = view.findViewById(R.id.recipeNameTextView)
            val recipeTimeTextView: TextView = view.findViewById(R.id.recipeTimeTextView)
            val recipeDescriptionTextView: TextView = view.findViewById(R.id.recipeDescriptionTextView)
            // Add similar lines for other UI elements
            val recipeIngredientsRecyclerView: RecyclerView = view.findViewById(R.id.recipeIngredientsRecyclerView)
            val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recipeIngredientsRecyclerView.layoutManager = layoutManager

            // Create an adapter for the horizontal RecyclerView
            val recipeIngredientsAdapter = RecipeIngredientsAdapter(it.recipeIngredients ?: emptyList())
            recipeIngredientsRecyclerView.adapter = recipeIngredientsAdapter


            // Populate UI elements with recipe attributes
            recipeNameTextView.text = it.name ?: ""
            recipeTimeTextView.text = "${it.totalTime?.toString() ?: ""} min"
            recipeDescriptionTextView.text = it.instructions ?: ""
            // Set other UI elements with their corresponding attributes

            // For the image, you can use Glide or any other image loading library
            val recipeImageView: ImageView = view.findViewById(R.id.recipeImageView)
            Glide.with(requireContext())
                .load(it.image)
                .placeholder(R.drawable.placeholder_image)
                .into(recipeImageView)
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecipeDetailFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecipeDetailFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}