package br.com.inter.testejava.dto;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Table(name = "usuario")
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @NotNull
    private String nome;

    @NotNull
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @JsonIgnore
    private List<String> digitos = new ArrayList<String>();

    @Column(columnDefinition="CLOB")
    @Lob
    @JsonIgnore
    private String pubKey;

    @Column(columnDefinition="BLOB")
    @Lob
    @JsonIgnore
    private byte[] cryptNome;

    @Column(columnDefinition="BLOB")
    @Lob
    @JsonIgnore
    private byte[] cryptEmail;

    public Usuario(String nome, String email) {
        this.nome = nome;
        this.email = email;                
    }    

    public Usuario() {
        
    }    
}


    
    
