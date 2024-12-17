package org.example;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class Request {
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final InputStream body;
    private final Map<String, List<String>> queryParams;

    public Request(String method, String path, Map<String, String> headers, InputStream body) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.body = body;
        this.queryParams = parseQueryParams(path);
    }

    private Map<String, List<String>> parseQueryParams(String path) {
        try {
            URI uri = new URI(path);
            String query = uri.getQuery();
            List<NameValuePair> params = URLEncodedUtils.parse(URI.create(query), "UTF-8");
            Map<String, List<String>> queryParams = new HashMap<>();
            for (NameValuePair param : params) {
                queryParams.computeIfAbsent(param.getName(), k -> new ArrayList<>()).add(param.getValue());
            }
            return queryParams;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public InputStream getBody() {
        return body;
    }

    public String getQueryParam(String name) {
        List<String> values = queryParams.get(name);
        return values != null && !values.isEmpty() ? values.get(0) : null;
    }

    public Map<String, List<String>> getQueryParams() {
        return queryParams;
    }
}
