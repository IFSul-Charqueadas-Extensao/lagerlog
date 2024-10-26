let precoTotal = 0;
let produtosNoCarrinho = [];

document.addEventListener("DOMContentLoaded", function () {
    

    document.querySelectorAll('.ticket').forEach(function(ticket) {

        var estoque = parseFloat(ticket.querySelector('.estoque').innerText);
        var qtdVenda = parseFloat(ticket.querySelector('.qtdUnitaria').innerText);
        var pontoSuprimento = parseFloat(ticket.querySelector('.pontoSuprimento').innerText);

        if(estoque < qtdVenda){
            ticket.classList.add("semEstoque");
        } else if(estoque < pontoSuprimento){
            ticket.classList.add("estoqueBaixo");
        }   

        ticket.addEventListener('click', function() {
            var produtoId = ticket.id;
            var nomeProduto = ticket.querySelector('.nomeProduto').innerText;
            var qtdUnit = ticket.querySelector('.qtdUnitaria').innerText;
            var estoque = ticket.querySelector('.estoque').innerText;
            var precoProduto = parseFloat(ticket.querySelector('.precoProduto').innerText);
        

            var produtoExistente = document.querySelector('#itensCarrinho .detalhes-produto[data-produto="' + produtoId + '"]');

            var qtdUnitaria = parseFloat(qtdUnit);
            var qtdEstoque = parseFloat(estoque);
              
            if (produtoExistente) {

                var quantidadeAtual = parseInt(produtoExistente.getAttribute('data-quantidade'));

                if(quantidadeAtual * qtdUnitaria < qtdEstoque){
                    quantidadeAtual++;
                    produtoExistente.setAttribute('data-quantidade', quantidadeAtual);
                    atualizarTextoQuantidade(produtoExistente, precoProduto, quantidadeAtual);
                } else {
                   alert("Produto em falta!");
                }
                
            } else {

                var qtdUnitaria2 = parseInt(ticket.querySelector('.qtdUnitaria').innerText);
                var qtdEstoque2 = parseInt(ticket.querySelector('.estoque').innerText);



                if(qtdUnitaria2 < qtdEstoque2){
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
                            var precoTotal = calcularPrecoTotal();
                            document.querySelector('#total').textContent = "R$ " + precoTotal.toFixed(2);
                        } else if (quantidadeAtual == 1){
                            produto.remove();
                            var precoTotal = calcularPrecoTotal();
                            document.querySelector('#total').textContent = "R$ " + precoTotal.toFixed(2);
                        }
                    });

                    var aumentarBtn = document.createElement('button');
                    aumentarBtn.textContent = '+';
                    aumentarBtn.addEventListener('click', function() {
                        var quantidadeAtual = parseInt(produto.getAttribute('data-quantidade'));
                        var qtdUnitariaCar = parseFloat(produto.querySelector('.qtdUnit').innerText);
                        var qtdEstoqueCar = parseFloat(produto.querySelector('.estoqueCar').innerText);
                                            
                        if(quantidadeAtual * qtdUnitariaCar < qtdEstoqueCar){
                            quantidadeAtual++;
                            produto.setAttribute('data-quantidade', quantidadeAtual);
                            produto.querySelector('.quantidade').textContent = "Qtd.: " + quantidadeAtual + " | R$ " + (quantidadeAtual * precoProduto).toFixed(2);
                            var precoTotal = calcularPrecoTotal();
                            document.querySelector('#total').textContent = "R$ d" + precoTotal.toFixed(2);
                        } else {
                            alert("Produto em falta!");
                        }    

                    });

                    var removerBtn = document.createElement('button');
                    removerBtn.textContent = 'x';
                    removerBtn.addEventListener('click', function() {
                    produto.remove();
                    var precoTotal = calcularPrecoTotal();
                    document.querySelector('#total').textContent = "R$ " + precoTotal.toFixed(2);
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
                    quantidade.textContent = "Qtd.: 1" + " | R$ " + precoProduto.toFixed(2);
                    quantidadeControle.appendChild(quantidade);
                    
                    var divQtdUnit = document.createElement('div');
                    divQtdUnit.setAttribute("class", 'qtdUnit')
                    divQtdUnit.textContent = qtdUnit;
                    quantidadeControle.appendChild(divQtdUnit);

                    var divEstoque = document.createElement('div');
                    divEstoque.setAttribute("class", 'estoqueCar');
                    divEstoque.textContent = estoque;
                    quantidadeControle.appendChild(divEstoque);

                carrinho.appendChild(produto);
                } else {
                   alert("Produto em falta!");
                }
            }
    
            var precoTotal = calcularPrecoTotal();
            document.querySelector('#total').textContent = "R$ " + precoTotal.toFixed(2);
        });
    });
    
    function atualizarTextoQuantidade(produto, precoProduto, quantidade) {
        var valorTotal = (precoProduto * quantidade).toFixed(2);
        produto.querySelector('.quantidade').textContent = "Qtd.: " + quantidade + " | R$ " + valorTotal;
    }

    function calcularPrecoTotal() {
        var detalhesProdutos = document.querySelectorAll('#itensCarrinho .detalhes-produto');
        var total = 0;
    
        detalhesProdutos.forEach(function(produto) {
            var precoTexto = produto.querySelector('p').textContent;
            var preco = parseFloat(precoTexto.split('R$ ')[1]);
            var quantidade = parseInt(produto.getAttribute('data-quantidade'));
            total += preco * quantidade;
        });
        return total;
    }

    document.getElementById('btnLimpar').addEventListener('click', function () {
        precoTotal = 0;
        document.getElementById('total').textContent = "Total: R$ " + precoTotal.toFixed(2);

        var carrinhoDiv = document.querySelector('#itensCarrinho');
        carrinhoDiv.innerHTML = "";
    });


    document.getElementById('btnFinalizar').addEventListener('click', function() {
        var itensCarrinho = document.getElementById('itensCarrinho');
        var produtos = itensCarrinho.querySelectorAll('.detalhes-produto');
        console.log(produtos.length);

        if(produtos.length > 0){
            document.getElementById('modalTipoVenda').style.display = 'block';
        } else {
            alert('Carrinho sem produtos.');
        }
    });

    document.getElementById('modalClose').addEventListener('click', function() {
        document.getElementById('modalTipoVenda').style.display = 'none';
    });

    document.getElementById('btnConfirmarVenda').addEventListener('click', function() {
        var modoPagamento  = document.querySelector('input[name="tipoVenda"]:checked');

        if (modoPagamento ) {
            var itensCarrinho = document.getElementById('itensCarrinho');
            var produtos = itensCarrinho.querySelectorAll('.detalhes-produto');
            var carrinho = [];

            produtos.forEach(function (produto) {
                var idProduto = produto.getAttribute('data-produto');
                var precoProduto = produto.getAttribute('data-preco');
                var quantidadeProduto = produto.getAttribute('data-quantidade');

                carrinho.push({
                    id: idProduto,
                    preco: precoProduto,
                    quantidade: quantidadeProduto
                });
            });

            var url = 'http://localhost:8080/venda/finalizar';

            // var corpoRequisicao = { carrinho: carrinho, modoPagamento: modoPagamento.value };
            // console.log('Corpo da requisição:', JSON.stringify(corpoRequisicao));

            fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ carrinho: carrinho, modoPagamento : modoPagamento.value })
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('A resposta da rede não foi ok ' + response.statusText);
                }
                return response.text();
            })
            .then(data => {
                console.log('Venda finalizada com sucesso:', data);
                alert('Venda finalizada com sucesso!');
                location.reload();
            })
            .catch(error => {
                console.error('Houve um problema com a operação fetch:', error);
            });

            // Fecha o modal após a confirmação
            document.getElementById('modalTipoVenda').style.display = 'none';
        } else {
            alert('Por favor, selecione um tipo de venda.');
        }
    });

    // Fecha o modal se o usuário clicar fora dele
    window.onclick = function(event) {
        const modal = document.getElementById('modalTipoVenda');
        if (event.target === modal) {
            modal.style.display = 'none';
        }
    };
    
});