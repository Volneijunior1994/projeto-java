package model;

import java.io.Serializable;

public class Produto implements Serializable {
    private int codigo;
    private String descricao;
    private double custo;
    private double precoVenda;
    private int codigoFornecedor;

    public Produto(int codigo, String descricao, double custo, double precoVenda, int codigoFornecedor) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.custo = custo;
        this.precoVenda = precoVenda;
        this.codigoFornecedor = codigoFornecedor;
    }

    // Getters e Setters
    public int getCodigo() { return codigo; }
    public String getDescricao() { return descricao; }
    public double getCusto() { return custo; }
    public double getPrecoVenda() { return precoVenda; }
    public int getCodigoFornecedor() { return codigoFornecedor; }

    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setCusto(double custo) { this.custo = custo; }
    public void setPrecoVenda(double precoVenda) { this.precoVenda = precoVenda; }
    public void setCodigoFornecedor(int codigoFornecedor) { this.codigoFornecedor = codigoFornecedor; }

    @Override
    public String toString() {
        return String.format("Cód: %d | %s | Preço: R$ %.2f | Fornecedor: %d", codigo, descricao, precoVenda, codigoFornecedor);
    }
}
