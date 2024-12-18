document.addEventListener("DOMContentLoaded", function () {
    let precoTotal = 0;
    let produtosNoCarrinho = [];
    let precoSelecionado = null;

    const precosCores = {
        4: 'white', // Vale água (Branco)
        6: 'blue', // Vale refrigerante (Azul marinho escuro)
        12: '#a345a2', // R$12 (Roxo)
        13: 'yellow', // R$13 (Amarelo)
        14: 'green', // R$14 (Verde)
        15: 'brown', // R$15 (Marrom)
        16: 'red', // R$16 (Vermelho)
        18: 'lightblue', // R$18 (Azul claro)
        22: 'orange', // R$22 (Laranjas)
    };
    

    // Função para fazer requisição e buscar produtos
    function buscarProdutos() {
        fetch('/produto/listarProdutos')  // Novo endpoint que retorna os produtos em JSON
            .then(response => response.json()) // Espera a resposta em formato JSON
            .then(produtos => {
                gerarProdutosNaPagina(produtos); // Passa os produtos recebidos para gerar os tickets
                gerarBotoesDePreco(produtos); // Gera os botões de preço
            })
            .catch(error => console.error("Erro ao buscar produtos:", error));
    }

    buscarProdutos(); // Chama a função ao carregar a página

// Função para gerar os tickets de produtos dinamicamente
function gerarProdutosNaPagina(produtos) {
    const produtosContainer = document.getElementById("produtosContainer");
    produtosContainer.innerHTML = ""; // Limpa o container antes de adicionar os novos produtos
}


function gerarBotoesDePreco(produtos) {
    const filtroPrecosContainer = document.getElementById("filtroPrecosContainer");
    const precosUnicos = new Set();

    // Coleta os preços únicos dos produtos
    produtos.forEach(produto => {
        if (precosCores[produto.preco]) {  // Só adiciona preços que estão no mapeamento
            precosUnicos.add(produto.preco);
        }
    });

    // Converte o Set para um array, ordena em ordem crescente e gera os botões
    Array.from(precosUnicos).sort((a, b) => a - b).forEach(preco => {
        const botaoPreco = document.createElement("button");
        botaoPreco.classList.add("ticket");
        botaoPreco.style.backgroundColor = precosCores[preco]; // Atribui a cor correspondente ao preço
        botaoPreco.innerHTML = `<p>R$ ${preco.toFixed(2)}</p>`; // O valor do preço é centralizado dentro de <p>

        botaoPreco.addEventListener("click", function () {
            precoSelecionado = preco;
            document.getElementById("modalProdutos").style.display = "block";
            preencherProdutosNoModal(precoSelecionado, produtos);
        });

        filtroPrecosContainer.appendChild(botaoPreco);
    });
}


    document.getElementById("modalProdutosClose").addEventListener("click", function () {
        document.getElementById("modalProdutos").style.display = "none";
    });

    // Atualiza o modal para exibir produtos com o preço selecionado
    function preencherProdutosNoModal(precoSelecionado, produtos) {
        const containerModal = document.getElementById("produtosModalContainer");
        containerModal.innerHTML = "";

        // Adiciona os produtos ao modal com o preço filtrado
        produtos.forEach(function (produto) {
            if (produto.preco === precoSelecionado) {
                const produtoDiv = document.createElement("div");
                produtoDiv.classList.add("produto-modal");
                produtoDiv.setAttribute("data-produto", produto.id);

                const nomeProdutoElem = document.createElement("p");
                nomeProdutoElem.textContent = produto.descricao;
                produtoDiv.appendChild(nomeProdutoElem);

                const precoProdutoElem = document.createElement("p");
                precoProdutoElem.textContent = `R$ ${produto.preco.toFixed(2)}`;
                produtoDiv.appendChild(precoProdutoElem);
               
                const estoqueProdutoElem = document.createElement("p");
                estoqueProdutoElem.textContent = `Estoque: ${produto.estoque}`;
                produtoDiv.appendChild(estoqueProdutoElem);

                const btnAdicionar = document.createElement("button");
                btnAdicionar.textContent = "Adicionar ao Carrinho";
                btnAdicionar.addEventListener("click", function () {
                    adicionarAoCarrinho(produto.id, produto.descricao, produto.preco, produto.estoque, produto.quantidade);
                    
                });

                produtoDiv.appendChild(btnAdicionar);
                containerModal.appendChild(produtoDiv);
            }
        });
    }

    // Função para adicionar o produto ao carrinho
    function adicionarAoCarrinho(produtoId, nomeProduto, precoProduto, estoque, qtdUnitaria) {
        var produtoExistente = document.querySelector('#itensCarrinho .detalhes-produto[data-produto="' + produtoId + '"]');

        if (produtoExistente) {
            var quantidadeAtual = parseInt(produtoExistente.getAttribute('data-quantidade'));
            if (quantidadeAtual < estoque) {
                quantidadeAtual++;
                produtoExistente.setAttribute('data-quantidade', quantidadeAtual);
                atualizarTextoQuantidade(produtoExistente, precoProduto, quantidadeAtual);
            } else {
                alert("Produto em falta!");
            }
        } else {
            if (qtdUnitaria <= estoque) {
                var carrinho = document.querySelector('#itensCarrinho');
                var produto = document.createElement('div');
                produto.classList.add('detalhes-produto');
                produto.setAttribute('data-produto', produtoId);
                produto.setAttribute('data-preco', precoProduto);
                produto.setAttribute('data-quantidade', 1); // Inicializa a quantidade como 1


                var itemCarrinho = document.createElement('p');
                itemCarrinho.setAttribute('class', "pSemEspaco");
                itemCarrinho.textContent = nomeProduto + " R$ " + precoProduto;
                produto.appendChild(itemCarrinho);

                var quantidadeControle = document.createElement('div');
                quantidadeControle.classList.add('quantidade-controle');

                var diminuirBtn = document.createElement('button');
                diminuirBtn.textContent = '-';
                diminuirBtn.addEventListener('click', function() {
                    var quantidadeAtual = parseInt(produto.getAttribute('data-quantidade'));
                    if (quantidadeAtual > 1) {
                        quantidadeAtual--;
                        produto.setAttribute('data-quantidade', quantidadeAtual);
                        produto.querySelector('.quantidade').textContent = "Qtd.: " + quantidadeAtual + " | R$ " + (quantidadeAtual * precoProduto).toFixed(2);
                        atualizarPrecoTotal();
                    } else {
                        produto.remove();
                        atualizarPrecoTotal();
                    }
                });

                var aumentarBtn = document.createElement('button');
                aumentarBtn.textContent = '+';
                aumentarBtn.addEventListener('click', function() {
                    var quantidadeAtual = parseInt(produto.getAttribute('data-quantidade'));
                    if (quantidadeAtual < estoque) {
                        quantidadeAtual++;
                        produto.setAttribute('data-quantidade', quantidadeAtual);
                        produto.querySelector('.quantidade').textContent = "Qtd.: " + quantidadeAtual + " | R$ " + (quantidadeAtual * precoProduto).toFixed(2);
                        atualizarPrecoTotal();
                    } else {
                        alert("Produto em falta!");
                    }
                });

                var removerBtn = document.createElement('button');
                removerBtn.textContent = 'x';
                removerBtn.addEventListener('click', function() {
                    produto.remove();
                    atualizarPrecoTotal();
                });

                produto.appendChild(quantidadeControle);
                var divButton = document.createElement('div');
                divButton.setAttribute('class', "buttonCarrinho");
                quantidadeControle.appendChild(divButton);
                divButton.appendChild(diminuirBtn);
                divButton.appendChild(aumentarBtn);
                divButton.appendChild(removerBtn);

                var quantidade = document.createElement('p');
                quantidade.classList.add('quantidade');
                quantidade.textContent = "Qtd.: 1 | R$ " + precoProduto.toFixed(2);
                quantidadeControle.appendChild(quantidade);

                carrinho.appendChild(produto);
                atualizarPrecoTotal();
            } else {
                alert("Produto em falta!");
            }
        }
    }

    // Atualizar o texto da quantidade no carrinho
    function atualizarTextoQuantidade(produto, precoProduto, quantidade) {
        var valorTotal = (precoProduto * quantidade).toFixed(2);
        produto.querySelector('.quantidade').textContent = "Qtd.: " + quantidade + " | R$ " + valorTotal;
        let estoqueTexto = document.querySelector('div.produto-modal p:nth-of-type(3)').textContent;  //busca pelo terceiro <p> no modal, o <p> que mostra o estoque
        let estoque = estoqueTexto.split(' ')[0] + ' ' + estoqueTexto.split(' ')[1];  //pega o texto 'Estoque' e também o valor dele com split 
        document.querySelector('div.produto-modal p:nth-of-type(3)').textContent = estoque + " Qtd: " + quantidade;  //concatena o texto 'estoque' a nova quantidade
    }

    // Atualizar o preço total do carrinho
    function atualizarPrecoTotal() {
        var detalhesProdutos = document.querySelectorAll('#itensCarrinho .detalhes-produto');
        var total = 0;
        detalhesProdutos.forEach(function(produto) {
            var preco = parseFloat(produto.querySelector('p').textContent.split('R$ ')[1]);
            var quantidade = parseInt(produto.getAttribute('data-quantidade'));
            total += preco * quantidade;
        });
        document.querySelector('#total').textContent = "R$ " + total.toFixed(2);
    }

    // Função para limpar o carrinho
    document.getElementById('btnLimpar').addEventListener('click', function () {
        precoTotal = 0;
        document.getElementById('total').textContent = "Total: R$ " + precoTotal.toFixed(2);
        var carrinhoDiv = document.querySelector('#itensCarrinho');
        carrinhoDiv.innerHTML = "";
    });

    // Função para finalizar a venda
    document.getElementById('btnFinalizar').addEventListener('click', function() {
        var itensCarrinho = document.getElementById('itensCarrinho');
        var produtos = itensCarrinho.querySelectorAll('.detalhes-produto');
        if(produtos.length > 0){
            document.getElementById('modalTipoVenda').style.display = 'block';
        } else {
            alert('Carrinho sem produtos.');
        }
    });

        // Fechar o modal de tipo de venda ao clicar no 'X'
        document.getElementById("modalClose").addEventListener("click", function () {
            document.getElementById("modalTipoVenda").style.display = "none";
        });
    
        // Fechar o modal de tipo de venda ao clicar fora da área do modal
        window.addEventListener("click", function (event) {
            if (event.target === document.getElementById("modalTipoVenda")) {
                document.getElementById("modalTipoVenda").style.display = "none";
            }
        });

    // Confirmar a venda no modal
    document.getElementById('btnConfirmarVenda').addEventListener('click', function() {
        var modoPagamento = document.querySelector('input[name="tipoVenda"]:checked');
        if (modoPagamento) {
            var itensCarrinho = document.getElementById('itensCarrinho');
            var produtos = itensCarrinho.querySelectorAll('.detalhes-produto');
            var carrinho = [];
            var estoqueDisponivel = true;
    
            produtos.forEach(function (produto) {
                var idProduto = produto.getAttribute('data-produto');
                var quantidade = parseInt(produto.getAttribute('data-quantidade'));
                var precoProduto = parseFloat(produto.getAttribute('data-preco'));
    
                // Verifique se a quantidade do produto no carrinho é maior que a quantidade no estoque
                var quantidadeEstoque = parseInt(produto.getAttribute('data-estoque'));
                if (quantidade > quantidadeEstoque) {
                    estoqueDisponivel = false;
                    alert("Quantidade do produto " + produto.getAttribute('data-nome') + " não disponível em estoque.");
                }
    
                carrinho.push({id: idProduto, quantidade: quantidade, preco: precoProduto});
            });
    
            if (estoqueDisponivel) {
                // Enviar a requisição POST para finalizar a venda
                fetch('/venda/finalizar', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        carrinho: carrinho,
                        modoPagamento: modoPagamento.value
                    })
                })
                .then(response => response.json())
                .then(data => {
                    alert(data);
                    document.getElementById('modalTipoVenda').style.display = 'none';
                    document.getElementById('itensCarrinho').innerHTML = '';
                    document.getElementById('total').textContent = 'R$ 0.00';
                })
                .catch(error => {
                    console.error("Erro ao finalizar venda", error);
                    alert("Erro ao finalizar venda.");
                });
            }
        } else {
            alert("Selecione uma forma de pagamento.");
        }
    });
});