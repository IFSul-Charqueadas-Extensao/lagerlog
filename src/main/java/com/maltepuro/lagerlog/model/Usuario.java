package com.maltepuro.lagerlog.model;

import java.util.Set;

// import com.maltepuro.lagerlog.model.roles.UsuarioRole;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor  //criar construtor sem argumentos
@AllArgsConstructor //criar construtor com todos argumentos
@Data //criar getters, setters, toString ...
@Entity // tabela

@Table
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cod_usuario")
    private Long id;
    private String usuario;
    private String nome;
    private String senha;
    // private String grupo;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "cod_usuario"))
    @Column(name = "role")
    private Set<String> grupos;
    
    private boolean status;
}