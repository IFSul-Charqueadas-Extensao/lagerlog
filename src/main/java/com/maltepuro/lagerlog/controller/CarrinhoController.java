package com.maltepuro.lagerlog.controller;

import org.springframework.web.bind.annotation.RestController;

import com.maltepuro.lagerlog.model.Carrinho;
import com.maltepuro.lagerlog.model.ProdutosVendidos;
import com.maltepuro.lagerlog.model.ProdutosVendidosId;
import com.maltepuro.lagerlog.model.Estoque;
import com.maltepuro.lagerlog.model.Produto;
import com.maltepuro.lagerlog.model.Venda;
import com.maltepuro.lagerlog.model.VendaRequest;
import com.maltepuro.lagerlog.repository.EstoqueRepository;
import com.maltepuro.lagerlog.repository.ProdutosVendidosRepository;
import com.maltepuro.lagerlog.repository.VendaRepository;
import com.maltepuro.lagerlog.repository.ProdutoRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/venda")
public class CarrinhoController {

    @Autowired // injetar repositório automaticamente, em vez de criar uma instância dele manualmente.
    private VendaRepository vendaRepository; // utilizar a classe de acesso ao banco de dados
    @Autowired
    private ProdutosVendidosRepository produtosVendidosRepository; // utilizar a classe de acesso ao banco de dados

    @Autowired
    private EstoqueRepository estoqueRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    public CarrinhoController(VendaRepository vendaRepository, ProdutoRepository produtoRepository) {
        this.vendaRepository = vendaRepository;
        this.produtoRepository = produtoRepository;
    }

    @PostMapping("/finalizar")
    public ResponseEntity<String> finalizarVenda(@RequestBody VendaRequest request) {
            
        List<Carrinho> produtos = request.getCarrinho();
        String modoPagamento = request.getModoPagamento().toUpperCase();

        if (produtos == null || produtos.isEmpty()) {
            return ResponseEntity.badRequest().body("Carrinho vazio.");
        }

        try {
            BigDecimal valorTotal = calcularValorTotal(produtos);

            Venda venda = new Venda();
            venda.setDataVenda(Timestamp.valueOf(LocalDateTime.now()));
            venda.setValorVenda(valorTotal.doubleValue());
            venda.setCodUsuario(1L); // teste com usuário com ID 1
            venda.setModoPagamento(modoPagamento);
            vendaRepository.save(venda);

            Long codVenda = venda.getId();
            for (Carrinho produto : produtos) {
                ProdutosVendidosId produtosVendidosId = new ProdutosVendidosId();
                produtosVendidosId.setCodVenda(codVenda);
                produtosVendidosId.setCodProduto(produto.getId());
                
                ProdutosVendidos produtosVendidos = new ProdutosVendidos();
                produtosVendidos.setId(produtosVendidosId);
                produtosVendidos.setQuantidade(produto.getQuantidade());
                produtosVendidosRepository.save(produtosVendidos);

                Produto produtoEntity = produtoRepository.findById(produto.getId())
                        .orElseThrow(() -> new RuntimeException("Produto não encontrado: " + produto.getId()));
                
                // Ajustando estoque
                produtoEntity.setEstoque(produtoEntity.getEstoque() - produto.getQuantidade());
                produtoRepository.save(produtoEntity); // Salva a alteração no estoque do produto
                Estoque baixaEstoque = new Estoque();
                baixaEstoque.setProduto(produtoEntity);
                baixaEstoque.setTipo("VENDA");
                baixaEstoque.setQuantidade(-produto.getQuantidade());
                baixaEstoque.setDataCadastro(LocalDateTime.now());
                baixaEstoque.setObservacao("VENDA " + codVenda);
                estoqueRepository.save(baixaEstoque);
            }

            return ResponseEntity.ok("Venda finalizada com sucesso!");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Erro ao finalizar venda: " + e.getMessage());
        }
    }

    private BigDecimal calcularValorTotal(List<Carrinho> produtos) {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (Carrinho produto : produtos) {
            BigDecimal precoUnitario = BigDecimal.valueOf(produto.getPreco());
            BigDecimal quantidade = BigDecimal.valueOf(produto.getQuantidade());
            BigDecimal subtotal = precoUnitario.multiply(quantidade);
            valorTotal = valorTotal.add(subtotal);
        }
        return valorTotal;
    }
}

