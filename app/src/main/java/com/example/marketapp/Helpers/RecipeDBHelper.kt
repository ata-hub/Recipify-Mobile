package com.example.marketapp.Helpers

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.marketapp.Models.Recipe

class RecipeDBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 8 // Incremented version for new change
        private const val DATABASE_NAME = "RecipeDatabase.db"

        // Define table and column names
        private const val TABLE_NAME = "recipes_table"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_TOTAL_TIME = "totalTime"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_IMAGE = "image"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_INGREDIENTS = "ingredients"
        private const val COLUMN_INSTRUCTIONS = "instructions" // Added column for instructions
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_RECIPE_TABLE = ("CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY," +
                "$COLUMN_NAME TEXT," +
                "$COLUMN_TOTAL_TIME INTEGER," +
                "$COLUMN_DESCRIPTION TEXT," +
                "$COLUMN_IMAGE TEXT," +
                "$COLUMN_CATEGORY TEXT," +
                "$COLUMN_INGREDIENTS TEXT," +
                "$COLUMN_INSTRUCTIONS TEXT" + // Define column for instructions
                ")")
        db.execSQL(CREATE_RECIPE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
        Log.i("sqlite", "db upgraded to version:$newVersion")

    }

    fun insertRecipe(recipe: Recipe): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, recipe.name)
            put(COLUMN_TOTAL_TIME, recipe.totalTime)
            put(COLUMN_DESCRIPTION, recipe.description)
            put(COLUMN_IMAGE, recipe.image)
            put(COLUMN_CATEGORY, recipe.category)
            put(COLUMN_INGREDIENTS, recipe.recipeIngredients?.joinToString("\n")) // Join with \n
            put(COLUMN_INSTRUCTIONS, recipe.instructions) // Store instructions
            Log.i("sqlite", "adding recipe" + recipe.name)
        }
        val id = db.insert(TABLE_NAME, null, values)
        db.close()
        return id
    }

    fun deleteRecipe(recipeId: Long): Int {
        val db = this.writableDatabase
        val deletedRows = db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(recipeId.toString()))
        db.close()
        return deletedRows
    }

    fun getAllRecipes(): ArrayList<Recipe> {
        val recipeList = ArrayList<Recipe>()
        val db = this.readableDatabase
        val cursor: Cursor? = db.rawQuery("SELECT * FROM $TABLE_NAME", null)
        cursor?.use {
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                val totalTime = cursor.getInt(cursor.getColumnIndex(COLUMN_TOTAL_TIME))
                val description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION))
                val image = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE))
                val category = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY))
                val ingredientsString = cursor.getString(cursor.getColumnIndex(COLUMN_INGREDIENTS))
                val recipeIngredients = ingredientsString?.split("\n") // Split by \n
                val instructions = cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUCTIONS))

                // Create Recipe object
                val recipe = Recipe(id, name, totalTime, description, image, null, recipeIngredients, instructions, category)
                recipeList.add(recipe)
            }
        }
        cursor?.close()
        db.close()
        return recipeList
    }
}
