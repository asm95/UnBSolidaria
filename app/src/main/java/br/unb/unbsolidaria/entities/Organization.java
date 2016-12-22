package br.unb.unbsolidaria.entities;


import java.io.Serializable;

/**
 * Created by chris on 02/11/16.
 */
public class Organization {
    /**
     * The Global User ID (GID) is used to identify uniquely any user (org. or vol.) on the server
     * Currently the GID is being used by Database class on it's in-memory persistence
     */
    private int id;
    /**
     * CNPJ is an obligatory field at registration. Cannot be changed to avoid confusion in reports
     * or further account situation inspection by admins/moderators.
     */
    private String cnpj;
    private String legalName;
    private String commercialName;
    /**
     * Email is an obligatory field at registration. Can be changed in order to allow the user
     * receive newsletters and important updates from admins (i.e. server maintenance)
     */
    private String email;
    /**
     * PhoneNumber is an obligatory field at registration. Can be changed @ EditProfile screen.
     */
    private String phoneNumber;
    private String website;
    /**
     * Description should be short and objective.
     */
    private String description;
    private String address;
    private String cep;



    public Organization() {
    }
    public Organization(int id, String cnpj, String legalName,
                        String commercialName, String email, String phoneNumber,
                        String website, String description, String address,
                        String cep) {
        this.id = id;
        this.cnpj = cnpj;
        this.legalName = legalName;
        this.commercialName = commercialName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.description = description;
        this.address = address;
        this.cep = cep;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public String getCommercialName() {
        return commercialName;
    }

    public void setCommercialName(String commercialName) {
        this.commercialName = commercialName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCep() { return cep; }

    public void setCep(String cep) {
        this.cep = cep;
    }
}
