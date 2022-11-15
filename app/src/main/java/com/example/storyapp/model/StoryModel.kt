package com.example.storyapp.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
@Parcelize
@Entity(tableName = "story")
data class StoryModel(
    @PrimaryKey
    @field:SerializedName("id")
    val id: String,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("description")
    val description: String,
    @field:SerializedName("photoUrl")
    val photoUrl: String,
    @field:SerializedName("createdAt")
    val createdAt: String,
    @field:SerializedName("lat")
    val lat: Double,
    @field:SerializedName("lon")
    val lon: Double
) : Parcelable {
    companion object : Parceler<StoryModel>{
        override fun create(parcel: Parcel): StoryModel {
            return StoryModel(parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readString().toString(),
                parcel.readDouble(),
                parcel.readDouble())
        }

        override fun StoryModel.write(parcel: Parcel, flags: Int) {
            parcel.writeString(id)
            parcel.writeString(name)
            parcel.writeString(description)
            parcel.writeString(photoUrl)
            parcel.writeString(createdAt)
            parcel.writeDouble(lat)
            parcel.writeDouble(lon)
        }
    }
}
