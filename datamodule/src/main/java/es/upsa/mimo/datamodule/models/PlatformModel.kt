package es.upsa.mimo.datamodule.models

class PlatformModel
{
    var id: Int? = null;
    var name: String? = null;
    var image_background: String? = null;
    var description: String? = null;

    class PlatformsResponse
    {
        lateinit var platform: PlatformModel;
    }

    class AllPlatformsResponse
    {
        lateinit var results: List<PlatformModel>;
    }

    fun getImgFile(dark: Boolean): String? // El API no devuelve el logo, pero si lo tiene en la pagina, asi que usamos está funcion
    {
        var imgPath: String = "logo_web"
        this.name?.let {
            val plataformaNombre = it.toLowerCase();
            if (plataformaNombre.contains("pc"))
                imgPath = "logo_pc"
            else if (plataformaNombre.contains("sega") || plataformaNombre.contains("dreamcast") || plataformaNombre.contains("game gear") ||
                    plataformaNombre.contains("genesis") || plataformaNombre.contains("nepnep"))
                imgPath = "logo_sega"
            else if (plataformaNombre.contains("playstation") || plataformaNombre.contains("ps"))
                imgPath = "logo_ps"
            else if (plataformaNombre.contains("xbox"))
                imgPath = "logo_xbox"
            else if (plataformaNombre.contains("nintendo") || plataformaNombre.contains("gamecube") || plataformaNombre.contains("game boy") ||
                    plataformaNombre.contains("nes") || plataformaNombre.contains("wii"))
                imgPath = "logo_nintendo"
            else if (plataformaNombre.contains("atari") || plataformaNombre.contains("jaguar"))
                imgPath = "logo_atari"
            else if (plataformaNombre.contains("mac") || plataformaNombre.contains("ios") || plataformaNombre.contains("apple"))
                imgPath = "logo_apple"
            else if (plataformaNombre.contains("android"))
                imgPath = "logo_android"
            else if (plataformaNombre.contains("linux"))
                imgPath = "logo_linux"
            else if (plataformaNombre.contains("commodore"))
                imgPath = "logo_commodore"
            else if (plataformaNombre.contains("3do"))
                imgPath = "logo_threedo"
        }

        val darkModeStr = if (dark) "_dark.png" else ".png"
        return "$imgPath$darkModeStr"
    }
}