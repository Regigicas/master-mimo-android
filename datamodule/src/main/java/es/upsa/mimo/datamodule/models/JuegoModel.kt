package es.upsa.mimo.datamodule.models

class JuegoModel
{
    var id: Int? = null;
    var name: String? = null;
    var description: String? = null;
    var released: String? = null;
    var background_image: String? = null;
    var background_image_additional: String? = null;
    var rating: Float? = null;
    private var platforms: List<PlatformModel.PlatformsResponse>? = null;
    var plataformas: List<PlatformModel>?
        get()
        {
            val array: MutableList<PlatformModel> = mutableListOf();
            this.platforms?.let {
                for (plat in it)
                    array.add(plat.platform);
            }

            return array;
        }
        set(_) {}

    class ResponseQuery
    {
        lateinit var results: List<JuegoModel>;
    }

    /*fun getBackgroundURL(): URL
    {
        return URL(string: self.getBackgroundString())!
    }*/

    fun getBackgroundString(): String
    {
        if (background_image == null)
            return "https://via.placeholder.com/500x500";

        val splits = background_image!!.split("/")
        val url1 = splits[splits.size - 1];
        val url2 = splits[splits.size - 2];
        val url3 = splits[splits.size - 3];
        val backgroundUrl = "https://api.rawg.io/media/crop/600/400/$url3/$url2/$url1";
        return backgroundUrl;
    }

    /*func getPlatformString() -> String
    {
        var platforms: String = "Ninguna"

        var first: Bool = true
        if let plataformasList = self.plataformas
        {
            for plat in plataformasList {
                if first
                {
                    first = false
                    platforms = plat.name!
                }
                else
                {
                    platforms += " | \(plat.name!)"
                }
            }
        }

        return platforms
    }*/
}