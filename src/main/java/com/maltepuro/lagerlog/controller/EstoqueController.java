package com.maltepuro.lagerlog.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.maltepuro.lagerlog.model.Estoque;
import com.maltepuro.lagerlog.model.Produto;
import com.maltepuro.lagerlog.repository.EstoqueRepository;
import com.maltepuro.lagerlog.repository.ProdutoRepository;


@Controller
public class EstoqueController {

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping("/estoque")
    public String getEstoque(Model model) {
        List<Produto> produto = produtoRepository.findAll();
        Produto[] arrayProdutosEstoque = produto.toArray(new Produto[0]);
        model.addAttribute("produtos", arrayProdutosEstoque);
        return "listarEstoque";
    }

    
    @GetMapping("/estoque/registros")
    public String getRegistrosEstoque(Model model) {
        List<Estoque> estoque = estoqueRepository.findAll();
        Estoque[] arrayEstoque = estoque.toArray(new Estoque[0]);
        model.addAttribute("estoque", arrayEstoque);
        return "listarRegistrosEstoque";
    }

    @GetMapping("/estoque/cadastrar")
    public String entradaEstoque(Model model){
        List<Produto> produtos = produtoRepository.findAll();
        model.addAttribute("produtos", produtos);
        return "cadastrarEstoque";
    }

   
    @PostMapping("/estoque/cadastrar")
    public String cadastrarEstoque(@RequestParam String produto,
        @RequestParam String quantidade, @RequestParam String observacao, 
        RedirectAttributes redirectAttributes) {
        Estoque e = new Estoque();

        Produto produtoEntity = produtoRepository.findById(Long.parseLong(produto))
            .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        double estoqueAtual = produtoEntity.getEstoque();
        double novaQuantidade = estoqueAtual + Integer.parseInt(quantidade);
        produtoEntity.setEstoque(novaQuantidade); 

        e.setProduto(produtoEntity);
        e.setTipo("ENTRADA");
        e.setQuantidade(Double.parseDouble(quantidade));
        e.setObservacao(observacao);
        e.setDataCadastro(LocalDateTime.now());
        estoqueRepository.save(e);
                
        System.out.println("Entrada cadastrada com sucesso!");
        redirectAttributes.addFlashAttribute("mensagem", "Entrada de estoque cadastrada com sucesso!");
        return "redirect:/estoque";
    }

    @GetMapping("/estoque/ajustar")
    public String ajusteEstoque(Model model){
        List<Produto> produtos = produtoRepository.findAll();
        model.addAttribute("produtos", produtos);
        return "ajustarEstoque";
    }

    @PostMapping("/estoque/ajustar")
    public String ajustarEstoque(@RequestParam String produto,
        @RequestParam String quantidade, @RequestParam String observacao, 
        RedirectAttributes redirectAttributes) {
        Estoque e = new Estoque();

        Produto produtoEntity = produtoRepository.findById(Long.parseLong(produto))
            .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        double estoqueAtual = produtoEntity.getEstoque();
        int qtdDeBaixa = Integer.parseInt(quantidade);
        String descricaoProduto = produtoEntity.getDescricao();
        String unidade = produtoEntity.getUnidade();
        if(estoqueAtual < qtdDeBaixa){
            String mensagem = "A operação não pôde ser concluída. O estoque do produto '" + descricaoProduto + "' é insuficiente para a quantidade de baixa solicitada. Estoque atual: " + estoqueAtual + " " + unidade + " .";
            System.out.println(mensagem);
            redirectAttributes.addFlashAttribute("mensagem", mensagem);
            return "redirect:/estoque/ajustar";
        } else {
        produtoEntity.setEstoque(estoqueAtual - qtdDeBaixa); 

        e.setProduto(produtoEntity);
        e.setTipo("AJUSTE");
        e.setQuantidade(-Double.parseDouble(quantidade));
        e.setObservacao(observacao);
        e.setDataCadastro(LocalDateTime.now());
        estoqueRepository.save(e);
        System.out.println("Ajuste realizado com sucesso!");
        redirectAttributes.addFlashAttribute("mensagem", "Ajuste de estoque realizado com sucesso!");
        return "redirect:/estoque";

        }
        
        
    }
}