package br.com.inter.testejava.dto;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class Resultados { 

    @NotNull
    @Transient
    private String n;

    @NotNull
    @Transient
    private Integer k;

    @NotNull
    @Transient
    private Integer digito;

    public Resultados(String n, Integer k, Integer digito) {
        this.n = n;
        this.k = k;
        this.digito = digito;
    }

    public Resultados(String n, Integer k) {
        this.n = n;
        this.k = k;
    }

    public Resultados() {

    }
}
