package com.anurag.therabeat.connectors;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.anurag.therabeat.Playlist;
import com.anurag.therabeat.SingletonInstances;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlaylistService {

    private SharedPreferences sharedPreferences;
    private String endpoint;

    public PlaylistService(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
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

    public void removeFromPlaylist(String uri, Context context) {
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
        Toast toast = Toast.makeText(context.getApplicationContext(), "Please delete songs from the Spotify App", Toast.LENGTH_LONG);
        toast.show();
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
}
