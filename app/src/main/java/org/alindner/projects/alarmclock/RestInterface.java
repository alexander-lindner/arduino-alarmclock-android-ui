package org.alindner.projects.alarmclock;

import org.alindner.projects.alarmclock.models.ResultModel;
import org.alindner.projects.alarmclock.models.SettingsModel;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RestInterface {
    @GET("/")
    Call<ResultModel> getList();

    @POST("/")
    @FormUrlEncoded
    Call<Object> add(@Field("name") String name,
                     @Field("time") String time,
                     @Field("monday") boolean monday,
                     @Field("tuesday") boolean tuesday,
                     @Field("wednesday") boolean wednesday,
                     @Field("thursday") boolean thursday,
                     @Field("friday") boolean friday,
                     @Field("saturday") boolean saturday,
                     @Field("sunday") boolean sunday,
                     @Field("special") boolean special);

    @DELETE("/")
    Call<Object> delete(@Query("id") Long id);

    @POST("/settings")
    @FormUrlEncoded
    Call<Object> setIntensity(@Field("intensity") int intensity);

    @POST("/settings")
    @FormUrlEncoded
    Call<Object> setUrl(@Field("streamurl") String url);

    @GET("/settings")
    Call<SettingsModel> getSettings();
}
