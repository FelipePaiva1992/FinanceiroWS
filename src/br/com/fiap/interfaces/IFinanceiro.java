package br.com.fiap.interfaces;

import java.util.Set;

import br.com.fiap.entidade.Cliente;

public interface IFinanceiro {
	
	Cliente cadastrarCliente(Cliente cliente) throws Exception;
	Set<Cliente> listarClientes();
	boolean cobrar(String indentificador, Double valor);
	Double saldo(String indentificador) throws Exception;
	void cobrarMensalidade(String identificador,Double valor);
	

}
