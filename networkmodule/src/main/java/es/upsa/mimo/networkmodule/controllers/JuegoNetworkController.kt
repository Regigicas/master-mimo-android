package es.upsa.mimo.networkmodule.controllers

import android.content.Context
import android.util.Log
import com.android.volley.Response
import es.upsa.mimo.datamodule.enums.JuegoOrderEnum
import es.upsa.mimo.datamodule.models.JuegoModel
import es.upsa.mimo.networkmodule.*

class JuegoNetworkController
{
    companion object
    {
        @JvmStatic
        fun getJuegos(page: Int, order: JuegoOrderEnum, context: Context, callback: (juegos: List<JuegoModel>) -> Unit)
        {
            if (page == 0)
                throw AssertionError(context.getString(R.string.assert_invalid_page));

            val queue = VolleyQueueInstance.getInstance(context);
            val url = context.getString(R.string.juegos_global).addParametersToURL(mapOf(
                Pair("page_size", context.getString(R.string.max_games_per_request)),
                Pair("page", "$page"), Pair("ordering", context.getString(order.stringValue()))));
            val juegoRequest = GSONRequest(url, JuegoModel.ResponseQuery::class.java, null,
                Response.Listener { response ->
                    callback(response.results);
                },
                Response.ErrorListener { response ->
                    response.printStackTrace();
                });

            queue.addToRequestQueue(juegoRequest);
        }

        @JvmStatic
        fun getJuegosPlataforma(page: Int, platId: Int, context: Context, callback: (juegos: List<JuegoModel>) -> Unit)
        {
            if (page == 0)
                throw AssertionError(context.getString(R.string.assert_invalid_page));

            val queue = VolleyQueueInstance.getInstance(context);
            val url = context.getString(R.string.juegos_global).addParametersToURL(mutableMapOf(
                Pair("page_size", context.getString(R.string.max_games_per_request)),
                Pair("page", "$page"), Pair("platforms", "$platId")));
            val juegoRequest = GSONRequest(url, JuegoModel.ResponseQuery::class.java, null,
                Response.Listener { response ->
                    callback(response.results);
                },
                Response.ErrorListener { response ->
                    response.printStackTrace();
                });

            queue.addToRequestQueue(juegoRequest);
        }

        @JvmStatic
        fun getJuegoInfo(juegoId: Int, context: Context, callback: (juego: JuegoModel) -> Unit)
        {
            val queue = VolleyQueueInstance.getInstance(context);
            val url = context.getString(R.string.juego_id, juegoId);
            val juegoRequest = GSONRequest(url, JuegoModel::class.java, null,
                Response.Listener { response ->
                    callback(response);
                },
                Response.ErrorListener { response ->
                    response.printStackTrace();
                });

            queue.addToRequestQueue(juegoRequest);
        }

        @JvmStatic
        fun getJuegosQuery(page: Int, query: String, context: Context, callback: (juegos: List<JuegoModel>) -> Unit)
        {
            if (page == 0)
                throw AssertionError(context.getString(R.string.assert_invalid_page));

            val queue = VolleyQueueInstance.getInstance(context);
            val url = context.getString(R.string.juegos_global).addParametersToURL(mutableMapOf(
                Pair("page_size", context.getString(R.string.max_games_per_request)),
                Pair("page", "$page"), Pair("search", query)));
            val juegoRequest = GSONRequest(url, JuegoModel.ResponseQuery::class.java, null,
                Response.Listener { response ->
                    callback(response.results);
                },
                Response.ErrorListener { response ->
                    response.printStackTrace();
                });

            queue.addToRequestQueue(juegoRequest);
        }
    }
}
