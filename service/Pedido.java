// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
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

   public Pedido(int var1, int var2, Endereco var3) {
      this.numeroPedido = var1;
      this.codigoCliente = var2;
      this.enderecoEntrega = var3;
      this.produtos = new ArrayList();
      this.montanteTotal = (double)0.0F;
   }

   public void adicionarProduto(Produto var1) {
      this.produtos.add(var1);
      this.montanteTotal += var1.getPrecoVenda();
   }

   public int getNumeroPedido() {
      return this.numeroPedido;
   }

   public int getCodigoCliente() {
      return this.codigoCliente;
   }

   public Endereco getEnderecoEntrega() {
      return this.enderecoEntrega;
   }

   public List<Produto> getProdutos() {
      return this.produtos;
   }

   public double getMontanteTotal() {
      return this.montanteTotal;
   }

   public String toString() {
      return String.format("Pedido #%d | Cliente: %d | Total: R$ %.2f", this.numeroPedido, this.codigoCliente, this.montanteTotal);
   }
}
