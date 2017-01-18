package br.unb.unbsolidaria;

import java.util.List;

import br.unb.unbsolidaria.entities.News;
import br.unb.unbsolidaria.entities.Opportunity;
import br.unb.unbsolidaria.entities.Organization;
import br.unb.unbsolidaria.entities.User;
import br.unb.unbsolidaria.entities.Voluntary;

/**
 * Created by eduar on 04/01/2017.
 */
public class Singleton {
    private static Singleton ourInstance = new Singleton();

    private User user;

    public static final String usersUrl = "http://164.41.209.169/users/";
    public static final String trabsUrl = "http://164.41.209.169/trabalhos/";

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
