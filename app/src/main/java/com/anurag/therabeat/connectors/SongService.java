package com.anurag.therabeat.connectors;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

    public ArrayList<Song> searchSongs(Context context, String searchQuery, RecyclerViewAdapter.OnNoteListener listener, RecyclerViewAdapter adapter) {
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
                        for (int n = 0; n < jsonArray.length(); n++) {
                            try {
                                JSONObject object = jsonArray.getJSONObject(n);
                                JSONArray artistObj = object.optJSONArray("artists");
                                JSONArray imageObj = object.optJSONObject("album").optJSONArray("images");
                                Log.d("check length", String.valueOf(artistObj.length()));
                                StringBuilder artists = new StringBuilder();
                                artists.append(artistObj.getJSONObject(0).getString("name"));
                                for (int i = 1; i < artistObj.length(); i++) {
                                    JSONObject obj1 = artistObj.getJSONObject(i);
                                    artists.append(", " + obj1.get("name"));
//                                    Log.d("check name",obj1.optJSONObject("name").toString());
                                }
                                Song song = gson.fromJson(object.toString(), Song.class);
                                song.setArtist(artists.toString());
                                song.setImageUrl(imageObj.getJSONObject(1).getString("url"));
                                playlists.add(song);
                            } catch (JSONException e) {
                                e.printStackTrace();
//                                progressDialog.dismiss();
                            }
                        }
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

    public ArrayList<Song> getPlaylistSongs(Context context, RecyclerViewAdapter.OnNoteListener listener, RecyclerView myView, SwipeRefreshLayout mSwipeRefreshLayout, TextView viewById) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, "https://api.spotify.com/v1/playlists/" + sharedPreferences.getString("playlistId", "") + "/tracks", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response: ", response.toString());
                        playlistssongs.clear();
                        Gson gson = new Gson();
                        JSONArray jsonArray = response.optJSONArray("items");
                        for (int n = 0; n < jsonArray.length(); n++) {
                            try {
                                JSONObject object = jsonArray.getJSONObject(n);
                                object = object.optJSONObject("track");
                                Log.d("object", object.toString());
                                JSONArray artistObj = object.optJSONArray("artists");
                                JSONArray imageObj = object.optJSONObject("album").optJSONArray("images");
                                Log.d("check length", String.valueOf(artistObj.length()));
                                StringBuilder artists = new StringBuilder();
                                artists.append(artistObj.getJSONObject(0).getString("name"));
                                for (int i = 1; i < artistObj.length(); i++) {
                                    JSONObject obj1 = artistObj.getJSONObject(i);
                                    artists.append(", " + obj1.get("name"));
//                                    Log.d("check name",obj1.optJSONObject("name").toString());
                                }
                                Song song = gson.fromJson(object.toString(), Song.class);
                                song.setArtist(artists.toString());
                                song.setImageUrl(imageObj.getJSONObject(1).getString("url"));
                                playlistssongs.add(song);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        }
                        if (playlistssongs.size() != 0) {
                            myView.setAdapter(new RecyclerViewAdapter(playlistssongs, listener, R.menu.playlist_list_songs_recycler_view_menu));
                            viewById.setVisibility(View.GONE);
                            myView.setVisibility(View.VISIBLE);
                        } else {
                            myView.setVisibility(View.GONE);
                            viewById.setVisibility(View.VISIBLE);
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
//                        adapter.updateEmployeeListItems(playlists);
//                            progressDialog.dismiss();

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());
                        mSwipeRefreshLayout.setRefreshing(false);

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
