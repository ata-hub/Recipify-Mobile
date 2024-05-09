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
            val adapter = RecipeAdapter(getSampleRecipes().toMutableList(), requireContext(), ActionType.LIKE) //burası recipes.toMutableList() olacak
            recyclerView.adapter = adapter
        }

        return view
    }
    private fun getSampleRecipes(): List<Recipe> {
        return listOf(
            Recipe(
                name = "Chicken Breasts Lombardi",
                totalTime = 75,
                description = "",
                image = "https://img.sndimg.com/food/image/upload/w_555,h_416,c_fit,fl_progressive,q_95/v1/img/recipes/49/m1z1F8S5mAZgyImm5zYw_Lombardi%20Chicken%203.jpg",
                recipeIngredientQuantities = listOf(1.0, 2.0),
                recipeIngredients = listOf("Ingredient A", "Ingredient B"),
                instructions = "Cook mushrooms in 2 tbsp butter in a large  skillet, stirring constantly, just   " +
                        "until tender.Remove from heat; set aside.  Cut each chicken breast half in  half lengthwise.   " +
                        "Place each piece of chicken  between two sheets of wax paper; flatten to  1/8\\ thickness, using a meat  " +
                        "mallet or rolling pin.Dredge chicken pieces in flour.Place 5 or 6  pieces of chicken in 1 to 2  " +
                        "tbsp butter in a  large skillet; cook over medium  heat 3 to 4 minutes on each  side or until " +
                        "golden.Place chicken in a lightly  greased 13x9\\ baking dish,  overlapping  edges.",
                category = "meal"
            ),
            Recipe(
                name = "Butterflied Lamb with Garlic Butter",
                totalTime = 300,
                description = "",
                image = "https://img.sndimg.com/food/image/upload/w_555,h_416,c_fit,fl_progressive,q_95/v1/img/recipes/10/9/picnSlN8g.jpg",
                recipeIngredientQuantities = listOf(2.0, 3.0),
                recipeIngredients = listOf("Ingredient C", "Ingredient D"),
                instructions = "Blend ingredients and bake.",
                category = "meal"
            ),
            Recipe(
                name = "Pecan Apple Cake",
                totalTime = 90,
                description = "",
                image = "https://img.sndimg.com/food/image/upload/w_555,h_416,c_fit,fl_progressive,q_95/v1/img/recipes/11/8/pic1UgDNm.jpg",
                recipeIngredientQuantities = listOf(2.0, 3.0),
                recipeIngredients = listOf("butter","sugar","cinnamon","nutmeg","all-purpose flour","tart apples","eggs","milk","vanilla extract","pecans","baking powder","baking soda","salt"),
                instructions = "Preheat the oven to 350 degrees.Brush the sides of a 8 x 3 1/4-inch  springform pan with the melted butter." +
                        "Mix together 1/2 cup sugar, cinnamon,  nutmeg, and 1/4 cup flour and sprinkle the mixture evenly over the bottom of the pan." +
                        "Wrap foil around the pan to prevent leakage. Starting at the outside  edge, arrange a ring of apple slices in the pan, " +
                        "slightly overlapping and  pointing to the center.(It will feel backwards.) " +
                        "Fill in the center with  another circle of apples, with some overlap occurring., \n" +
                        "Layer any remaining  apple slices evenly, overlapping to prevent the batter from escaping. " +
                        "With a  wooden spoon or electric mixer, beat together the butter and 1 cup sugar.Add  the eggs, milk, rum, and vanilla." +
                        " The batter will look curdled.Add 1 1/4  cups flour, the nuts, baking powder, baking soda, and salt, beating only  until the flour is completely incorporated.Pour the batter over the apples  and spread evenly.Place the pan on a baking sheet and bake in the middle of  the oven until a toothpick inserted in the cake comes out clean, about 70  minutes., \n" +
                        "Cover with a piece of foil if the top begins to brown too quickly." +
                        "Let the cake rest in the pan on a rack for 5 minutes, then, using a small, " +
                        "flexible knife, gently separate the sides of the cake from the pan." +
                        "Invert the cake on the rack, letting it stay in the pan for another 10 minutes, " +
                        "then  remove the pan, lifting it up carefully.\"",
                category = "dessert"
            ),
            Recipe(
                name = "Pumpkin Apple Pie",
                totalTime = 60,
                description = "",
                image = "https://img.sndimg.com/food/image/upload/w_555,h_416,c_fit,fl_progressive,q_95/v1/img/recipes/10/32/picLXeOVb.jpg",
                recipeIngredientQuantities = listOf(2.0, 3.0),
                recipeIngredients = listOf("Ingredient C", "Ingredient D"),
                instructions = "Blend ingredients and bake.",
                category = "dessert"
            ),
            Recipe(
                name = "Perfectly Spiced Banana Bread",
                totalTime = 65,
                description = "",
                image = "https://img.sndimg.com/food/image/upload/w_555,h_416,c_fit,fl_progressive,q_95/v1/img/recipes/13/87/01480807071.jpeg",
                recipeIngredientQuantities = listOf(2.0, 3.0),
                recipeIngredients = listOf("Ingredient C", "Ingredient D"),
                instructions = "Blend ingredients and bake.",
                category = "dessert"
            ),
            Recipe(
                name = "Cafe Diablo",
                totalTime = 10,
                description = "",
                image = "https://img.sndimg.com/food/image/upload/w_555,h_416,c_fit,fl_progressive,q_95/v1/img/recipes/15/87/pic0bMKrl.jpg",
                recipeIngredientQuantities = listOf(2.0, 3.0),
                recipeIngredients = listOf("Ingredient C", "Ingredient D"),
                instructions = "Blend ingredients and bake.",
                category = "dessert"
            ),


            // Add more sample recipes as needed
        )
    }
}
