package es.upsa.mimo.networkmodule

import java.net.URLEncoder

fun String.addParametersToURL(parameters: Map<String, String>): String
{
    var result = this
    if (result.endsWith("/"))
        result = result.substring(0, result.lastIndexOf("/"))

    var first = true
    for (param in parameters)
    {
        if (first)
        {
            first = false
            result += "?${param.key}=${URLEncoder.encode(param.value, "UTF-8")}"
        }
        else
            result += "&${param.key}=${URLEncoder.encode(param.value, "UTF-8")}"
    }

    return result
}
