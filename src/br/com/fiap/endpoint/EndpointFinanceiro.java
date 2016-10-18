package br.com.fiap.endpoint;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;

import br.com.fiap.entidade.Cliente;
import br.com.fiap.facade.Financeiro;
import br.com.governo.ws.Imposto;
import br.com.governo.ws.WebServiceNF;
import br.com.governo.ws.WebServiceNFService;



@WebService
public class EndpointFinanceiro{
	
	private final Financeiro financeiro = new Financeiro();
	
	public EndpointFinanceiro() {
		
	}
	
	@WebMethod
	public Cliente cadastrarCliente(@WebParam(name = "cliente") Cliente cliente) throws Exception {
		return financeiro.cadastrarCliente(cliente);
	}

	@WebMethod
	public List<Cliente> listarClientes(@WebParam(name = "username",header=true) String username, 
										@WebParam(name = "password", header = true)String password) throws Exception {
		if(financeiro.isGerente(username, password)){
			return financeiro.listarClientes().stream().collect(Collectors.toList());
		}
		throw new Exception("Acesso Negado!");
	}

	@WebMethod
	public boolean cobrar(@WebParam(name = "indentificador") String indentificador, @WebParam(name = "valor") Double valor) throws Exception {
		return financeiro.cobrar(indentificador, valor);
	}

	@WebMethod
	public Double saldo(@WebParam(name = "username",header=true) String username, 
						@WebParam(name = "password", header = true)String password) throws Exception {
	
		Optional<Cliente> cliente = financeiro.getUsuario(username);
		if(cliente.isPresent()){
			if(cliente.get().getSenha().equals(password)){
				return cliente.get().getSaldo();
			}
		}	
		throw new Exception("Acesso Negado!");
		
	}

	@WebMethod
	public void cobrarMensalidade(@WebParam(name = "indentificador") String identificador,
								  @WebParam(name = "username",header=true) String username, 
								  @WebParam(name = "password", header = true)String password) throws Exception {
		
		if(!financeiro.isGerente(username, password)){
			throw new Exception("Acesso Negado!");
		}
		Optional<Cliente> cliente = financeiro.contemCliente(identificador);
		if(cliente.isPresent()){
			WebServiceNFService service = new WebServiceNFService();
	        WebServiceNF port = service.getWebServiceNFPort();
	        
	        Map<String, List<String>> header = new HashMap<>();
	        header.put("cpf", Collections.singletonList("12345678901"));
	        header.put("password", Collections.singletonList("123"));
			
			Map<String, Object> req_ctx = ((BindingProvider)port).getRequestContext();
			req_ctx.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					"http://fiapgoverno.brazilsouth.cloudapp.azure.com:8080/Governo/WebServiceNF?wsdl");
			req_ctx.put(MessageContext.HTTP_REQUEST_HEADERS, header);

			List<Imposto> impostos = port.listarImpostos();
	        
			Double valorAliquota = new Double(0);
			
			for(Imposto i : impostos)
				valorAliquota += i.getAliquota();
			
			Double valorImposto = valorAliquota * new Double(50.0);
			
			financeiro.cobrarMensalidade(identificador,valorImposto);
		}
	}
}
