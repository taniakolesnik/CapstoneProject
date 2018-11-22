package uk.co.taniakolesnik.capstoneproject.auth;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static final String API_BASE_URL = "https://github.com/";
    private static final String API_URL =  "https://api.github.com/";

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static  Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    private static  Retrofit.Builder builderApi = new Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create());

    private static  Retrofit retrofit = builder
            .client(httpClient.build())
            .build();

    private static  Retrofit retrofitAPI = builderApi
            .client(httpClient.build())
            .build();

    public static <S> S createService(Class<S> serviceClass){
        return retrofit.create(serviceClass);
    }

    public static <S> S createApiService(Class<S> serviceClass){
        return retrofitAPI.create(serviceClass);
    }

}