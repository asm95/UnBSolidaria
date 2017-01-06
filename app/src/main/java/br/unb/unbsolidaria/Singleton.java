package br.unb.unbsolidaria;

import java.util.List;

import br.unb.unbsolidaria.entities.News;
import br.unb.unbsolidaria.entities.Opportunity;

/**
 * Created by eduar on 04/01/2017.
 */
public class Singleton {
    private static Singleton ourInstance = new Singleton();

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

    public void setOpportunityList(List<Opportunity> opportunityList) {
        this.opportunityList = opportunityList;
    }

    public void setNewsList(List<News> newsList) {
        this.newsList = newsList;
    }
}
