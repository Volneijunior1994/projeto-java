package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Pedido implements Serializable {
    private int numeroPedido;
    private int codigoCliente;
    private Endereco enderecoEntrega;
    private List<Produto> produtos;
    private double montanteTotal;

    public Pedido(int numeroPedido, int codigoCliente, Endereco enderecoEntrega) {
        this.numeroPedido = numeroPedido;
        this.codigoCliente = codigoCliente;
        this.enderecoEntrega = enderecoEntrega;
        this.produtos = new ArrayList<>();
        this.montanteTotal = 0.0;
    }

    public void adicionarProduto(Produto p) {
        this.produtos.add(p);
        this.montanteTotal += p.getPrecoVenda();
    }

    // Getters
    public int getNumeroPedido() { return numeroPedido; }
    public int getCodigoCliente() { return codigoCliente; }
    public Endereco getEnderecoEntrega() { return enderecoEntrega; }
    public List<Produto> getProdutos() { return produtos; }
    public double getMontanteTotal() { return montanteTotal; }

    @Override
    public String toString() {
        return String.format("Pedido #%d | Cliente: %d | Total: R$ %.2f", numeroPedido, codigoCliente, montanteTotal);
    }
}
