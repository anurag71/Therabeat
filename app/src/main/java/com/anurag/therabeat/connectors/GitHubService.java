package com.anurag.therabeat.connectors;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HTTP;
import retrofit2.http.Path;

public interface GitHubService {
    @HTTP(method = "DELETE", path = "\thttps://api.spotify.com/v1/playlists/{playlist_id}/tracks", hasBody = true)
    Call<ResponseBody> getData(@Path("playlist_id") String postfix, @Body Map<String, Object> options);
}
