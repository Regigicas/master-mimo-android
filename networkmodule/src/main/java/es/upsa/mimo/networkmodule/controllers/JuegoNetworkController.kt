package es.upsa.mimo.networkmodule.controllers

import android.content.Context
import android.util.Log
import com.android.volley.Response
import es.upsa.mimo.datamodule.enums.JuegoOrderEnum
import es.upsa.mimo.datamodule.models.JuegoModel
import es.upsa.mimo.networkmodule.GSONRequest
import es.upsa.mimo.networkmodule.R
import es.upsa.mimo.networkmodule.VolleyQueueInstance

class JuegoNetworkController
{
    companion object
    {
        @JvmStatic
        fun getJuegos(page: Int, order: JuegoOrderEnum, context: Context, callback: (juegos: List<JuegoModel>) -> Void)
        {
            assert(page > 0, { "El número de la página tiene que ser > 0" });

            val queue = VolleyQueueInstance.getInstance(context);
            val url = context.getString(R.string.juegos_global);
            val juegoRequest = GSONRequest(url,
                JuegoModel.ResponseQuery::class.java,
                mutableMapOf(
                    Pair("page_size", "20"),
                    Pair("page", "$page"), Pair("ordering", "${order.stringValue()}")
                ),
                Response.Listener { response ->
                    callback(response.results);
                },
                Response.ErrorListener { response ->
                    Log.e("response", response.localizedMessage);
                });

            queue.addToRequestQueue(juegoRequest);
        }
    }
}
