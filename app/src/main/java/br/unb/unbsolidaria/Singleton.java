package br.unb.unbsolidaria;

import java.util.List;

import br.unb.unbsolidaria.entities.Opportunity;

/**
 * Created by eduar on 04/01/2017.
 */
public class Singleton {
    private static Singleton ourInstance = new Singleton();

    private List<Opportunity> mList;

    public void setmList(List<Opportunity> mList) {
        this.mList = mList;
    }

    public List<Opportunity> getmList() {

        return mList;
    }

    public static Singleton getInstance() {
        return ourInstance;
    }

    private Singleton() {
    }
}
