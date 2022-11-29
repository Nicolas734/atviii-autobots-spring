package com.autobots.automanager.controles;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.RepositorioMercadoria;
import com.autobots.automanager.repositorios.RepositorioUsuario;
import com.autobots.automanager.repositorios.RepositorioVenda;

@RestController
@RequestMapping("/mercadoria")
public class MercadoriaControle {
	
	@Autowired
	private RepositorioMercadoria repositorio;
	@Autowired
	private RepositorioEmpresa repositorioEmpresa;
	@Autowired
	private RepositorioUsuario repositorioUsuario;
	@Autowired
	private RepositorioVenda repositorioVenda;
	
	@GetMapping("/buscar")
	public ResponseEntity<List<Mercadoria>> buscarMercadorias(){
		List<Mercadoria> mercadorias = repositorio.findAll();
		return new ResponseEntity<List<Mercadoria>>(mercadorias, HttpStatus.FOUND);
	}
	
	@GetMapping("/buscar/{id}")
	public ResponseEntity<Mercadoria> buscarMercadoria(@PathVariable Long id){
		Mercadoria mercadoria = repositorio.findById(id).orElse(null);
		HttpStatus status = null;
		if(mercadoria == null) {
			status = HttpStatus.NOT_FOUND;
		}else {
			status = HttpStatus.FOUND;
		}
		return new ResponseEntity<Mercadoria>(mercadoria,status);
	}
	
	@PostMapping("/cadastrar/{idEmpresa}")
	public ResponseEntity<Empresa> cadastrarMercadoriaEmpresa(@RequestBody Mercadoria dados, @PathVariable Long idEmpresa){
		dados.setOriginal(true);
		Empresa empresa = repositorioEmpresa.findById(idEmpresa).orElse(null);
		dados.setCadastro(new Date());
		HttpStatus status = null;
		if(empresa == null) {
			status = HttpStatus.NOT_FOUND;
		}else {
			empresa.getMercadorias().add(dados);
			repositorioEmpresa.save(empresa);
			status = HttpStatus.CREATED;
		}
		return new ResponseEntity<Empresa>(empresa,status);
	}
	
	@DeleteMapping("/excluir/{idMercadoria}")
	public ResponseEntity<?> excluirMercadoriaEmpresa(@PathVariable Long idMercadoria){
		List<Empresa> empresas = repositorioEmpresa.findAll();
		List<Usuario> usuarios = repositorioUsuario.findAll();
		List<Venda> vendas = repositorioVenda.findAll();
		
		//empresa
		for(Empresa empresa: repositorioEmpresa.findAll()) {
			if(empresa.getMercadorias().size() > 0) {
				for(Mercadoria mercadoriaEmpresa: empresa.getMercadorias()) {
					if(mercadoriaEmpresa.getId() == idMercadoria) {
						for(Empresa empresaRegistrada: empresas) {
							empresaRegistrada.getMercadorias().remove(mercadoriaEmpresa);
						}
					}
				}
			}
		}
		
		//usuario
		for(Usuario usuario: repositorioUsuario.findAll()) {
			if(usuario.getMercadorias().size() > 0) {
				for(Mercadoria mercadoriaUsuario:usuario.getMercadorias()) {
					if(mercadoriaUsuario.getId() == idMercadoria) {
						for(Usuario usuarioRegistrado: usuarios) {
							usuarioRegistrado.getMercadorias().remove(mercadoriaUsuario);
						}
					}
				}
			}
		}
		
		//venda
		for(Venda venda: repositorioVenda.findAll()) {
			if(venda.getMercadorias().size() > 0) {
				for(Mercadoria mercadoriaVenda: venda.getMercadorias()) {
					if(mercadoriaVenda.getId() == idMercadoria) {
						for(Venda vendaRegistrada:vendas) {
							vendaRegistrada.getMercadorias().remove(mercadoriaVenda);
						}
					}
				}
			}
		}

		repositorio.deleteById(idMercadoria);
		return new ResponseEntity<>("Mercadoria excluida com sucesso...",HttpStatus.ACCEPTED);
	}

}