// Source code is decompiled from a .class file using FernFlower decompiler (from Intellij IDEA).
package model;

import java.io.Serializable;

public class Produto implements Serializable {
   private int codigo;
   private String descricao;
   private double custo;
   private double precoVenda;
   private int codigoFornecedor;

   public Produto(int var1, String var2, double var3, double var5, int var7) {
      this.codigo = var1;
      this.descricao = var2;
      this.custo = var3;
      this.precoVenda = var5;
      this.codigoFornecedor = var7;
   }

   public int getCodigo() {
      return this.codigo;
   }

   public String getDescricao() {
      return this.descricao;
   }

   public double getCusto() {
      return this.custo;
   }

   public double getPrecoVenda() {
      return this.precoVenda;
   }

   public int getCodigoFornecedor() {
      return this.codigoFornecedor;
   }

   public void setDescricao(String var1) {
      this.descricao = var1;
   }

   public void setCusto(double var1) {
      this.custo = var1;
   }

   public void setPrecoVenda(double var1) {
      this.precoVenda = var1;
   }

   public void setCodigoFornecedor(int var1) {
      this.codigoFornecedor = var1;
   }

   public String toString() {
      return String.format("Cód: %d | %s | Preço: R$ %.2f | Fornecedor: %d", this.codigo, this.descricao, this.precoVenda, this.codigoFornecedor);
   }
}
