package br.unb.unbsolidaria.entities;

import android.util.Log;

/**
 * Created by eduar on 06/01/2017.
 */

public class Login {
    private String username;
    private String password;
    private String email;

    public Login(String username, String password, String email){
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
