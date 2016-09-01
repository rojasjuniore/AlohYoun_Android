package com.aloh.YOU.io.models;

/**
 * Created by develop on 15/06/16.
 */
public class AppDataSet {

    private int id;
    private String nombre;
    private Boolean banner_p_activo;
    private String banner_p_codigo;
    private Boolean banner_p_arriba;
    private Boolean banner_p_abajo;
    private Boolean banner_g_activo;
    private String banner_g_codigo;
    private Boolean banner_g_entrada;
    private Boolean banner_g_salida;
    private String TagInicio;
    private String web;
    private String idgcm;
    private String keygcm;
    private String votar;
    private String compartir;
    private String titulocompartir;

    public AppDataSet(){

    }
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Boolean getBanner_p_activo() {
        return banner_p_activo;
    }

    public String getBanner_p_codigo() {
        return banner_p_codigo;
    }

    public Boolean getBanner_p_arriba() {
        return banner_p_arriba;
    }

    public Boolean getBanner_p_abajo() {
        return banner_p_abajo;
    }

    public Boolean getBanner_g_activo() {
        return banner_g_activo;
    }

    public String getBanner_g_codigo() {
        return banner_g_codigo;
    }

    public Boolean getBanner_g_entrada() {
        return banner_g_entrada;
    }

    public Boolean getBanner_g_salida() {
        return banner_g_salida;
    }

    public String getTagInicio() {
        return TagInicio;
    }

    public String getWeb() {
        return web;
    }

    public String getIdgcm() {
        return idgcm;
    }

    public String getKeygcm() {
        return keygcm;
    }

    public String getVotar() {
        return votar;
    }

    public String getCompartir() {
        return compartir;
    }

    public String getTitulocompartir() {
        return titulocompartir;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setBanner_p_activo(Boolean banner_p_activo) {
        this.banner_p_activo = banner_p_activo;
    }

    public void setBanner_p_codigo(String banner_p_codigo) {
        this.banner_p_codigo = banner_p_codigo;
    }

    public void setBanner_p_arriba(Boolean banner_p_arriba) {
        this.banner_p_arriba = banner_p_arriba;
    }

    public void setBanner_p_abajo(Boolean banner_p_abajo) {
        this.banner_p_abajo = banner_p_abajo;
    }

    public void setBanner_g_activo(Boolean banner_g_activo) {
        this.banner_g_activo = banner_g_activo;
    }

    public void setBanner_g_codigo(String banner_g_codigo) {
        this.banner_g_codigo = banner_g_codigo;
    }

    public void setBanner_g_entrada(Boolean banner_g_entrada) {
        this.banner_g_entrada = banner_g_entrada;
    }

    public void setBanner_g_salida(Boolean banner_g_salida) {
        this.banner_g_salida = banner_g_salida;
    }

    public void setTagInicio(String tagInicio) {
        TagInicio = tagInicio;
    }

    public void setWeb(String web) {
        this.web = web;
    }

    public void setIdgcm(String idgcm) {
        this.idgcm = idgcm;
    }

    public void setKeygcm(String keygcm) {
        this.keygcm = keygcm;
    }

    public void setVotar(String votar) {
        this.votar = votar;
    }

    public void setCompartir(String compartir) {
        this.compartir = compartir;
    }

    public void setTitulocompartir(String titulocompartir) {
        this.titulocompartir = titulocompartir;
    }

    @Override
    public String toString() {
        return "AppDataSet{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", banner_p_activo=" + banner_p_activo +
                ", banner_p_codigo='" + banner_p_codigo + '\'' +
                ", banner_p_arriba=" + banner_p_arriba +
                ", banner_p_abajo=" + banner_p_abajo +
                ", banner_g_activo=" + banner_g_activo +
                ", banner_g_codigo='" + banner_g_codigo + '\'' +
                ", banner_g_entrada=" + banner_g_entrada +
                ", banner_g_salida=" + banner_g_salida +
                ", TagInicio='" + TagInicio + '\'' +
                ", web='" + web + '\'' +
                ", idgcm='" + idgcm + '\'' +
                ", keygcm='" + keygcm + '\'' +
                ", votar='" + votar + '\'' +
                ", compartir='" + compartir + '\'' +
                ", titulocompartir='" + titulocompartir + '\'' +
                '}';
    }


}
