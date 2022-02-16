package com.anurag.therabeat.connectors;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.anurag.therabeat.Playlist;
import com.anurag.therabeat.SingletonInstances;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlaylistService {

    private SharedPreferences sharedPreferences;
    private String endpoint;

    public PlaylistService(Context context) {
        sharedPreferences = context.getSharedPreferences("Therabeat", 0);
        endpoint = "\thttps://api.spotify.com/v1/users/";
    }

    public void createPlaylist(String userId, Context context, SharedPreferences.Editor editor) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", "Therabeat");
        params.put("description", "Your therabeat playlist");
        params.put("public", "false");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, endpoint + userId + "/playlists", new JSONObject(params), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response: ", response.toString());
                        try {
                            Log.d("playlistID", response.get("id").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            editor.putString("playlistId", response.get("id").toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        editor.apply();
                        Gson gson = new Gson();
                        JSONObject jsonObject = response.optJSONObject("tracks");

//                            progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());
//                        progressDialog.dismiss();

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        SingletonInstances.getInstance(context.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    public void addToPlaylist(String uri, Context context) {
        HashMap<String, String[]> params = new HashMap<String, String[]>();
        params.put("uris", new String[]{uri});
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, "https://api.spotify.com/v1/playlists/" + sharedPreferences.getString("playlistId", "") + "/tracks", new JSONObject(params), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response: ", response.toString());
                        Log.d("PlaylistService", "Added to playlist");

                        Gson gson = new Gson();
                        JSONObject jsonObject = response.optJSONObject("tracks");
                        Toast toast = Toast.makeText(context.getApplicationContext(), "The song has been added to your playlist", Toast.LENGTH_LONG);
                        toast.show();

//                            progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());
//                        progressDialog.dismiss();

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        SingletonInstances.getInstance(context.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    public void removeFromPlaylist(String uri, Context context) throws IOException {
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("uri",uri);
//        HashMap<String, String> val = new HashMap<>();
//        JSONArray array=new JSONArray();
//
//
//            JSONObject obj=new JSONObject();
//            try {
//                obj.put("uri",uri);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            array.put(obj);
//        ArrayList<HashMap<String,String>> sample = new ArrayList<>();
//        sample.add(params);
//        val.put("\"tracks\"", "\""+sample.toString()+"\"");
//        Log.d("val",val.toString());
//        Log.d("Response",uri);
//        Log.d("Array",array.toString());
//        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
//                (Request.Method.DELETE, "https://api.spotify.com/v1/playlists/" + sharedPreferences.getString("playlistId", "") + "/tracks", array, new Response.Listener<JSONArray>() {
//
//                    @Override
//                    public void onResponse(JSONArray response) {
//
//                        Log.d("Response: ", response.toString());
//                        Log.d("PlaylistService", "Added to playlist");

//                            progressDialog.dismiss();
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e("Volley", error.getMessage());
////                        progressDialog.dismiss();
//
//                    }
//                }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = new HashMap<>();
//                String token = sharedPreferences.getString("token", "");
//                String auth = "Bearer " + token;
//                headers.put("Authorization", auth);
//                headers.put("Content-Type", "application/json");
//                return headers;
//            }
//        };
//        SingletonInstances.getInstance(context.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
//        RequestParams requestParams = new RequestParams();
//
//        requestParams.put("tracks", "mariyam.shimaanath");
//        requestParams.put("inventory_id", 19);
//        requestParams.put("pending", true);
//
//        String url="https://api.spotify.com/v1/playlists/" + sharedPreferences.getString("playlistId", "") + "/tracks";
//
//        new AsyncHttpClient().delete(url, requestParams, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                String rs = new String(responseBody);
//
//                // do whatever you want
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//
//            }
//        });
//        Retrofit retrofit retrofit= new Retrofit.Builder().
//                .baseUrl("https://api.github.com/")
//                .build();
//
//        RestAdapter.Builder builder = new RestAdapter.Builder()
//                .setRequestInterceptor(new RequestInterceptor() {
//                    @Override
//                    public void intercept(RequestFacade request) {
//                        request.addHeader("Accept", "application/json;versions=1");
//                        if (isUserLoggedIn()) {
//                            request.addHeader("Authorization", getToken());
//                        }
//                    }
//                });
//
//        GitHubService service = retrofit.create(GitHubService.class);

        Thread deleteThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://api.spotify.com/v1/playlists/" + sharedPreferences.getString("playlistId", "") + "/tracks");
                    HttpURLConnection connection = null;
                    JSONObject jo = new JSONObject();
                    try {
                        jo.put("uri", uri);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONArray ja = new JSONArray();
                    ja.put(jo);

                    JSONObject mainObj = new JSONObject();
                    try {
                        mainObj.put("tracks", ja);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String urlParameters = mainObj.toString();
                    Log.d("delete request", urlParameters);
                    try {
                        connection = (HttpURLConnection) url.openConnection();

                        //Setting the request properties and header
//                        connection.setRequestProperty("X-HTTP-Method-Override", "DELETE");
                        connection.setRequestMethod("DELETE");
                        connection.setRequestProperty("Authorization:", "Bearer " + sharedPreferences.getString("token", ""));
                        connection.setRequestProperty("Content-Type", "application/json");


                        connection.setUseCaches(false);
                        connection.setDoInput(true);
                        connection.setDoOutput(true);

                        // Send request
                        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                        wr.writeBytes(urlParameters);
                        wr.flush();
                        wr.close();
                        int responseCode = connection.getResponseCode();
                        Log.d("delete request", String.valueOf(responseCode));
                        // To handle web services which server responds with response code
                        // only
                        try {
                            String response = connection.getResponseMessage();
                            Log.d("delete request", response);
                        } catch (Exception e) {
                            Log.e("delete request", "Cannot convert the input stream to string for the url= , Code response=" + responseCode + "for the JsonObject: " + mainObj.toString());
                        }
                    } catch (
                            Exception e
                    ) {

                        throw e;
                    } finally {

                        if (connection != null) {
                            connection.disconnect();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        deleteThread.start();
        try {
            deleteThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Toast toast = Toast.makeText(context, "The song has been deleted from your playlist, please swipe down to refresh", Toast.LENGTH_LONG);
        toast.show();

//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
//                (Request.Method.PUT, "https://api.spotify.com/v1/playlists/" + sharedPreferences.getString("playlistId", "") + "/tracks", mainObj, new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d("Response: ", response.toString());
//                        Log.d("PlaylistService", "Added to playlist");
//
//                        Gson gson = new Gson();
//                        JSONObject jsonObject = response.optJSONObject("tracks");
//
////                            progressDialog.dismiss();
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e("Volley", error.networkResponse.data.toString());
////                        progressDialog.dismiss();
//
//                    }
//                }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> headers = new HashMap<>();
//                String token = sharedPreferences.getString("token", "");
//                String auth = "Bearer " + token;
//                headers.put("Authorization", auth);
//                headers.put("Content-Type", "application/json");
//                return headers;
//            }
//        };
//        Log.d("delete request",jsonObjectRequest.getBody().toString());
//        Log.d("delete request", mainObj.toString());
//        SingletonInstances.getInstance(context.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    public void getPlaylists(String userId, Context context, SharedPreferences.Editor editor) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, "\thttps://api.spotify.com/v1/users/" + userId + "/playlists", null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        ArrayList<Playlist> playlists = new ArrayList<>();
                        JSONArray jsonArray = response.optJSONArray("items");
                        for (int n = 0; n < jsonArray.length(); n++) {
                            try {
                                JSONObject object = jsonArray.getJSONObject(n);
                                Playlist playlist = gson.fromJson(object.toString(), Playlist.class);
                                playlists.add(playlist);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        boolean flag = false;
                        for (Playlist p : playlists) {
                            if (p.getName().equals("Therabeat")) {
                                editor.putString("playlistId", p.getId());
                                editor.apply();
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            createPlaylist(userId, context, editor);
                        }

//                            progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());
//                        progressDialog.dismiss();

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        SingletonInstances.getInstance(context.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    public void getPlaylist(ImageView playlistImageView, Context context) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, "\thttps://api.spotify.com/v1/playlists/" + sharedPreferences.getString("playlistId", ""), null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("check response", response.toString());
                        Gson gson = new Gson();
                        JSONArray jsonObject = null;
                        try {
                            jsonObject = response.getJSONArray("images");
                            JSONObject urlObject = jsonObject.optJSONObject(0);
                            String url = urlObject.getString("url");//getJSONObject(0).getString("url");
                            Picasso.get().load(url).into(playlistImageView);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


//                            progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());
//                        progressDialog.dismiss();

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        SingletonInstances.getInstance(context.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

}
