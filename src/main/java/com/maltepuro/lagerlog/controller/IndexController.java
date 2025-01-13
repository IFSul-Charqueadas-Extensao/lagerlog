package com.maltepuro.lagerlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import com.maltepuro.lagerlog.model.Produto;
import com.maltepuro.lagerlog.repository.ProdutoRepository;

import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/")
@Controller
public class IndexController {

    @Autowired
    private ProdutoRepository produtosRepository;
    
    @GetMapping()
    public String index() {
        return "redirect:/home";
    }
    
    
    @GetMapping("/home")
    public String main(Model model) {
        // Aqui você recupera os produtos com status 1 do banco de dados, o que representa produtos ativos
        List<Produto> produtosStatusAtivos = produtosRepository.findByStatus(1);
        Produto[] arrayProdutosStatusAtivos = produtosStatusAtivos.toArray(new Produto[0]);
        model.addAttribute("produtosAtivos", arrayProdutosStatusAtivos);
        return "index/home";
    }

    @RequestMapping("/controle")
    public String getControle() {
        return "index/controle";
    }

    @RequestMapping("/caixa")
    public String getCaixa(){
        return "index/caixa";
    }

    @RequestMapping("/sistema")
    public String getSistema(){
        return "index/sistema";
    }

}
