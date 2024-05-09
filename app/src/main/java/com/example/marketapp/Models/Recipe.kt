package com.example.marketapp.Models


import android.os.Parcel
import android.os.Parcelable

data class Recipe(
    val id: Long? = null,
    val name: String?,
    val totalTime: Int?,
    val description: String?,
    val image: String?,
    val recipeIngredientQuantities: List<Double>?,
    val recipeIngredients: List<String>?,
    val instructions: String?,
    val category: String? // New attribute
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as Long?,
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readArrayList(Double::class.java.classLoader) as List<Double>?,
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(name)
        parcel.writeInt(totalTime ?: 0) // Default to 0 if totalTime is null
        parcel.writeString(description)
        parcel.writeString(image)
        parcel.writeList(recipeIngredientQuantities)
        parcel.writeStringList(recipeIngredients)
        parcel.writeString(instructions)
        parcel.writeString(category) // Write the 'category' attribute to Parcel
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Recipe> {
        override fun createFromParcel(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }
    }
}