package com.example.marketapp.Adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.marketapp.Enums.ActionType
import com.example.marketapp.Fragments.RecipeDetailFragment
import com.example.marketapp.Helpers.RecipeDBHelper
import com.example.marketapp.Models.Recipe
import com.example.marketapp.R

class RecipeAdapter(private var recipes: MutableList<Recipe>, private val context: Context, private val actionType: ActionType) :
    RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {
    private val dbHelper = RecipeDBHelper(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }
    fun updateRecipes(newRecipes: MutableList<Recipe>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage)
        private val recipeName: TextView = itemView.findViewById(R.id.recipeName)
        private val recipeTime: TextView = itemView.findViewById(R.id.recipeTime)
        private val recipeDescription: TextView = itemView.findViewById(R.id.recipeDescription)
        private val actionIcon: ImageView = itemView.findViewById(R.id.actionIcon)

        fun bind(recipe: Recipe) {
            when (actionType) {
                ActionType.LIKE -> {
                    actionIcon.setImageResource(R.drawable.empty_heart_icon)
                }
                ActionType.DELETE -> {
                    actionIcon.setImageResource(R.drawable.trash_can_icon)
                }

                else -> {}
            }
            // Load image using Glide or your preferred image loading library
            Glide.with(itemView.context)
                .load(recipe.image)
                .placeholder(R.drawable.placeholder_image) // Replace with your placeholder drawable
                .into(recipeImage)

            recipeName.text = recipe.name
            recipeTime.text = itemView.context.getString(R.string.total_time, recipe.totalTime)
            recipeDescription.text = recipe.description

            // Set click listener or any other interaction handling here
            itemView.setOnClickListener {
                // Handle item click
                // Handle item click
                val context = itemView.context
                val fragmentManager = (context as AppCompatActivity).supportFragmentManager

                // Create a new instance of RecipeDetailFragment and pass the selected recipe as an argument
                val detailFragment = RecipeDetailFragment()
                val bundle = Bundle()
                bundle.putParcelable("recipe", recipe)
                detailFragment.arguments = bundle

                // Replace the current fragment in the frameLayout container with RecipeDetailFragment
                fragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, detailFragment)
                    .addToBackStack(null) // Optional: Add to back stack for fragment navigation
                    .commit()
            }
            actionIcon.setOnClickListener {
                when (actionType) {
                    ActionType.LIKE -> {
                        actionIcon.setOnClickListener {
                            // Update the heart icon based on the new like status
                            actionIcon.setImageResource(R.drawable.filled_heart_icon)
                            //save the recipe to sqlite
                            dbHelper.insertRecipe(recipe)

                        }
                    }
                    ActionType.DELETE -> {
                        // Handle delete action
                        val position = adapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            val deletedRecipe = recipes[position]
                            val deletedId = deletedRecipe.id
                            if (deletedId != null) {
                                dbHelper.deleteRecipe(deletedId)
                                recipes.removeAt(position)
                                notifyDataSetChanged()
                            }
                        }
                    }

                    else -> {}
                }
            }
            // Handle heart icon click

        }
    }
}