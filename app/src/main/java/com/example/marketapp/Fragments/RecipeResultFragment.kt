package com.example.marketapp.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.marketapp.Adapters.RecipeAdapter
import com.example.marketapp.Enums.ActionType
import com.example.marketapp.Models.Recipe
import com.example.marketapp.R

class RecipeResultFragment : Fragment() {

    private var recipes: List<Recipe>? = null

    companion object {
        private const val ARG_RECIPES = "recipes"

        fun newInstance(recipes: List<Recipe>): RecipeResultFragment {
            val fragment = RecipeResultFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_RECIPES, ArrayList(recipes))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            recipes = it.getParcelableArrayList(ARG_RECIPES)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recipe_result, container, false)

        // Initialize RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.resultRecipeRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Set up RecyclerView adapter with the list of recipes
        recipes?.let { recipes ->
            val adapter = RecipeAdapter(recipes.toMutableList(), requireContext(), ActionType.LIKE)
            recyclerView.adapter = adapter
        }

        // Set OnClickListener to the back button
        view.findViewById<View>(R.id.backButton).setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return view
    }
}
