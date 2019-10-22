package com.inexture.baseproject.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class RepoSearchResponse(
    @SerializedName("total_count")
    val total: Int = 0,
    @SerializedName("items")
    val items: List<Repo>
)

@Entity
data class Repo(
    @PrimaryKey
    val id: Int,
    @field:SerializedName("name")
    val name: String,
    @field:SerializedName("full_name")
    val fullName: String,
    @field:SerializedName("description")
    val description: String?,
    @field:SerializedName("owner")
    @field:Embedded(prefix = "owner_")
    val owner: Owner,
    @field:SerializedName("stargazers_count")
    val stars: Int
) {

    data class Owner(
        @field:SerializedName("login")
        val login: String,
        @field:SerializedName("url")
        val url: String?
    )
}