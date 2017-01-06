package br.unb.unbsolidaria.communication;

import org.json.JSONObject;

import br.unb.unbsolidaria.entities.Voluntary;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Created by eduar on 05/01/2017.
 */

public interface VoluntaryService {
    @POST()
    Call<Voluntary> postVoluntary(@Body Voluntary voluntary);

    @PUT()
    Call<Voluntary> putVoluntary(@Body Voluntary voluntary);

    @POST("rest-auth/login/")
    Call<Voluntary> login(@Body JSONObject json);
}
