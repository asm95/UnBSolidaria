package br.unb.unbsolidaria.entities;

/**
 * Created by eduar on 06/01/2017.
 */

public class Signup {
    private String username;
    private String email;
    private String password1;
    private String password2;

    public Signup(String username, String email, String password1, String password2){
        this.username = username;
        this.email = email;
        this.password1 = password1;
        this.password2 = password2;
    }
}
