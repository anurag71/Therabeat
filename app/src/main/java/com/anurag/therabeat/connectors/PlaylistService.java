package com.anurag.therabeat.connectors;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
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
        sharedPreferences = SingletonInstances.getInstance(context.getApplicationContext()).getSharedPreferencesInstance();
        endpoint = "\thttps://api.spotify.com/v1/users/";
    }

    public void createPlaylist(String userId, Context context, SharedPreferences.Editor editor) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", "TherabeatAttention");
        params.put("description", "Your therabeat playlist for Attention");
        params.put("public", "false");
        JsonObjectRequest AttentionCreationRequest = new JsonObjectRequest
                (Request.Method.POST, endpoint + userId + "/playlists", new JSONObject(params), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            editor.putString("AttentionplaylistId", response.get("id").toString());

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
        params = new HashMap<String, String>();
        params.put("name", "TherabeatMemory");
        params.put("description", "Your therabeat playlist for Memory");
        params.put("public", "false");
        JsonObjectRequest MemoryCreationRequest = new JsonObjectRequest
                (Request.Method.POST, endpoint + userId + "/playlists", new JSONObject(params), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            editor.putString("MemoryplaylistId", response.get("id").toString());

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
        params = new HashMap<String, String>();
        params.put("name", "TherabeatAnxiety");
        params.put("description", "Your therabeat playlist for Anxiety");
        params.put("public", "false");
        JsonObjectRequest AnxietyCreationRequest = new JsonObjectRequest
                (Request.Method.POST, endpoint + userId + "/playlists", new JSONObject(params), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            editor.putString("AnxietyplaylistId", response.get("id").toString());

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
        SingletonInstances.getInstance(context.getApplicationContext()).addToRequestQueue(AttentionCreationRequest);
        SingletonInstances.getInstance(context.getApplicationContext()).addToRequestQueue(MemoryCreationRequest);
        SingletonInstances.getInstance(context.getApplicationContext()).addToRequestQueue(AnxietyCreationRequest);
    }

    public void addToPlaylist(String uri, Context context) {
        String mode = sharedPreferences.getString("mode", "Memory");
        HashMap<String, String[]> params = new HashMap<String, String[]>();
        params.put("uris", new String[]{uri});
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, "https://api.spotify.com/v1/playlists/" + sharedPreferences.getString(mode + "playlistId", "") + "/tracks", new JSONObject(params), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

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
        int reponsecode;
        String mode = sharedPreferences.getString("mode", "Memory");
        Thread deleteThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://api.spotify.com/v1/playlists/" + sharedPreferences.getString(mode + "playlistId", "") + "/tracks");
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
                        // To handle web services which server responds with response code
                        // only
                        Log.d("response", String.valueOf(responseCode));
                        if (responseCode == 200) {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                public void run() {
                                    Toast toast = Toast.makeText(context, "The song has been deleted from your playlist, please swipe down to refresh", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            });
                        } else {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                public void run() {
                                    Toast toast = Toast.makeText(context, "There was an error removing the song", Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            });

                        }
                        try {
                            String response = connection.getResponseMessage();
                        } catch (Exception e) {
                            Log.e("delete request", "Cannot convert the input stream to string for the url= , Code response=" + responseCode + "for the JsonObject: " + mainObj.toString());
                        }
                    } catch (Exception e) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            public void run() {
                                Toast toast = Toast.makeText(context, "There was an error removing the song", Toast.LENGTH_LONG);
                                toast.show();
                            }
                        });
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
                        int count = 0;
                        for (Playlist p : playlists) {
                            if (p.getName().equals("TherabeatMemory")) {
                                editor.putString("MemoryplaylistId", p.getId());
                                editor.apply();
                                count++;
                                flag = true;
                            } else if (p.getName().equals("TherabeatAttention")) {
                                editor.putString("AttentionplaylistId", p.getId());
                                editor.apply();
                                count++;
                                flag = true;
                            } else if (p.getName().equals("TherabeatAnxiety")) {
                                editor.putString("AnxietyplaylistId", p.getId());
                                editor.apply();
                                count++;
                                flag = true;
                            }
                            if (count == 3) {
                                break;
                            }
                        }
                        if (!flag) {
                            createPlaylist(userId, context, editor);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", error.toString());

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
        String mode = sharedPreferences.getString("mode", "Memory");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, "\thttps://api.spotify.com/v1/playlists/" + sharedPreferences.getString(mode + "playlistId", ""), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Gson gson = new Gson();
                        JSONArray jsonObject = null;
                        try {
                            jsonObject = response.getJSONArray("images");
                            if (jsonObject.length() != 0) {
                                JSONObject urlObject = jsonObject.optJSONObject(0);
                                String url = urlObject.getString("url");//getJSONObject(0).getString("url");
                                Picasso.get().load(url).into(playlistImageView);
                            }
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
