package com.instantsystem.demo.util;

import com.instantsystem.demo.exception.RestCallException;
import com.instantsystem.demo.exception.UnexpectedHttpMethodException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;

public class WebClientManager {

    private static final Logger LOG = LoggerFactory.getLogger(WebClientManager.class.getName());
    WebClient client;

    public WebClientManager() {
        client = WebClient.create();
    }

    /**
     * Call a URL using {@link WebClient}
     * @param url           The URL to reach
     * @param httpMethod    The Http method to use
     * @param city          THe city this Url is reach for (Log and exception use mostly)
     * @return  The Response as a {@link String}
     */
    public String makeHttpCall(String url, String httpMethod, String city) {
        LOG.info("Making HTTP Call to url {}, with method {} for city {}", url, httpMethod, city);
        String body = null;
        try {
            body = client
                    .method(extractHttpMethod(httpMethod, city, url))
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            // TODO Instead of two catch, create a handle exception method, with specific exception (Timeout, bad request, etc...)
        } catch (UnexpectedHttpMethodException uhme) {
            //Already logged and thrown, just rethrow
            throw uhme;
        }catch (Exception e) {
            String message = String.format("Failed to reach the url : %s, with method : %s, for the city : %s", url, httpMethod, city);
            LOG.error(message, e);
            throw new RestCallException(message);
        }
        return body;
    }

    /**
     * Retrieve the {@link HttpMethod} from a given String
     * @param method    The method as String
     * @param city      The city we are doing the call for (LOG and exception uses)
     * @param url       The URL we are doing the call for (LOG and exception uses)
     * @return  The Http Method extracted
     * @throws UnexpectedHttpMethodException if the method is incorrect or not in the list
     */
    private HttpMethod extractHttpMethod(String method, String city, String url) {
        method = method.toLowerCase();
        HttpMethod httpMethod;
        switch (method) {
            case "get" :
                httpMethod = HttpMethod.GET;
                break;
            case "post" :
                httpMethod = HttpMethod.POST;
                break;
            case "put" :
                httpMethod = HttpMethod.PUT;
                break;
            case "delete" :
                httpMethod = HttpMethod.DELETE;
                break;
            case "patch" :
                httpMethod = HttpMethod.PATCH;
                break;
            default:
                String message = new StringBuilder("Failed to process the request for the city : ")
                        .append(city)
                        .append(", at url : ")
                        .append(url)
                        .append(", with method : ")
                        .append(method)
                        .append(".\nPlease contact administrator, configuration is wrong for this city endpoint")
                    .toString();
                LOG.error(message);
                throw new UnexpectedHttpMethodException(message);
        }
        return httpMethod;
    }
}
