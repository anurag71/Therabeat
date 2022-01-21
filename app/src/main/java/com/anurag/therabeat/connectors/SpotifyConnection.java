package com.anurag.therabeat.connectors;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class SpotifyConnection {

    private static final String CLIENT_ID = "a98fdf7072d24d9dbf8999a6d74212b0";
    private static final String REDIRECT_URI = "http://com.anurag.therabeat/callback";
    private static final int REQUEST_CODE = 1337;
    private static final String SCOPES = "user-read-recently-played,user-library-modify,user-read-email,user-read-private,playlist-read-private";
    public static final String AUTH_TOKEN = "AUTH_TOKEN";

    public static SpotifyAppRemote mSpotifyAppRemote;
    public static String webApiError;
    public static String playerConnectionError;

    public SpotifyConnection(Context context) {
        getPlayerInstance(context);
    };

    public void getPlayerInstance(Context context){
            Log.d("SpotifyConnector","Inside");
            ConnectionParams connectionParams =
                    new ConnectionParams.Builder(CLIENT_ID)
                            .setRedirectUri(REDIRECT_URI)
                            .showAuthView(true)
                            .build();
            Log.d("SpotifyConnector","Connected");
            SpotifyAppRemote.connect(context, connectionParams,
                    new Connector.ConnectionListener() {

                        public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                            Log.d("SpotifyConnection",spotifyAppRemote.toString());
                            mSpotifyAppRemote = spotifyAppRemote;
                            Log.d("SpotifyConnection",mSpotifyAppRemote.toString());
                            Log.d("MainActivity", "Connected! Yay!");

                        }

                        public void onFailure(Throwable throwable) {
                            Log.d("MyActivity", throwable.getMessage(), throwable);

                            // Something went wrong when attempting to connect! Handle errors here
                        }
                    });
    }

    public void openLoginWindow(Context context) {

        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN,REDIRECT_URI);

        builder.setScopes(new String[]{SCOPES});

        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginActivity((Activity) context,REQUEST_CODE,request);
    }
}
