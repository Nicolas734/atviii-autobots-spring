package com.autobots.automanager.controles;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.RepositorioTelefone;
import com.autobots.automanager.repositorios.RepositorioUsuario;

@RestController
@RequestMapping("/telefone")
public class TelefoneControle {
	
	@Autowired
	private RepositorioTelefone repositorio;
	@Autowired
	private RepositorioUsuario repositorioUsuario;
	@Autowired
	private RepositorioEmpresa repositorioEmpresa;
	
	@GetMapping("/buscar")
	public ResponseEntity<List<Telefone>> buscarTelefones(){
		List<Telefone> telefones = repositorio.findAll();
		return new ResponseEntity<List<Telefone>>(telefones,HttpStatus.FOUND);
	}
	
	@GetMapping("/buscar/{id}")
	public ResponseEntity<Telefone> buscarTelefone(@PathVariable Long id){
		Telefone telefone = repositorio.findById(id).orElse(null);
		HttpStatus status = null;
		if(telefone == null) {
			status = HttpStatus.NOT_FOUND;
		}else {
			status = HttpStatus.FOUND;
		}
		return new ResponseEntity<Telefone>(telefone,status);
	}
	
	@DeleteMapping("/excluir/{idTelefone}")
	public ResponseEntity<?> excluirTelefone(@PathVariable Long idTelefone){
		Telefone verificacao = repositorio.findById(idTelefone).orElse(null);
		if(verificacao == null) {
			return new ResponseEntity<>("Telefone não econtrado...", HttpStatus.NOT_FOUND);
		}else {
			
			//usuario
			for(Usuario usuario: repositorioUsuario.findAll()) {
				if(!usuario.getTelefones().isEmpty()) {
					for(Telefone telefone: usuario.getTelefones()) {
						if(telefone.getId() == idTelefone) {
							usuario.getTelefones().remove(telefone);
							repositorioUsuario.save(usuario);
						}
						break;
					}
				}
			}
			
			//empresa
			for(Empresa empresa: repositorioEmpresa.findAll()) {
				if(!empresa.getTelefones().isEmpty()) {
					for(Telefone telefone: empresa.getTelefones()) {
						if(telefone.getId() == idTelefone) {
							empresa.getTelefones().remove(telefone);
							repositorioEmpresa.save(empresa);
						}
						break;
					}
				}
			}
			
			return new ResponseEntity<>("Telefone excluido com sucesso...", HttpStatus.ACCEPTED);
		}
	}

}
