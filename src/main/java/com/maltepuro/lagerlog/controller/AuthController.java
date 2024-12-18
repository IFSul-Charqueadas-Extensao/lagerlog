package com.maltepuro.lagerlog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.maltepuro.lagerlog.model.Usuario;
import com.maltepuro.lagerlog.repository.UsuarioRepository;
import org.springframework.web.bind.annotation.RequestParam;


@RequestMapping("/auth")
@Controller
public class AuthController {
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/login")
    public String getLogin(){
        return "login";
    }

    @PostMapping("/login")
    public String postLogin() {
        return "redirect:/home";
    }
    
    @GetMapping("/login/success")
    public String getLoginSuccess(){
        return "redirect:/home";
    }

    @GetMapping("/login/failure")
    public String getLoginFailure(Model model){
        model.addAttribute("erro", true);
        return "redirect:/auth/login";
    }

    @GetMapping("/signup")
    public String getSignup() {
        if(!usuarioRepository.findAll().isEmpty()){
            return "redirect:/auth/login";
        }
        return "signup";
    }

    @PostMapping("/signup")
    public String postSignup(@RequestParam String username, @RequestParam String password, @RequestParam String displayname) {
        if(!usuarioRepository.findAll().isEmpty()) return "redirect:/auth/login";

        if(!usuarioRepository.findByUsuario(username).isPresent()){
            Usuario usuario = new Usuario();
            usuario.setUsuario(username);
            usuario.setSenha(passwordEncoder.encode(password));
            usuario.setNome(displayname);
            usuario.setGrupos(Set.of("ROLE_ADMINISTRADOR", "ROLE_SUPERVISOR", "ROLE_OPERADOR"));
            usuarioRepository.save(usuario);
            
            return "redirect:/auth/login";
        }
        
        return "signup";
    }
    


    
    
    // @PostMapping("/login")
    // public String authenticate(Authentication authentication) {
    //     return authenticationService.authenticate(authentication);
    // }

    // @PostMapping("/login")
    // public ResponseEntity<Void> authenticate(@RequestBody LoginRequest loginRequest) {
    //     Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password());
    //     Authentication autnheticationResponse = this.authenticationManager.authenticate(authenticationRequest);
    //             return null;
    // }

    // public record LoginRequest(String username, String password) {
    // }

    // @PostMapping("/login")
    // public String authenticate(@RequestParam String username, @RequestParam String password){
    //     // Authentication authentication = new Authentication()
    //     return authenticationService.authenticate(authentication);
    // }
}