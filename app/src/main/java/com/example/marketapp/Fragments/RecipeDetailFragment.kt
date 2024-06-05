package com.example.marketapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.marketapp.Adapters.RecipeIngredientsAdapter
import com.example.marketapp.Models.Recipe
import com.example.marketapp.R

class RecipeDetailFragment : Fragment() {

    private var recipe: Recipe? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            recipe = it.getParcelable("recipe")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recipe_detail, container, false)

        // Initialize UI elements
        val recipeNameTextView = view.findViewById<View>(R.id.recipeNameTextView) as TextView
        val recipeTimeTextView = view.findViewById<View>(R.id.recipeTimeTextView) as TextView
        val recipeDescriptionTextView = view.findViewById<View>(R.id.recipeDescriptionTextView) as TextView
        val recipeIngredientsRecyclerView = view.findViewById<View>(R.id.recipeIngredientsRecyclerView) as RecyclerView
        val recipeImageView = view.findViewById<View>(R.id.recipeImageView) as ImageView
        val recipeBackButton = view.findViewById<View>(R.id.recipeBackButton) as ImageButton

        // Populate UI elements with recipe data
        recipe?.let {
            recipeNameTextView.text = it.name ?: ""
            recipeTimeTextView.text = "${it.totalTime?.toString() ?: ""} min"
            recipeDescriptionTextView.text = it.instructions ?: ""

            // Load image using Glide library
            Glide.with(requireContext())
                .load(it.image)
                .placeholder(R.drawable.placeholder_image)
                .into(recipeImageView)

            // Set up RecyclerView for ingredients
            val layoutManager = LinearLayoutManager(requireContext())
            recipeIngredientsRecyclerView.layoutManager = layoutManager
            val adapter = RecipeIngredientsAdapter(it.recipeIngredients ?: emptyList())
            recipeIngredientsRecyclerView.adapter = adapter
        }

        // Set OnClickListener to the back button
        recipeBackButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param recipe The recipe object to display.
         * @return A new instance of fragment RecipeDetailFragment.
         */
        @JvmStatic
        fun newInstance(recipe: Recipe) =
            RecipeDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("recipe", recipe)
                }
            }
    }
}
