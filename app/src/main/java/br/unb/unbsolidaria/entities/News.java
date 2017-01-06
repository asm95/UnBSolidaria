package br.unb.unbsolidaria.entities;

/**
 * Created by eduar on 05/01/2017.
 */

public class News {

    private int id;
    private String titulo;
    private String descricao;
    private int photo;

    public News(){}

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getPhoto() {
        return photo;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }
}
