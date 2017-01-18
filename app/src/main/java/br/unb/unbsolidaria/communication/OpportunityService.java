package br.unb.unbsolidaria.communication;

import java.util.List;

import br.unb.unbsolidaria.entities.Opportunity;
import br.unb.unbsolidaria.entities.Organization;
import br.unb.unbsolidaria.entities.RetrofitResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OpportunityService {
    //Post de uma organizacao
    @POST("trabalhos/")
    Call<Opportunity> postOpportunities(@Body Opportunity opportunity);

    @PUT("edit_trabalho/")
    Call<RetrofitResponse> updateOpportunitie(@Body Opportunity opportunity);

    //Get lista de organizacoes
    @GET("trabalhos/")
    Call<List<Opportunity>> getOpportunities();

    /*//Get lista de organizacoes por nome
    @GET("/organizations")
    Call<List<Organization>> getOrganizationByName(@Query("name") String name);
*/}
