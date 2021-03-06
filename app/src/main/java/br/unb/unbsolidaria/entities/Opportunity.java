package br.unb.unbsolidaria.entities;

import java.util.Calendar;
import java.util.LinkedList;

/**
 * A opportunity is an event that can be created only by organizations. It invites volunteers to
 * join them for a social cause.
 */
public class Opportunity {
    /**
     * The Global User ID (GID) is used to identify uniquely any user (org. or vol.) on the server
     * Currently the GID is being used by Database class on it's in-memory persistence
     */
    private int id;
    //TODO: discutir feature de aprovação por parte da organização
    /**
     * Some opportunities can be restricted by the owner's approval. This is a feature still to be
     * discuessed by the core team members.
     */
    private boolean requerAprovacao;
    /**
     * Required field. Title should be at maximum 20 caracters
     */
    private String title;
    //TODO: definir limite da descrição (precisa estar acordado com o appWeb)
    private String description;
    //TODO: classe Local para conter informações tais como CEP
    /**
     * Adress could be picked up from google maps (not so easy to implement)
     */
    private String address;
    /**
     * Max amount of volunteers that can join the event. If it is unlimited, the value is -1.
     */
    private int nPositions;
    //TODO: propor um modelo para suportar marcação de dias pontuais e repetição
    private Calendar startDate;
    private Calendar endDate;

    private Organization organization;
    private LinkedList<Voluntary> approvedVoluntaries = null;

    public enum oDate {
        start, end;
    }
    //TODO: 2016/11/24 modificar quando for pegar a foto do server
    //para teste pelo res
    private int photo;



    public Opportunity() {
    }
    public Opportunity(int id, String address, int nPositions, String title,
                       String description, Calendar startDate, Calendar endDate,
                       Organization organization, int photo) {
        this.id = id;
        this.address = address;
        this.nPositions = nPositions;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.organization = organization;
        this.photo = photo;
    }

    public Opportunity(int id, String address, int nPositions, String title,
                       String description, Calendar startDate, Calendar endDate,
                       Organization organization) {
        this.id = id;
        this.address = address;
        this.nPositions = nPositions;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.organization = organization;
    }

    public String getFormattedDate (oDate dateType){
        switch (dateType){
            case start:
                return String.format("%1$td/%1$tm/%1$tY", this.startDate);
            case end:
                return String.format("%1$td/%1$tm/%1$tY", this.endDate);
        }
        return "";
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getLocal() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getVagas() {
        return nPositions;
    }

    public void setNPositions(int nPositions) {
        this.nPositions = nPositions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public int getSpots() { return nPositions;}
}
