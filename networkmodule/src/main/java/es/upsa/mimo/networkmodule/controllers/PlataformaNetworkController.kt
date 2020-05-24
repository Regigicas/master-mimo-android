package es.upsa.mimo.networkmodule.controllers

import android.content.Context
import com.android.volley.Response
import es.upsa.mimo.datamodule.models.PlatformModel
import es.upsa.mimo.networkmodule.requests.GSONRequest
import es.upsa.mimo.networkmodule.R
import es.upsa.mimo.networkmodule.requests.VolleyQueueInstance

class PlataformaNetworkController
{
    companion object
    {
        @JvmStatic
        fun getListadoPlataformas(context: Context, callback: (juegos: List<PlatformModel>) -> Unit)
        {
            val queue = VolleyQueueInstance.getInstance(context)
            val url = context.getString(R.string.plataformas_global)
            val juegoRequest =
                GSONRequest(url, PlatformModel.AllPlatformsResponse::class.java,
                    null,
                    Response.Listener { response ->
                        callback(response.results)
                    },
                    Response.ErrorListener { response ->
                        response.printStackTrace()
                    })

            queue.addToRequestQueue(juegoRequest)
        }

        @JvmStatic
        fun getPlataformaInfo(id: Int, context: Context, callback: (juegos: PlatformModel) -> Unit)
        {
            val queue = VolleyQueueInstance.getInstance(context)
            val url = context.getString(R.string.plataformas_id, id)
            val juegoRequest =
                GSONRequest(url, PlatformModel::class.java,
                    null,
                    Response.Listener { response ->
                        callback(response)
                    },
                    Response.ErrorListener { response ->
                        response.printStackTrace()
                    })

            queue.addToRequestQueue(juegoRequest)
        }
    }
}
