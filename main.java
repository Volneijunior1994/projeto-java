package view;

import model.*;
import repository.GenericRepository;
import service.LogService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static GenericRepository<Pessoa> pessoaRepo = new GenericRepository<>("pessoas.dat");
    private static GenericRepository<Produto> produtoRepo = new GenericRepository<>("produtos.dat");
    private static GenericRepository<Pedido> pedidoRepo = new GenericRepository<>("pedidos.dat");

    public static void main(String[] args) {
        int opcao;
        do {
            exibirMenu("--- MENU PRINCIPAL ---");
            opcao = lerInteiro();
            switch (opcao) {
                case 1: menuPessoas(); break;
                case 2: menuProdutos(); break;
                case 3: menuVendas(); break;
                case 0: System.out.println("Saindo..."); break;
                default: System.out.println("Opção inválida! Tente novamente.");
            }
        } while (opcao != 0);
        scanner.close();
    }

    private static void exibirMenu(String titulo) {
        System.out.println("\n====================================");
        try (BufferedReader br = new BufferedReader(new FileReader("data/menus.txt"))) {
            String linha;
            boolean imprimir = false;
            while ((linha = br.readLine()) != null) {
                if (linha.equals(titulo)) {
                    imprimir = true;
                    System.out.println(linha);
                } else if (linha.startsWith("---") && imprimir) {
                    break;
                }
                
                if (imprimir && !linha.equals(titulo)) {
                    System.out.println(linha);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de menus: " + e.getMessage());
            System.out.println(titulo); // Fallback se arquivo sumir
        }
        System.out.print("Escolha uma opção: ");
    }

    private static int lerInteiro() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Digite um número inteiro: ");
            }
        }
    }

    private static double lerDouble() {
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Digite um número decimal: ");
            }
        }
    }

    // --- SUBMENUS E CRUDs ---

    private static void menuPessoas() {
        int op;
        do {
            exibirMenu("--- MENU PESSOAS ---");
            op = lerInteiro();
            List<Pessoa> lista = pessoaRepo.loadAll();
            switch (op) {
                case 1: // Incluir
                    System.out.print("Código: "); int cod = lerInteiro();
                    if (lista.stream().anyMatch(p -> p.getCodigo() == cod)) {
                        System.out.println("Já existe uma pessoa com este código.");
                        LogService.log("Tentativa de inclusão de Pessoa com código duplicado: " + cod);
                        break;
                    }
                    System.out.print("Nome: "); String nome = scanner.nextLine();
                    System.out.print("Tipo (1-Cliente, 2-Fornecedor, 3-Ambos): "); int tipo = lerInteiro();
                    Pessoa novaPessoa = null;
                    if (tipo == 1) {
                        novaPessoa = new Cliente(cod, nome);
                    } else if (tipo == 2) {
                        novaPessoa = new Fornecedor(cod, nome);
                    } else if (tipo == 3) {
                        // Para o caso de 'ambos', podemos criar uma classe que implemente ambas as interfaces
                        // ou simplesmente instanciar um Cliente e um Fornecedor com o mesmo código/nome
                        // Para simplicidade, vamos considerar que uma pessoa pode ser apenas um tipo principal
                        // ou adicionar uma flag na classe Pessoa se for o caso.
                        // Por enquanto, vamos manter como Cliente ou Fornecedor.
                        System.out.println("Opção 'Ambos' não implementada para este exemplo. Escolha Cliente ou Fornecedor.");
                        break;
                    } else {
                        System.out.println("Tipo de pessoa inválido.");
                        break;
                    }

                    System.out.println("Adicionar Endereço:");
                    Endereco end = criarEndereco();
                    if (end != null) {
                        novaPessoa.addEndereco(end);
                    }
                    
                    lista.add(novaPessoa);
                    pessoaRepo.saveAll(lista);
                    LogService.log("Inclusão de Pessoa: " + nome + " (Código: " + cod + ")");
                    System.out.println("Pessoa cadastrada com sucesso!");
                    break;
                case 2: // Consultar
                    if (lista.isEmpty()) {
                        System.out.println("Nenhuma pessoa cadastrada.");
                        break;
                    }
                    System.out.print("Consultar por Código (0 para listar todos): "); int cCons = lerInteiro();
                    if (cCons == 0) {
                        lista.forEach(p -> {
                            System.out.println(p);
                            p.getEnderecos().forEach(e -> System.out.println("  " + e));
                        });
                    } else {
                        Optional<Pessoa> pessoaConsultada = lista.stream().filter(p -> p.getCodigo() == cCons).findFirst();
                        if (pessoaConsultada.isPresent()) {
                            System.out.println(pessoaConsultada.get());
                            pessoaConsultada.get().getEnderecos().forEach(e -> System.out.println("  " + e));
                        } else {
                            System.out.println("Pessoa não encontrada.");
                        }
                    }
                    break;
                case 3: // Alterar
                    System.out.print("Código da pessoa para alterar: "); int cAlt = lerInteiro();
                    Optional<Pessoa> pessoaParaAlterar = lista.stream().filter(x -> x.getCodigo() == cAlt).findFirst();
                    if (pessoaParaAlterar.isPresent()) {
                        Pessoa pAlt = pessoaParaAlterar.get();
                        System.out.print("Novo nome (atual: " + pAlt.getNome() + "): "); 
                        String novoNome = scanner.nextLine();
                        if (!novoNome.isEmpty()) {
                            pAlt.setNome(novoNome);
                        }
                        // Lógica para alterar endereço pode ser adicionada aqui
                        pessoaRepo.saveAll(lista);
                        LogService.log("Alteração de Pessoa ID: " + cAlt);
                        System.out.println("Pessoa alterada com sucesso!");
                    } else {
                        System.out.println("Pessoa não encontrada.");
                    }
                    break;
                case 4: // Excluir
                    System.out.print("Código da pessoa para excluir: "); int cExc = lerInteiro();
                    if(lista.removeIf(x -> x.getCodigo() == cExc)) {
                        pessoaRepo.saveAll(lista);
                        LogService.log("Exclusão de Pessoa ID: " + cExc);
                        System.out.println("Pessoa excluída com sucesso!");
                    } else {
                        System.out.println("Pessoa não encontrada.");
                    }
                    break;
                case 0: System.out.println("Voltando ao Menu Principal..."); break;
                default: System.out.println("Opção inválida! Tente novamente.");
            }
        } while (op != 0);
    }

    private static Endereco criarEndereco() {
        System.out.print("CEP: "); String cep = scanner.nextLine();
        System.out.print("Logradouro: "); String logradouro = scanner.nextLine();
        System.out.print("Número: "); String numero = scanner.nextLine();
        System.out.print("Complemento: "); String complemento = scanner.nextLine();
        System.out.print("Tipo (Residencial, Comercial, Entrega, Correspondencia): "); String tipo = scanner.nextLine();
        if (cep.isEmpty() || logradouro.isEmpty() || numero.isEmpty() || tipo.isEmpty()) {
            System.out.println("Campos obrigatórios do endereço não preenchidos. Endereço não será adicionado.");
            return null;
        }
        return new Endereco(cep, logradouro, numero, complemento, tipo);
    }

    private static void menuProdutos() {
        int op;
        do {
            exibirMenu("--- MENU PRODUTOS ---");
            op = lerInteiro();
            List<Produto> lista = produtoRepo.loadAll();
            switch (op) {
                case 1: // Incluir
                    System.out.print("Código: "); int cod = lerInteiro();
                    if (lista.stream().anyMatch(p -> p.getCodigo() == cod)) {
                        System.out.println("Já existe um produto com este código.");
                        LogService.log("Tentativa de inclusão de Produto com código duplicado: " + cod);
                        break;
                    }
                    System.out.print("Descrição: "); String desc = scanner.nextLine();
                    System.out.print("Custo: "); double custo = lerDouble();
                    System.out.print("Preço de Venda: "); double venda = lerDouble();
                    System.out.print("Código do Fornecedor: "); int codF = lerInteiro();
                    
                    // Validação simples: verificar se o fornecedor existe
                    if (pessoaRepo.loadAll().stream().noneMatch(p -> p.getCodigo() == codF && p instanceof Fornecedor)) {
                        System.out.println("Fornecedor com código " + codF + " não encontrado. Produto não cadastrado.");
                        LogService.log("Tentativa de inclusão de Produto com fornecedor inexistente: " + codF);
                        break;
                    }

                    lista.add(new Produto(cod, desc, custo, venda, codF));
                    produtoRepo.saveAll(lista);
                    LogService.log("Inclusão de Produto: " + desc + " (Código: " + cod + ")");
                    System.out.println("Produto cadastrado com sucesso!");
                    break;
                case 2: // Consultar
                    if (lista.isEmpty()) {
                        System.out.println("Nenhum produto cadastrado.");
                        break;
                    }
                    System.out.print("Consultar por Código (0 para listar todos): "); int cCons = lerInteiro();
                    if (cCons == 0) {
                        lista.forEach(System.out::println);
                    } else {
                        Optional<Produto> produtoConsultado = lista.stream().filter(p -> p.getCodigo() == cCons).findFirst();
                        if (produtoConsultado.isPresent()) {
                            System.out.println(produtoConsultado.get());
                        } else {
                            System.out.println("Produto não encontrado.");
                        }
                    }
                    break;
                case 3: // Alterar
                    System.out.print("Código do produto para alterar: "); int cAlt = lerInteiro();
                    Optional<Produto> produtoParaAlterar = lista.stream().filter(x -> x.getCodigo() == cAlt).findFirst();
                    if (produtoParaAlterar.isPresent()) {
                        Produto pAlt = produtoParaAlterar.get();
                        System.out.print("Nova descrição (atual: " + pAlt.getDescricao() + "): "); 
                        String novaDesc = scanner.nextLine();
                        if (!novaDesc.isEmpty()) pAlt.setDescricao(novaDesc);

                        System.out.print("Novo custo (atual: " + pAlt.getCusto() + "): "); 
                        String novoCustoStr = scanner.nextLine();
                        if (!novoCustoStr.isEmpty()) pAlt.setCusto(Double.parseDouble(novoCustoStr));

                        System.out.print("Novo preço de venda (atual: " + pAlt.getPrecoVenda() + "): "); 
                        String novoPrecoStr = scanner.nextLine();
                        if (!novoPrecoStr.isEmpty()) pAlt.setPrecoVenda(Double.parseDouble(novoPrecoStr));

                        System.out.print("Novo código do fornecedor (atual: " + pAlt.getCodigoFornecedor() + "): "); 
                        String novoCodFornStr = scanner.nextLine();
                        if (!novoCodFornStr.isEmpty()) {
                            int novoCodForn = Integer.parseInt(novoCodFornStr);
                            if (pessoaRepo.loadAll().stream().anyMatch(p -> p.getCodigo() == novoCodForn && p instanceof Fornecedor)) {
                                pAlt.setCodigoFornecedor(novoCodForn);
                            } else {
                                System.out.println("Novo fornecedor não encontrado. Mantendo o anterior.");
                            }
                        }

                        produtoRepo.saveAll(lista);
                        LogService.log("Alteração de Produto ID: " + cAlt);
                        System.out.println("Produto alterado com sucesso!");
                    } else {
                        System.out.println("Produto não encontrado.");
                    }
                    break;
                case 4: // Excluir
                    System.out.print("Código do produto para excluir: "); int cExc = lerInteiro();
                    if(lista.removeIf(x -> x.getCodigo() == cExc)) {
                        produtoRepo.saveAll(lista);
                        LogService.log("Exclusão de Produto ID: " + cExc);
                        System.out.println("Produto excluído com sucesso!");
                    } else {
                        System.out.println("Produto não encontrado.");
                    }
                    break;
                case 0: System.out.println("Voltando ao Menu Principal..."); break;
                default: System.out.println("Opção inválida! Tente novamente.");
            }
        } while (op != 0);
    }

    private static void menuVendas() {
        int op;
        do {
            exibirMenu("--- MENU VENDAS ---");
            op = lerInteiro();
            List<Pedido> lista = pedidoRepo.loadAll();
            switch (op) {
                case 1: // Novo Pedido
                    System.out.print("Número do Pedido: "); int num = lerInteiro();
                    if (lista.stream().anyMatch(p -> p.getNumeroPedido() == num)) {
                        System.out.println("Já existe um pedido com este número.");
                        LogService.log("Tentativa de inclusão de Pedido com número duplicado: " + num);
                        break;
                    }
                    System.out.print("Código do Cliente: "); int codC = lerInteiro();
                    
                    Optional<Pessoa> clienteOpt = pessoaRepo.loadAll().stream()
                            .filter(p -> p.getCodigo() == codC && p instanceof Cliente)
                            .findFirst();
                    
                    if (clienteOpt.isEmpty()) {
                        System.out.println("Cliente não encontrado ou não é um cliente válido.");
                        LogService.log("Tentativa de novo Pedido com cliente inexistente: " + codC);
                        break;
                    }
                    Pessoa cliente = clienteOpt.get();

                    if (cliente.getEnderecos().isEmpty()) {
                        System.out.println("Cliente não possui endereços cadastrados. Adicione um endereço primeiro.");
                        break;
                    }
                    // Por simplicidade, pegamos o primeiro endereço. Em um sistema real, o usuário escolheria.
                    Endereco enderecoEntrega = cliente.getEnderecos().get(0);

                    Pedido ped = new Pedido(num, codC, enderecoEntrega);
                    
                    System.out.println("Adicionando produtos ao pedido (digite 0 para finalizar):");
                    List<Produto> produtosDisponiveis = produtoRepo.loadAll();
                    while(true) {
                        System.out.print("Código do Produto: "); int cp = lerInteiro();
                        if (cp == 0) break;

                        Optional<Produto> produtoAdicionar = produtosDisponiveis.stream().filter(pr -> pr.getCodigo() == cp).findFirst();
                        if (produtoAdicionar.isPresent()) {
                            ped.adicionarProduto(produtoAdicionar.get());
                            System.out.println("Produto " + produtoAdicionar.get().getDescricao() + " adicionado.");
                        } else {
                            System.out.println("Produto não encontrado.");
                        }
                    }

                    if (ped.getProdutos().isEmpty()) {
                        System.out.println("Pedido sem produtos. Cancelando pedido.");
                        break;
                    }

                    lista.add(ped);
                    pedidoRepo.saveAll(lista);
                    LogService.log("Novo Pedido Gerado: " + num + " para Cliente: " + codC);
                    System.out.println("Pedido " + num + " gerado com sucesso! Total: R$ " + String.format("%.2f", ped.getMontanteTotal()));
                    break;
                case 2: // Consultar
                    if (lista.isEmpty()) {
                        System.out.println("Nenhum pedido cadastrado.");
                        break;
                    }
                    System.out.print("Consultar por Número do Pedido (0 para listar todos): "); int nCons = lerInteiro();
                    if (nCons == 0) {
                        lista.forEach(p -> {
                            System.out.println(p);
                            System.out.println("  Endereço de Entrega: " + p.getEnderecoEntrega());
                            System.out.println("  Produtos:");
                            p.getProdutos().forEach(prod -> System.out.println("    - " + prod));
                        });
                    } else {
                        Optional<Pedido> pedidoConsultado = lista.stream().filter(p -> p.getNumeroPedido() == nCons).findFirst();
                        if (pedidoConsultado.isPresent()) {
                            Pedido pCons = pedidoConsultado.get();
                            System.out.println(pCons);
                            System.out.println("  Endereço de Entrega: " + pCons.getEnderecoEntrega());
                            System.out.println("  Produtos:");
                            pCons.getProdutos().forEach(prod -> System.out.println("    - " + prod));
                        } else {
                            System.out.println("Pedido não encontrado.");
                        }
                    }
                    break;
                case 3: // Alterar (Simplificado: não permite alterar itens do pedido, apenas o cliente ou endereço)
                    System.out.print("Número do pedido para alterar: "); int nAlt = lerInteiro();
                    Optional<Pedido> pedidoParaAlterar = lista.stream().filter(x -> x.getNumeroPedido() == nAlt).findFirst();
                    if (pedidoParaAlterar.isPresent()) {
                        Pedido pAlt = pedidoParaAlterar.get();
                        System.out.print("Novo Código do Cliente (atual: " + pAlt.getCodigoCliente() + "): ");
                        String novoCodCStr = scanner.nextLine();
                        if (!novoCodCStr.isEmpty()) {
                            int novoCodC = Integer.parseInt(novoCodCStr);
                            Optional<Pessoa> novoClienteOpt = pessoaRepo.loadAll().stream()
                                    .filter(p -> p.getCodigo() == novoCodC && p instanceof Cliente)
                                    .findFirst();
                            if (novoClienteOpt.isPresent() && !novoClienteOpt.get().getEnderecos().isEmpty()) {
                                // Não podemos alterar o cliente diretamente no objeto Pedido pois ele é final no construtor
                                // Para um sistema mais robusto, teríamos um setter ou recriaríamos o pedido.
                                // Por simplicidade, vamos apenas logar a tentativa.
                                System.out.println("Alteração de cliente não suportada diretamente. Recrie o pedido se necessário.");
                                LogService.log("Tentativa de alterar cliente do Pedido " + nAlt + " para " + novoCodC);
                            } else {
                                System.out.println("Novo cliente não encontrado ou sem endereço. Mantendo o anterior.");
                            }
                        }
                        pedidoRepo.saveAll(lista);
                        LogService.log("Alteração de Pedido ID: " + nAlt);
                        System.out.println("Pedido alterado (parcialmente) com sucesso!");
                    } else {
                        System.out.println("Pedido não encontrado.");
                    }
                    break;
                case 4: // Excluir
                    System.out.print("Número do pedido para excluir: "); int nExc = lerInteiro();
                    if(lista.removeIf(x -> x.getNumeroPedido() == nExc)) {
                        pedidoRepo.saveAll(lista);
                        LogService.log("Exclusão de Pedido: " + nExc);
                        System.out.println("Pedido excluído com sucesso!");
                    } else {
                        System.out.println("Pedido não encontrado.");
                    }
                    break;
                case 0: System.out.println("Voltando ao Menu Principal..."); break;
                default: System.out.println("Opção inválida! Tente novamente.");
            }
        } while (op != 0);
    }
}
