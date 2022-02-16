//package com.anurag.therabeat.connectors;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.Request;
//import com.android.volley.toolbox.HttpResponse;
//
//import org.apache.http.params.HttpConnectionParams;
//import org.apache.http.params.HttpParams;
//
//import java.io.IOException;
//import java.net.URI;
//import java.util.Map;
//
//public class OwnHttpClientStack extends com.android.volley.toolbox.BaseHttpStack {
//    private final static String HEADER_CONTENT_TYPE = "Content-Type";
//
//    public OwnHttpClientStack(HttpClient client) {
//        super(client);
//    }
//
//    @Override
//    public HttpResponse executeRequest(Request<?> request, Map<String, String> additionalHeaders) throws IOException, AuthFailureError {
//        HttpUriRequest httpRequest = createHttpRequest(request, additionalHeaders);
//        addHeaders(httpRequest, additionalHeaders);
//        addHeaders(httpRequest, request.getHeaders());
//        executeRequest(httpRequest);
//        HttpParams httpParams = httpRequest.getParams();
//        int timeoutMs = request.getTimeoutMs();
//        // TODO: Reevaluate this connection timeout based on more wide-scale
//        // data collection and possibly different for wifi vs. 3G.
//        HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
//        HttpConnectionParams.setSoTimeout(httpParams, timeoutMs);
//        return mClient.execute(httpRequest);
//    }
//
//    @Override
//    public HttpResponse performRequest(Request<?> request, Map<String, String> additionalHeaders)
//            throws IOException, AuthFailureError {
//
//    }
//
//    private static void addHeaders(HttpUriRequest httpRequest, Map<String, String> headers) {
//        for (String key : headers.keySet()) {
//            httpRequest.setHeader(key, headers.get(key));
//        }
//    }
//
//    static HttpUriRequest createHttpRequest(Request<?> request, Map<String, String> additionalHeaders) throws AuthFailureError {
//        switch (request.getMethod()) {
//            case Request.Method.DEPRECATED_GET_OR_POST: {
//                byte[] postBody = request.getPostBody();
//                if (postBody != null) {
//                    HttpPost postRequest = new HttpPost(request.getUrl());
//                    postRequest.addHeader(HEADER_CONTENT_TYPE, request.getPostBodyContentType());
//                    HttpEntity entity;
//                    entity = new ByteArrayEntity(postBody);
//                    postRequest.setEntity(entity);
//                    return postRequest;
//                } else {
//                    return new HttpGet(request.getUrl());
//                }
//            }
//            case Request.Method.GET:
//                return new HttpGet(request.getUrl());
//            case Request.Method.DELETE:
//                OwnHttpDelete deleteRequest =  new OwnHttpDelete(request.getUrl());
//                deleteRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
//                setEntityIfNonEmptyBody(deleteRequest, request);
//                return deleteRequest;
//            case Request.Method.POST: {
//                HttpPost postRequest = new HttpPost(request.getUrl());
//                postRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
//                setEntityIfNonEmptyBody(postRequest, request);
//                return postRequest;
//            }
//            case Request.Method.PUT: {
//                HttpPut putRequest = new HttpPut(request.getUrl());
//                putRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
//                setEntityIfNonEmptyBody(putRequest, request);
//                return putRequest;
//            }
//            case Request.Method.HEAD:
//                return new HttpHead(request.getUrl());
//            case Request.Method.OPTIONS:
//                return new HttpOptions(request.getUrl());
//            case Request.Method.TRACE:
//                return new HttpTrace(request.getUrl());
//            case Request.Method.PATCH: {
//                HttpPatch patchRequest = new HttpPatch(request.getUrl());
//                patchRequest.addHeader(HEADER_CONTENT_TYPE, request.getBodyContentType());
//                setEntityIfNonEmptyBody(patchRequest, request);
//                return patchRequest;
//            }
//            default:
//                throw new IllegalStateException("Unknown request method.");
//        }
//    }
//
//    private static void setEntityIfNonEmptyBody(HttpEntityEnclosingRequestBase httpRequest,
//                                                Request<?> request) throws AuthFailureError {
//        byte[] body = request.getBody();
//        if (body != null) {
//            HttpEntity entity = new ByteArrayEntity(body);
//            httpRequest.setEntity(entity);
//        }
//    }
//
//    private static class OwnHttpDelete extends HttpPost {
//        public static final String METHOD_NAME = "DELETE";
//
//        public OwnHttpDelete() {
//            super();
//        }
//
//        public OwnHttpDelete(URI uri) {
//            super(uri);
//        }
//
//        public OwnHttpDelete(String uri) {
//            super(uri);
//        }
//
//        public String getMethod() {
//            return METHOD_NAME;
//        }
//    }
//}