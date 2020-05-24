package es.upsa.mimo.networkmodule.requests

import android.content.Context
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class VolleyQueueInstance constructor(context: Context)
{
    companion object
    {
        val maxTimeoutTime = 8000;
        val maxNumRetries = 4;

        @Volatile
        private var INSTANCE: VolleyQueueInstance? = null
        fun getInstance(context: Context) =
            INSTANCE
                ?: synchronized(this)
            {
                INSTANCE
                    ?: VolleyQueueInstance(
                        context
                    ).also {
                    INSTANCE = it
                }
            }
    }

    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }

    fun <T> addToRequestQueue(req: Request<T>)
    {
        req.retryPolicy = DefaultRetryPolicy(
            maxTimeoutTime,
            maxNumRetries,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(req)
    }
}
