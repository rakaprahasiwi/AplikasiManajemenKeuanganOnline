package net.prahasiwi.laporankeuangan.api;


import net.prahasiwi.laporankeuangan.model.Value;
import net.prahasiwi.laporankeuangan.model.ValueCategory;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RegisterAPI {

    @FormUrlEncoded
    @POST("insert.php")
    Call<Value> insert(@Field("type") String type,
                      @Field("image") String image,
                      @Field("value") String value,
                      @Field("category") String category,
                      @Field("describes") String describes,
                      @Field("email") String email,
                      @Field("dates") String dates);

    @FormUrlEncoded
    @POST("read_today.php")
    Call<Value> read_today(@Field("email") String email,
                           @Field("dates") String dates);


    @FormUrlEncoded
    @POST("update.php")
    Call<Value> update(@Field("id") String id,
                       @Field("type") String type,
                       @Field("image") String image,
                       @Field("value") String value,
                       @Field("category") String category,
                       @Field("describes") String describes,
                       @Field("email") String email,
                       @Field("dates") String dates);

    @FormUrlEncoded
    @POST("delete.php")
    Call<Value> delete(@Field("id") String id);


    @FormUrlEncoded
    @POST("login.php")
    Call<Value> login(@Field("username") String username,
                      @Field("password") String password);
    @FormUrlEncoded
    @POST("register.php")
    Call<Value> register(@Field("username") String username,
                         @Field("email") String email,
                         @Field("password") String password,
                         @Field("no_hp") String no_hp);
    @FormUrlEncoded
    @POST("reset.php")
    Call<Value> reset(@Field("email") String email,
                      @Field("password") String password);

    @FormUrlEncoded
    @POST("income_add_cat.php")
    Call<Value> add_income(@Field("email") String email,
                           @Field("category") String category);

    @FormUrlEncoded
    @POST("income_read.php")
    Call<ValueCategory> read_income(@Field("email") String email);

    @FormUrlEncoded
    @POST("income_update_cat.php")
    Call<Value> update_income(@Field("id") String id,
                              @Field("category") String category);

    @FormUrlEncoded
    @POST("income_delete_cat.php")
    Call<Value> delete_income(@Field("id") String id,
                              @Field("category") String category);

    @FormUrlEncoded
    @POST("outcome_add_cat.php")
    Call<Value> add_outcome(@Field("email") String email,
                            @Field("category") String category);

    @FormUrlEncoded
    @POST("outcome_read.php")
    Call<ValueCategory> read_outcome(@Field("email") String email);

    @FormUrlEncoded
    @POST("outome_update_cat.php")
    Call<Value> update_outcome(@Field("id") String id,
                               @Field("category") String category);

    @FormUrlEncoded
    @POST("outome_delete_cat.php")
    Call<Value> delete_outcome(@Field("id") String id,
                               @Field("category") String category);
    @FormUrlEncoded
    @POST("read_all.php")
    Call<Value> read_all(@Field("email") String email);

    @FormUrlEncoded
    @POST("read_range.php")
    Call<Value> read_range(@Field("email") String email,
                           @Field("date1") String date1,
                           @Field("date2") String date2);


}
