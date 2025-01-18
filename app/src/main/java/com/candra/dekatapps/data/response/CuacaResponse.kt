package com.candra.dekatapps.data.response

import com.google.gson.annotations.SerializedName

data class CuacaResponse(

	@field:SerializedName("data")
	val data: List<DataItem>,

	@field:SerializedName("status")
	val status: String? = null
)


data class DataItem(

	@field:SerializedName("kondisi")
	val kondisi: String? = null,

	@field:SerializedName("dampak")
	val dampak: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("kecamatan")
	val kecamatan: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: String? = null
)
