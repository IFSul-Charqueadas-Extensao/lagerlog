package com.maltepuro.lagerlog.model;

import java.util.HashMap;
import java.util.Map;
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

    public String getHighestAuthority(){
        Set<String> grupos = getGrupos();
        
        Map<String, Integer> grupoPriority = new HashMap<>();
        grupoPriority.put("ROLE_ADMINISTRADOR", 1);
        grupoPriority.put("ROLE_SUPERVISOR", 2);
        grupoPriority.put("ROLE_OPERADOR", 3);

        String highestGrupo = null;
        int highestPriority = Integer.MAX_VALUE;

        for (String grupo : grupos){
            if (grupoPriority.containsKey(grupo) && grupoPriority.get(grupo) < highestPriority){
                highestGrupo = grupo;
                highestPriority = grupoPriority.get(grupo);
            }
        }

        String highestGrupoDisplayName = highestGrupo != null
            ? highestGrupo.replace("ROLE_", "")
                .toLowerCase()
                .substring(0, 1).toUpperCase()
                + highestGrupo.substring(6).toLowerCase()
            : "Null";

        return highestGrupoDisplayName;
    }
}