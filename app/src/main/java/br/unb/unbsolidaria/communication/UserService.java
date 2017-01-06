package br.unb.unbsolidaria.communication;

import br.unb.unbsolidaria.entities.Login;
import br.unb.unbsolidaria.entities.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by eduar on 06/01/2017.
 */

public interface UserService {
    @POST("/rest-auth/login/")
    Call<User> login(@Body Login login);

    @POST("/get_user/")
    Call<User> getUser(@Body User user);
}
