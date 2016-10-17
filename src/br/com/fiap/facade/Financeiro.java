package br.com.fiap.facade;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Random;

import br.com.fiap.entidade.Cliente;
import br.com.fiap.entidade.Gerente;
import br.com.fiap.interfaces.IFinanceiro;

public class Financeiro implements IFinanceiro {

	private Set<Cliente> clientes = new HashSet<>();
	private List<Gerente> gerentes = new ArrayList<>();
	
	public Financeiro() {
		gerentes.add(new Gerente("felipe","felipe123"));
		gerentes.add(new Gerente("antonio","antonio123"));
		gerentes.add(new Gerente("vinicius","vini123"));
	}

	public Cliente cadastrarCliente(Cliente cliente) throws Exception {
		boolean clienteJaExiste =  clientes.add(cliente);
		if (!clienteJaExiste) {
			throw new Exception("Usuário já cadastrado");
		} 
		Random random = new Random();
		String user = cliente.getNome().substring(0, Math.min(cliente.getNome().length(), 5));
		String pass = user + random.nextInt(999);
		
		cliente.setUsuario(user);
		cliente.setSenha(pass);
		
		return cliente;
		
	}

	public Set<Cliente> listarClientes() {
		return clientes;
	}

	public boolean cobrar(String indentificador, Double valor) {
		Optional<Cliente> cliente = contemCliente(indentificador);

		if (cliente.isPresent() && cliente.get().isSaldoMaiorQue(valor)) {
			Cliente clieteEncontrado = cliente.get();
			clieteEncontrado.subtrair(valor);
			return true;
		} 
			return false;
	}

	public Optional<Cliente> contemCliente(String indentificador) {
		return clientes.stream().filter(o -> o.getIdentificador().equals(indentificador)).findFirst();
	}

	public Double saldo(String indentificador) throws Exception {
		Optional<Cliente> cliente = contemCliente(indentificador);
		if(cliente.isPresent()){
			return cliente.get().getSaldo(); 
		}
		throw new Exception("Usuário não encontrado");
	}

	public void cobrarMensalidade(String identificador,Double valorImposto) {
		Double valorTotal = valorImposto + new Double(50.0);
		cobrar(identificador, valorTotal);
	}
	
	public boolean isGerente(String username, String password) {
		Optional<Gerente> gerentePesquisado = gerentes.stream().filter(gerente -> gerente.getUsername().equals(username) && gerente.getPassword().equals(password)).findFirst();
		return gerentePesquisado.isPresent();
	}
	
	public Optional<Cliente> getUsuario(String username){
		return clientes.stream().filter(o -> o.getUsuario().equals(username)).findFirst();
	}
	
	
}
