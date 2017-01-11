package br.unb.unbsolidaria.entities;

import java.io.Serializable;

/**
 * asm95 day 2016-12-04
 * Ideally this should be a super class of Voluntary and Organization because both of them are Users.
 * However, doing that so will just mess up with .persistency.DataBase
 * And forces all org/vol elements to be tied to an user account, which makes no sense (their
 * user/password would be stored there)
 */
public class User implements Serializable {
    private int         id;
    private String      login;
    private String      password;
    private UserType    type;
    private String key;
    private String username;
    private String first_name;
    private String last_name;
    private String email;
    private String telefone;
    private String descricao;
    private int tipo;
    private String sexo;
    private String cpf;
    private String cnpj;
    private String endereco;
    private String cep;

    public User(String username, String first_name, String last_name, String email,
                String telefone, String descricao, int tipo, String sexo, String cpf, String cnpj, String cep){
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.telefone = telefone;
        this.descricao = descricao;
        this.tipo = tipo;
        this.sexo = sexo;
        this.cpf = cpf;
        this.cnpj = cnpj;
        this.cep = cep;
    }

    public enum UserType{
        organization, voluntary
    }

    //source: http://stackoverflow.com/a/16058059
    private static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public User(String login, String password, UserType type, int id) {
        if (id < 0)
            throw new IllegalArgumentException("User ID must be positive");
        if (!isValidEmailAddress(login))
            throw new IllegalArgumentException("E-mail adress is not valid");
        if (!FormValidation.isValidPassword(password))
            throw new IllegalArgumentException("Password is not valid");

        this.login = login;
        this.password = password;
        this.type = type;
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public String getPassword() { return password; }

    public String getLogin() { return login; }

    public UserType getType(){
        return this.type;
    }

    public String getKey(){ return this.key; }

    public int getTipo(){return this.tipo;}

    public String getEndereco(){return this.endereco;}

    public String getCep(){return this.cep;}

    public void setKey(String key){
        this.key = key;
    }

    public void setCep(String cep){
        this.cep = cep;
    }

    public void setEndereco(String endereco){
        this.endereco = endereco;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public int getTypeInt(){
        switch (this.type){
            case voluntary:
                return 0;
            case organization:
                return 1;
        }
        return -1;
    }

    public static UserType getIntType(int i){
        if (i==0) return UserType.voluntary;
        return UserType.organization;
    }
}
