package com.example.canteenchecker.canteenmanager.proxy;

import java.io.IOException;
import java.util.Collection;

import com.example.canteenchecker.canteenmanager.CanteenManagerApplication1;
import com.example.canteenchecker.canteenmanager.domainobjects.Canteen;
import com.example.canteenchecker.canteenmanager.domainobjects.CanteenRating;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public class ServiceProxyManager {

    private static final String TAG = ServiceProxyManager.class.getName();

    private static final String SERVICE_BASE_URL = "https://canteenchecker.azurewebsites.net/";

    private final Proxy proxy = new Retrofit.Builder()
            .baseUrl(SERVICE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Proxy.class);

    public Canteen getCanteen() throws IOException {
        if (!CanteenManagerApplication1.getInstance().isAuthenticated()) {
            return null;
        }
        String token = "Bearer " + CanteenManagerApplication1.getInstance().getAuthenticationToken();

        // Request req = proxy.getCanteen(token).request();
        //Log.i(TAG, "proxy request  " + req.toString());
        //Headers h = proxy.getCanteen(token).request().headers();

        ///h.toMultimap().forEach((k, v) -> {
        //   Log.i(TAG, String.format("header '%s'   value: '%s'", k, v));
        //});

        //Log.i(TAG, "proxy request  " + req.toString());

        //String res = proxy.getCanteen(token).execute().raw().toString();
        //Log.i(TAG, "proxy response  " + res);

        ProxyCanteen canteen = proxy.getCanteen(token).execute().body();
        if (canteen == null) {
            return null;
        }
        return canteen.toCanteen();
    }

    public boolean updateCanteen(Canteen c) throws IOException {
        if (!CanteenManagerApplication1.getInstance().isAuthenticated()) {
            return false;
        }
        String token = "Bearer " + CanteenManagerApplication1.getInstance().getAuthenticationToken();

        ProxyCanteenNoRatings cnr = new ProxyCanteenNoRatings(c);
        return proxy.updateCanteen(token, cnr).execute().isSuccessful();
    }

    public boolean deleteRating(int ratingId) throws IOException {
        if (!CanteenManagerApplication1.getInstance().isAuthenticated()) {
            return false;
        }
        String token = "Bearer " + CanteenManagerApplication1.getInstance().getAuthenticationToken();
        return proxy.deleteRating(token, ratingId).execute().isSuccessful();
    }

    public String authenticate(String userName, String password) throws IOException {
        return proxy.postLogin(new ProxyLogin(userName, password)).execute().body();
    }

    private interface Proxy {
        @GET("/Admin/Canteen")
        Call<ProxyCanteen> getCanteen(@Header("Authorization") String token);

        @DELETE("/Admin/Canteen/Rating/{id}")
        Call<ResponseBody> deleteRating(@Header("Authorization") String token, @Path("id") int id);

        @PUT("/Admin/Canteen")
        Call<ResponseBody> updateCanteen(@Header("Authorization") String token, @Body ProxyCanteenNoRatings c);

        @POST("/Admin/Login")
        Call<String> postLogin(@Body ProxyLogin login);
    }

    private static class ProxyCanteen {

        int canteenId;
        String name;
        String phone;
        String website;
        String meal;
        float mealPrice;
        float averageRating;
        int averageWaitingTime;
        String address;
        Collection<CanteenRating> ratings;

        Canteen toCanteen() {
            return new Canteen(canteenId, name, phone, website, meal, mealPrice, averageRating, averageWaitingTime, address, ratings);
        }
    }

    private static class ProxyCanteenNoRatings {

        int canteenId;
        String name;
        String phone;
        String website;
        String meal;
        float mealPrice;
        float averageRating;
        int averageWaitingTime;
        String address;

        public ProxyCanteenNoRatings(Canteen c) {
            this.canteenId = c.getCanteenId();
            this.name = c.getName();
            this.phone = c.getPhone();
            this.website = c.getWebsite();
            this.meal = c.getMeal();
            this.mealPrice = c.getMealPrice();
            this.averageRating = c.getAverageRating();
            this.averageWaitingTime = c.getAverageWaitingTime();
            this.address = c.getAddress();
        }
    }

    private static class ProxyLogin {
        final String username;
        final String password;

        ProxyLogin(String userName, String password) {
            this.username = userName;
            this.password = password;
        }
    }
}
