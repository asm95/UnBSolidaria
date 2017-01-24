package br.unb.unbsolidaria.views.organization;

import android.app.ProgressDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

import br.unb.unbsolidaria.R;
import br.unb.unbsolidaria.Singleton;
import br.unb.unbsolidaria.communication.OpportunityService;
import br.unb.unbsolidaria.communication.OrganizationService;
import br.unb.unbsolidaria.communication.RestCommunication;
import br.unb.unbsolidaria.entities.User;
import br.unb.unbsolidaria.utils.OpportunitiesAdapter;
import br.unb.unbsolidaria.entities.Opportunity;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by criss on 04/12/2016.
 */

public class ViewOpportunities extends Fragment {

    View parentView;

    RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    List<Opportunity> mList;

    ProgressDialog mProgressDialog;

    private Singleton singleton = Singleton.getInstance();

    public enum requestType{
        AllOpportunities, OrganizationOpportunities;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_opportunities_list, container, false);

        mRecyclerView = (RecyclerView) parentView.findViewById(R.id.my_recycler_oportunity_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        Bundle box = getArguments();
        requestType userAction = (requestType) box.getSerializable("br.unb.unbsolidaria.ACTION");
        if (userAction == null)
            userAction = requestType.AllOpportunities;
        fetchListMain(userAction);

        return parentView;
    }

    void fetchListMain(requestType rt){
        mProgressDialog = new ProgressDialog(getContext());
        mProgressDialog.setTitle("Carregando...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        switch (rt){
            case AllOpportunities:
                updateListOpportunities(); break;
            case OrganizationOpportunities:
                fetchListByOrganization(); break;
        }
    }

    void onFetchListSuccess(){
        singleton.setOpportunityList(mList);

        mAdapter = new OpportunitiesAdapter(getActivity(), mList);
        mRecyclerView.setAdapter(mAdapter);

        mProgressDialog.dismiss();

        //TODO: discuss user model implementation
        //Box is used only in Voluntary application side because the join in button
        //OpportunityActivity should be hidden to Organization.
        //Bundle box = br.unb.unbsolidaria.views.organization.ViewOpportunities.this.getArguments();
    }

    void onFetchListFail(){
        mList = new LinkedList<>();
        mAdapter = new OpportunitiesAdapter(getActivity(), mList);
        mRecyclerView.setAdapter(mAdapter);

        mProgressDialog.dismiss();
        Snackbar.make(mRecyclerView, getString(R.string.rest_server_connection_error), Snackbar.LENGTH_LONG).show();
    }


    //NOTE: copied from voluntary.viewOpportunities
    void updateListOpportunities(){
        OpportunityService opportunityService = RestCommunication.createService(OpportunityService.class);
        Call<List<Opportunity>> call = opportunityService.getOpportunities();
        call.enqueue(new Callback<List<Opportunity>>() {
            @Override
            public void onResponse(Call<List<Opportunity>> call, Response<List<Opportunity>> response) {
                Log.i("RESTAPI","Trabalhos response: " + response.body());

                mList = response.body();
                singleton.setOpportunityList(mList);
                if (mList == null){
                    onFetchListFail();
                    return;
                }

                onFetchListSuccess();
            }
            @Override
            public void onFailure(Call<List<Opportunity>> call, Throwable t) {
                onFetchListFail();
            }
        });
    }

    void fetchListByOrganization(){
        final User org = singleton.getUser();
        //Hot-fix for communication problem (less data is sent)
        String json_data = "{\"organizacao\":\"" + org.getId() + "\"}";
        RequestBody rb = RequestBody.create(MediaType.parse("application/json"), json_data);

        OrganizationService organizationService = RestCommunication.createService(OrganizationService.class);
        Call<List<Opportunity>> call = organizationService.getOrganizationOpportunities(rb);
        call.enqueue(new Callback<List<Opportunity>>() {
            @Override
            public void onResponse(Call<List<Opportunity>> call, Response<List<Opportunity>> response) {
                mList = response.body();
                Log.i("MyOpportunities"," response for ID " + org.getId() + ": "+response.message() + " code: " + response.code() );

                if (mList == null){
                    onFetchListFail();
                    return;
                }

                onFetchListSuccess();
            }

            @Override
            public void onFailure(Call<List<Opportunity>> call, Throwable t) {
                onFetchListFail();
            }
        });
    }
}
