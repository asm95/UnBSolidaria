package br.unb.unbsolidaria;

import java.util.List;

import br.unb.unbsolidaria.communication.RestCommunication;
import br.unb.unbsolidaria.entities.News;
import br.unb.unbsolidaria.entities.Opportunity;
import br.unb.unbsolidaria.entities.User;

/**
 * Created by eduar on 04/01/2017.
 */
public class Singleton {
    private static Singleton ourInstance = new Singleton();

    private User user;

    public static final String USERS_URL = RestCommunication.API_BASE_URL + "users/";
    public static final String trabsUrl = RestCommunication.API_BASE_URL + "trabalhos/";

    private List<Opportunity> opportunityList;
    private List<News> newsList;

    public static Singleton getInstance() {
        return ourInstance;
    }

    private Singleton() {
    }

    public List<Opportunity> getOpportunityList() {
        return opportunityList;
    }

    public List<News> getNewsList() {
        return newsList;
    }

    public User getUser(){return this.user;}

    public void setUser(User user){
        this.user = user;
    }

    public void setOpportunityList(List<Opportunity> opportunityList) {
        this.opportunityList = opportunityList;
    }

    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
    }
}
