package br.unb.unbsolidaria.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import br.unb.unbsolidaria.entities.Opportunity;
import br.unb.unbsolidaria.entities.Organization;
import br.unb.unbsolidaria.entities.Tag;
import br.unb.unbsolidaria.entities.User;
import br.unb.unbsolidaria.entities.Voluntary;

public class DBHandler {
    private static DBHandler instance;

    private Context Context;
    private DBSQL sql_interface;

    private List<Voluntary> voluntaries;
    private List<Organization> organizations;
    private List<Tag> tags;
    private List<Opportunity> opportunities;
    private List<User> users;

    private LinkedHashMap<Integer, LinkedList<Integer>> org_opportunityList;

    private DBHandler (){}

    public static DBHandler setUp(Context ctx){
        if (instance == null && ctx != null){
            instance = new DBHandler();
            instance.Context = ctx;
            instance.sql_interface = DBSQL.getInstance(ctx);
            if (instance.sql_interface.isNew() && DBSQL.mockDataBase){
                instance.sql_interface.dbMockWorker();
            }
            instance.loadSQLDataBase();
        }

        return instance;
    }

    public void loadSQLDataBase (){
        opportunities = sql_interface.getAllOpportunities();
        organizations = sql_interface.getAllOrganizations();
        voluntaries = sql_interface.getAllVoluntaries();
        users = sql_interface.getAllUsers();
    }

    public static DBHandler getInstance() {
        return instance;
    }


    public boolean addOpportunity(Opportunity deploy){
        if (deploy == null || deploy.getOrganization() == null)
            return false;

        opportunities.add(deploy);
        sql_interface.addOpportunity(deploy);

        //addOrganizationOpportunity(org, newOpportunity);

        return true;
    }

    public boolean addOrganization(Organization deploy){
        if (deploy == null || deploy.getId() < 0)
            return false;

        sql_interface.addOrganization(deploy);
        organizations.add(deploy);

        return true;
    }

    public boolean addVoluntary(Voluntary deploy) {
        if (deploy == null || deploy.getId() < 0)
            return false;

        sql_interface.addVoluntary(deploy);
        voluntaries.add(deploy);

        return true;
    }

    public boolean addUser(User deploy){
        if (deploy == null || deploy.getId() < 0)
            return false;

        sql_interface.addUser(deploy);
        users.add(deploy);

        return true;
    }

    /**
     * Links an organization to an opportunity element. This follows one to many relationship.
     * @return True if the link could be established
     */
    public boolean addOrganizationOpportunity(Organization org, Opportunity opt){
        if (org == null || opt == null)
            return false;

        if ( !org_opportunityList.containsKey(org.getId()) ){
            //create Organization entry on that list
            LinkedList<Integer> newList = new LinkedList<>();
            newList.add(opt.getID());
            org_opportunityList.put(org.getId(), newList);
            return true;
        }

        LinkedList<Integer> currentList = org_opportunityList.get(org.getId());
        currentList.add(opt.getID());

        return true;
    }


    public Opportunity getOpportunity (int id){
        return opportunities.get(id-1);
    }

    public List<Opportunity> getOpportunities() { return opportunities; }

    public Organization getOrganization (int id){
        return organizations.get(id-1);
    }

    public boolean updateOrganization (Organization deploy){
        int numRowsUpdated = sql_interface.updateOrganization(deploy);

        if (numRowsUpdated > 1)
            Log.w("DBHandler", "updateOrganization: " + numRowsUpdated + " entities have the same ID in the database");

        return (numRowsUpdated>=1);
    }

    public Voluntary getVoluntary (int id){
        return voluntaries.get(id-1);
    }

    public User getUserByCredentials(String email, String password){

        //Check first the in-memory database (log-n)
        for (User usr : users){
            if (usr.getLogin().equals(email) && usr.getPassword().equals(password)){
                return usr;
            }
        }

        return sql_interface.getUserFromCredentials(email, password);
    }

    public int getOpportunityCount(){ return opportunities.size(); }

    public int getOrganizationCount() { return organizations.size(); }

    public int getVoluntaryCount() { return voluntaries.size(); }

    public int getUserCount(){ return users.size(); }


    // Util Functions
    public static Calendar getCalendar(String data) {
        SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(formatoData.parse(data));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return c;
    }
}
