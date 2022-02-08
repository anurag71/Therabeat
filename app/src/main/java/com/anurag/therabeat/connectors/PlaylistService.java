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
import com.anurag.therabeat.SingletonInstances;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PlaylistService {

    private SharedPreferences sharedPreferences;
    private RequestQueue queue;
    private String endpoint;

    public PlaylistService(Context context) {
        sharedPreferences = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
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
}
