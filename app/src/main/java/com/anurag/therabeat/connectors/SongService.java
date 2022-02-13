package com.anurag.therabeat.connectors;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.anurag.therabeat.R;
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

public class SongService {
    private ArrayList<Song> playlists = new ArrayList<>();
    private ArrayList<Song> playlistssongs = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private RequestQueue queue;
    private String endpoint;

    public SongService(Context context) {
        sharedPreferences = context.getSharedPreferences("Therabeat", 0);
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
                        adapter.updateEmployeeListItems(playlists);

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

    public ArrayList<Song> getPlaylistSongs(Context context, RecyclerViewAdapter.OnNoteListener listener, RecyclerView myView, ProgressDialog progressDialog, TextView viewById) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, "https://api.spotify.com/v1/playlists/" + sharedPreferences.getString("playlistId", "") + "/tracks", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        playlistssongs.clear();
                        Log.d("Response: ", response.toString());
                        Gson gson = new Gson();
                        JSONArray jsonArray = response.optJSONArray("items");
                        Log.d("Playlist", jsonArray.toString());
                        for (int n = 0; n < jsonArray.length(); n++) {
                            try {
                                JSONObject object = jsonArray.getJSONObject(n);
                                object = object.optJSONObject("track");
                                Log.d("object", object.toString());
                                Song song = gson.fromJson(object.toString(), Song.class);
                                playlistssongs.add(song);
                                Log.d("playlist", playlistssongs.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                            }
                        }
                        if (playlistssongs.size() != 0) {
                            Log.d("playlist", playlistssongs.get(0).getName());
                            myView.setAdapter(new RecyclerViewAdapter(playlistssongs, listener, R.menu.playlist_list_songs_recycler_view_menu));
                            viewById.setVisibility(View.GONE);
                            myView.setVisibility(View.VISIBLE);
                        } else {
                            viewById.setVisibility(View.VISIBLE);
                        }
                        progressDialog.dismiss();
//                        adapter.updateEmployeeListItems(playlists);
//                            progressDialog.dismiss();

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());
                        progressDialog.dismiss();

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
        return playlistssongs;
    }



}
