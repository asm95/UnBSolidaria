package br.unb.unbsolidaria.entities;

/**
 * Created by eduar on 18/01/2017.
 */

public class InscreverUsuario {
    private String orgUrl;
    private String volUrl;
    private String opportunityUrl;

    public InscreverUsuario(String orgUrl, String volUrl, String opportunityUrl){
        this.orgUrl = orgUrl;
        this.volUrl = volUrl;
        this.opportunityUrl = opportunityUrl;
    }
}
