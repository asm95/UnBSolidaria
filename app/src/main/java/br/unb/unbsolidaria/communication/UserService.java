package br.unb.unbsolidaria.communication;

import br.unb.unbsolidaria.entities.Login;
import br.unb.unbsolidaria.entities.RetrofitResponse;
import br.unb.unbsolidaria.entities.Signup;
import br.unb.unbsolidaria.entities.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Created by eduar on 06/01/2017.
 */

public interface UserService {
    @POST("rest-auth/login/")
    Call<User> login(@Body Login login);

    @POST("get_user/")
    Call<User> getUser(@Body User user);

    @POST("rest-auth/registration/")
    Call<User> newUser(@Body Signup signup);

    @PUT("set_user/")
    Call<RetrofitResponse> setUser(@Body User user);

    @PUT("set_user/")
    Call<RetrofitResponse> updateUser(@Body User user);
}
