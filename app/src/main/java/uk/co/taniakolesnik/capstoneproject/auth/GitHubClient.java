package uk.co.taniakolesnik.capstoneproject.auth;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GitHubClient  {

    // POST https://github.com/login/oauth/access_token

    @Headers("Accept: application/json")
    @POST("login/oauth/access_token")
    @FormUrlEncoded
    Call<AccessToken> getAccessToken(
            @Field("client_id") String clientID,
            @Field("client_secret") String clientSecret,
            @Field("code") String code
    );

    // https://api.github.com/user

    @Headers("Accept: application/json")
    @GET("user")
    Call<GitHubUser> getUserInfo(
            @Header("Authorization") String token);
}