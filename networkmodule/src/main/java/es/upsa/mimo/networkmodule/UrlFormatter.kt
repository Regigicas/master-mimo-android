package es.upsa.mimo.networkmodule

import java.net.URLEncoder

class UrlFormatter
{
    companion object
    {
        @JvmStatic
        fun addParametersToURL(url: String, parameters: Map<String, String>): String
        {
            var result = url;
            if (result.endsWith("/"))
                result = result.substring(0, result.lastIndexOf("/"));

            var first = true;
            for (param in parameters)
            {
                if (first)
                {
                    first = false;
                    result += "?${param.key}=${URLEncoder.encode(param.value, "UTF-8")}";
                }
                else
                    result += "&${param.key}=${URLEncoder.encode(param.value, "UTF-8")}";
            }

            return result;
        }
    }
}
