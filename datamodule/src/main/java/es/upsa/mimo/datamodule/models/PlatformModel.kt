package es.upsa.mimo.datamodule.models

import es.upsa.mimo.datamodule.R
import java.io.Serializable
import java.util.*

class PlatformModel : Serializable
{
    var id: Int = -1
    lateinit var name: String
    var image_background: String? = null
    var description: String? = null

    inner class PlatformsResponse : Serializable
    {
        lateinit var platform: PlatformModel
    }

    inner class AllPlatformsResponse
    {
        lateinit var results: List<PlatformModel>
    }

    fun getImgFile(dark: Boolean): Int // El API no devuelve el logo, pero si lo tiene en la pagina, asi que usamos está funcion
    {
        var imgId = if (dark) R.drawable.logo_web_dark else R.drawable.logo_web
        val plataformaNombre = name.toLowerCase(Locale.ROOT)
        if (plataformaNombre.contains("pc"))
            imgId = if (dark) R.drawable.logo_pc_dark else R.drawable.logo_pc
        else if (plataformaNombre.contains("sega") || plataformaNombre.contains("dreamcast") || plataformaNombre.contains("game gear") ||
                plataformaNombre.contains("genesis") || plataformaNombre.contains("nepnep"))
            imgId = if (dark) R.drawable.logo_sega_dark else R.drawable.logo_sega
        else if (plataformaNombre.contains("playstation") || plataformaNombre.contains("ps"))
            imgId = if (dark) R.drawable.logo_ps_dark else R.drawable.logo_ps
        else if (plataformaNombre.contains("xbox"))
            imgId = if (dark) R.drawable.logo_xbox_dark else R.drawable.logo_xbox
        else if (plataformaNombre.contains("nintendo") || plataformaNombre.contains("gamecube") || plataformaNombre.contains("game boy") ||
                plataformaNombre.contains("nes") || plataformaNombre.contains("wii"))
            imgId = if (dark) R.drawable.logo_nintendo_dark else R.drawable.logo_nintendo
        else if (plataformaNombre.contains("atari") || plataformaNombre.contains("jaguar"))
            imgId = if (dark) R.drawable.logo_atari_dark else R.drawable.logo_atari
        else if (plataformaNombre.contains("mac") || plataformaNombre.contains("ios") || plataformaNombre.contains("apple"))
            imgId = if (dark) R.drawable.logo_apple_dark else R.drawable.logo_apple
        else if (plataformaNombre.contains("android"))
            imgId = if (dark) R.drawable.logo_android_dark else R.drawable.logo_android
        else if (plataformaNombre.contains("linux"))
            imgId = if (dark) R.drawable.logo_linux_dark else R.drawable.logo_linux
        else if (plataformaNombre.contains("commodore"))
            imgId = if (dark) R.drawable.logo_commodore_dark else R.drawable.logo_commodore
        else if (plataformaNombre.contains("3do"))
            imgId = if (dark) R.drawable.logo_threedo_dark else R.drawable.logo_threedo

        return imgId
    }
}
