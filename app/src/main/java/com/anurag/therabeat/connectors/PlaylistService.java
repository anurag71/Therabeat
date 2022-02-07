package com.anurag.therabeat.connectors;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.anurag.therabeat.RecyclerViewAdapter;
import com.anurag.therabeat.SingletonInstances;
import com.anurag.therabeat.Song;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlaylistService {
    private ArrayList<Song> playlists = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;
    private String endpoint;

    public PlaylistService(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
        endpoint = "https://api.spotify.com/v1/search?type=track&q=";
    }

    public ArrayList<Song> getPlaylists(Context context, String searchQuery, RecyclerViewAdapter.OnNoteListener listener, RecyclerViewAdapter adapter) {
        Log.d("Playlist", endpoint + searchQuery);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint + searchQuery, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        playlists.clear();
                        Log.d("Response: ", response.toString());
                        Gson gson = new Gson();
                        JSONObject jsonObject = response.optJSONObject("tracks");
                        JSONArray jsonArray = jsonObject.optJSONArray("items");
                        Log.d("Playlist", jsonArray.toString());
                        for (int n = 0; n < jsonArray.length(); n++) {
                            try {
                                JSONObject object = jsonArray.getJSONObject(n);
                                Song song = gson.fromJson(object.toString(), Song.class);
                                playlists.add(song);
                                Log.d("playlist",playlists.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
//                                progressDialog.dismiss();
                            }
                        }
                        Log.d("playlist", playlists.get(0).getName());
//                        adapter.updateEmployeeListItems(playlists);

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
            }};
        SingletonInstances.getInstance(context.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
        return playlists;
    }

    public ArrayList<Song> getAllPlaylists(final VolleyCallBack callBack) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    Gson gson = new Gson();
                    JSONArray jsonArray = response.optJSONArray("items");
                    for (int n = 0; n < jsonArray.length(); n++) {
                        try {
                            JSONObject object = jsonArray.getJSONObject(n);
                            Song song = gson.fromJson(object.toString(), Song.class);
                            playlists.add(song);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    callBack.onSuccess();
                }, error -> {
                    // TODO: Handle error

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sharedPreferences.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
        return playlists;
    }

}
