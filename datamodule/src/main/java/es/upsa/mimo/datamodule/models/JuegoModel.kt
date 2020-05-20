package es.upsa.mimo.datamodule.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class JuegoModel : Serializable
{
    var id: Int = -1
    lateinit var name: String
    var description: String? = null
    var released: String? = null
    var background_image: String? = null
    var rating: Float? = null
    private var platforms: List<PlatformModel.PlatformsResponse>? = null
    var clip: ClipModel? = null

    var plataformas: List<PlatformModel>?
        get()
        {
            val array: MutableList<PlatformModel> = mutableListOf()
            this.platforms?.let {
                for (plat in it)
                    array.add(plat.platform)
            }

            return array
        }
        set(_) {}

    inner class ResponseQuery
    {
        lateinit var results: List<JuegoModel>
    }

    inner class ClipModel : Serializable
    {
        var clips: ClipsModel? = null

        inner class ClipsModel : Serializable
        {
            @SerializedName("320")
            var p320: String? = null
            @SerializedName("640")
            var p640: String? = null
            var full: String? = null
        }
    }

    fun getBackgroundString(): String
    {
        if (background_image == null)
            return "https://via.placeholder.com/100x100"

        val splits = background_image!!.split("/")
        val url1 = splits[splits.size - 1]
        val url2 = splits[splits.size - 2]
        val url3 = splits[splits.size - 3]
        val backgroundUrl = "https://api.rawg.io/media/crop/600/400/$url3/$url2/$url1"
        return backgroundUrl
    }

    fun getPlatformString(): String
    {
        var platforms = "Ninguna"
        var first = true
        plataformas?.let {
            for (plat in it)
            {
                if (first)
                {
                    first = false
                    platforms = plat.name
                }
                else
                {
                    platforms += " | ${plat.name}"
                }
            }
        }

        return platforms
    }
}
