package com.example.cooperadora_escuela.ui;

public class Cuota {
    public int id;
    public String tipo;
    public String tipo_display;
    public Integer mes; // puede ser null
    public String mes_display;
    public int anio;
    public double monto;
    public boolean pagado;
    public String fecha_pago;
    public String fecha_creacion;

    public Cuota(int id, String tipo, String tipo_display, Integer mes, String mes_display, int anio, double monto, boolean pagado, String fecha_pago, String fecha_creacion) {
        this.id = id;
        this.tipo = tipo;
        this.tipo_display = tipo_display;
        this.mes = mes;
        this.mes_display = mes_display;
        this.anio = anio;
        this.monto = monto;
        this.pagado = pagado;
        this.fecha_pago = fecha_pago;
        this.fecha_creacion = fecha_creacion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(String fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public String getFecha_pago() {
        return fecha_pago;
    }

    public void setFecha_pago(String fecha_pago) {
        this.fecha_pago = fecha_pago;
    }

    public boolean isPagado() {
        return pagado;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public String getMes_display() {
        return mes_display;
    }

    public void setMes_display(String mes_display) {
        this.mes_display = mes_display;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public String getTipo_display() {
        return tipo_display;
    }

    public void setTipo_display(String tipo_display) {
        this.tipo_display = tipo_display;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
