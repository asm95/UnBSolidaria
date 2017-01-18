package br.unb.unbsolidaria.views.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.unb.unbsolidaria.Singleton;
import br.unb.unbsolidaria.communication.OpportunityService;
import br.unb.unbsolidaria.communication.OrganizationService;
import br.unb.unbsolidaria.communication.RestCommunication;
import br.unb.unbsolidaria.entities.Opportunity;
import br.unb.unbsolidaria.entities.Organization;
import br.unb.unbsolidaria.entities.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by eduar on 13/01/2017.
 */

public class ViewMyOpprtunities extends Fragment {
    private Singleton singleton = Singleton.getInstance();

    private List<Opportunity> opportunityList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final User org = singleton.getUser();
        OrganizationService organizationService = RestCommunication.createService(OrganizationService.class);
        Call<List<Opportunity>> call = organizationService.getOrganizationOpportunities(org);
        call.enqueue(new Callback<List<Opportunity>>() {
            @Override
            public void onResponse(Call<List<Opportunity>> call, Response<List<Opportunity>> response) {
                opportunityList = response.body();
                Log.i("MyOpportunities","opp: "+opportunityList+" response: "+response.message());
            }

            @Override
            public void onFailure(Call<List<Opportunity>> call, Throwable t) {

            }
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
