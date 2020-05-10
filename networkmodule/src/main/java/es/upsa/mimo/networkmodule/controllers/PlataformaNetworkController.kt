package es.upsa.mimo.networkmodule.controllers

import android.content.Context
import android.util.Log
import com.android.volley.Response
import es.upsa.mimo.datamodule.models.PlatformModel
import es.upsa.mimo.networkmodule.GSONRequest
import es.upsa.mimo.networkmodule.R
import es.upsa.mimo.networkmodule.VolleyQueueInstance

class PlataformaNetworkController
{
    companion object
    {
        @JvmStatic
        fun getListadoPlataformas(context: Context, callback: (juegos: List<PlatformModel>) -> Unit)
        {
            val queue = VolleyQueueInstance.getInstance(context);
            val url = context.getString(R.string.plataformas_global);
            val juegoRequest = GSONRequest(url,
                PlatformModel.AllPlatformsResponse::class.java,
                null,
                Response.Listener { response ->
                    callback(response.results);
                },
                Response.ErrorListener { response ->
                    Log.e("response", response.localizedMessage);
                });

            queue.addToRequestQueue(juegoRequest);
        }

        @JvmStatic
        fun getPlataformaInfo(id: Int, context: Context, callback: (juegos: PlatformModel) -> Void)
        {
            val queue = VolleyQueueInstance.getInstance(context);
            val url = context.getString(R.string.plataformas_id, id);
            val juegoRequest = GSONRequest(url,
                PlatformModel.PlatformsResponse::class.java,
                null,
                Response.Listener { response ->
                    callback(response.platform);
                },
                Response.ErrorListener { response ->
                    Log.e("response", response.localizedMessage);
                });

            queue.addToRequestQueue(juegoRequest);
        }
    }
}
