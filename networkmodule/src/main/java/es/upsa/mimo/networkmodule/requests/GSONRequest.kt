package es.upsa.mimo.networkmodule.requests

import android.content.Context
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

class GSONRequest<T>(url: String, private val clazz: Class<T>, private val headers: MutableMap<String, String>?,
    private val listener: Response.Listener<T>, errorListener: Response.ErrorListener) : Request<T>(Method.GET, url, errorListener)
{
    private val gson = Gson()

    override fun getHeaders(): MutableMap<String, String> = headers ?: super.getHeaders()

    override fun deliverResponse(response: T) = listener.onResponse(response)

    override fun parseNetworkResponse(response: NetworkResponse?): Response<T>
    {
        return try
        {
            val json = String(response?.data ?: ByteArray(0), Charset.forName(HttpHeaderParser.parseCharset(response?.headers)))
            Response.success(gson.fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders(response))
        }
        catch (e: ServerError)
        {
            retryPolicy.retry(e)
            Response.error(ParseError(e))
        }
        catch (e: UnsupportedEncodingException)
        {
            Response.error(ParseError(e))
        }
        catch (e: JsonSyntaxException)
        {
            Response.error(ParseError(e))
        }
    }

    override fun deliverError(error: VolleyError?)
    {
        super.deliverError(error)
        if (error is ServerError)
            Response.error<ServerError>(ParseError(error))
    }
}
