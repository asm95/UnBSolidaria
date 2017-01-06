package br.unb.unbsolidaria.entities;

/**
 * Created by eduar on 05/01/2017.
 */

public class News {

    private int id;
    private String titulo;
    private String subtitulo;
    private String texto;
    private String dataCadastro;
    private String dataNoticia;

    public News(){}

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public String getTexto() {
        return texto;
    }

    public String getDataCadastro() {
        return dataCadastro;
    }

    public String getDataNoticia() {
        return dataNoticia;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public void setDataCadastro(String dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public void setDataNoticia(String dataNoticia) {
        this.dataNoticia = dataNoticia;
    }
}
