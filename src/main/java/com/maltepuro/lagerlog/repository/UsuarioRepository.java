package com.maltepuro.lagerlog.repository;

import com.maltepuro.lagerlog.model.Usuario;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

//operacoes básicas de acesso a banco de dados
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsuario(String usuario);
}
