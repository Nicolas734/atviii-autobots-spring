package com.autobots.automanager.controles;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Credencial;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.entidades.Email;
import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.enumeracoes.PerfilUsuario;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.RepositorioUsuario;
import com.autobots.automanager.repositorios.RepositorioVeiculo;
import com.autobots.automanager.repositorios.RepositorioVenda;

@RestController
@RequestMapping("/usuario")
public class UsuarioControle {
	
	@Autowired
	private RepositorioUsuario repositorio;
	@Autowired
	private RepositorioEmpresa repositorioEmpresa;
	@Autowired
	private RepositorioVenda repositorioVenda;
	@Autowired
	private RepositorioVeiculo repositorioVeiculo;
	
	@GetMapping("/buscar")
	public ResponseEntity<List<Usuario>> buscarUsuarios(){
		List<Usuario> usuarios = repositorio.findAll();
		return new ResponseEntity<List<Usuario>>(usuarios,HttpStatus.FOUND);
	}
	
	@GetMapping("/buscar/{id}")
	public ResponseEntity<Usuario> buscarUsuario(@PathVariable Long id){
		Usuario usuario = repositorio.findById(id).orElse(null);
		HttpStatus status = null;
		if(usuario == null) {
			status = HttpStatus.NOT_FOUND;
		}else {
			status = HttpStatus.FOUND;
		}
		return new ResponseEntity<Usuario>(usuario,status);
	}
	
	@PostMapping("/cadastrar-cliente")
	public ResponseEntity<Usuario> cadastrarUsuarioCliente(@RequestBody Usuario dados){
		dados.getPerfis().add(PerfilUsuario.CLIENTE);
		Usuario usuario = repositorio.save(dados);
		return new ResponseEntity<Usuario>(usuario,HttpStatus.CREATED);
	}
	
	@PostMapping("/cadastrar-funcionario/{idEmpresa}")
	public ResponseEntity<?> cadastrarUsuarioFuncionario(@RequestBody Usuario dados, @PathVariable Long idEmpresa){
		dados.getPerfis().add(PerfilUsuario.FUNCIONARIO);
		Empresa empresa = repositorioEmpresa.findById(idEmpresa).orElse(null);
		if(empresa == null) {
			return new ResponseEntity<String>("Empresa não encontrada...", HttpStatus.NOT_FOUND);
		}else {
			empresa.getUsuarios().add(dados);
			return new ResponseEntity<Empresa>(empresa, HttpStatus.CREATED);
		}
	}
	
	@PostMapping("/cadastrar-fornecedor")
	public ResponseEntity<Usuario> cadastrarUsuarioFornecedor(@RequestBody Usuario dados){
		dados.getPerfis().add(PerfilUsuario.FORNECEDOR);
		Usuario usuario = repositorio.save(dados);
		return new ResponseEntity<Usuario>(usuario,HttpStatus.CREATED);
	}
	
	//@PutMapping("/atualizar/{idUsuario}")
	
	/*@DeleteMapping("/excluir/{idUsuario}")
	public ResponseEntity<?> excluirCliente(@PathVariable Long idUsuario){
		Usuario usuario = repositorio.findById(idUsuario).orElse(null);
		if(usuario == null) {
			return new ResponseEntity<String>("Usuario não encontrado",HttpStatus.NOT_FOUND);
		}else {
			Set<Documento> documentos = usuario.getDocumentos();
			Set<Telefone> telefones = usuario.getTelefones();
			Set<Email> emails = usuario.getEmails();
			Set<Credencial> credenciais = usuario.getCredenciais();
			Set<Mercadoria> mercadorias = usuario.getMercadorias();
			Set<Venda> vendas = usuario.getVendas();
			Set<Veiculo> veiculos = usuario.getVeiculos();
			if(vendas.size() > 0) {
				for(Venda vendaRegistrada: repositorioVenda.findAll()) {
					if(vendaRegistrada.getCliente().getId() == idUsuario) {
						vendaRegistrada.setCliente(null);
					}
					if(vendaRegistrada.getFuncionario().getId() == idUsuario) {
						vendaRegistrada.setFuncionario(null);
					}
				}
			}
			if(veiculos.size() > 0) {
				for(Veiculo veiculoRegistrado: repositorioVeiculo.findAll()) {
					if(veiculoRegistrado.getProprietario().getId() == idUsuario) {
						veiculoRegistrado.setProprietario(null);
					}
				}
			}
			
			usuario.getDocumentos().removeAll(documentos);
			usuario.getTelefones().removeAll(telefones);
			usuario.getEmails().removeAll(emails);
			usuario.getCredenciais().removeAll(credenciais);
			usuario.getMercadorias().removeAll(mercadorias);
			usuario.getVeiculos().removeAll(veiculos);
			usuario.getVendas().removeAll(vendas);
			usuario.setEndereco(null);

			repositorio.delete(usuario);
			return new ResponseEntity<String>("Usuario excluido com sucesso",HttpStatus.ACCEPTED);
		}
	}*/
}