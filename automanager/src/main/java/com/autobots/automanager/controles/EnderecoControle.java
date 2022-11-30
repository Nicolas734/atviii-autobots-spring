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
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.RepositorioEndereco;
import com.autobots.automanager.repositorios.RepositorioUsuario;

@RestController
@RequestMapping("/endereco")
public class EnderecoControle {
	
	@Autowired
	private RepositorioEndereco repositorio;
	@Autowired
	private RepositorioUsuario repositorioUsuario;
	@Autowired
	private RepositorioEmpresa repositorioEmpresa;
	
	@GetMapping("/buscar")
	public ResponseEntity<List<Endereco>> buscarEnderecos(){
		List<Endereco> enderecos = repositorio.findAll();
		return new ResponseEntity<List<Endereco>>(enderecos,HttpStatus.FOUND);
	}
	
	@GetMapping("/buscar/{id}")
	public ResponseEntity<Endereco> buscarEndereco(@PathVariable Long id){
		Endereco endereco = repositorio.findById(id).orElse(null);
		HttpStatus status = null;
		if(endereco == null) {
			status = HttpStatus.NOT_FOUND;
		}else {
			status = HttpStatus.FOUND;
		}
		return new ResponseEntity<Endereco>(endereco,status);
	}
	
	@DeleteMapping("/excluir/{idEndereco}")
	public ResponseEntity<?> excluirEndereco(@PathVariable Long idEndereco){
		Endereco verificacao = repositorio.findById(idEndereco).orElse(null);
		if(verificacao == null) {
			return new ResponseEntity<>("Endereco não econtrado...", HttpStatus.NOT_FOUND);
		}else {

			//usuario
			for(Usuario usuario: repositorioUsuario.findAll()) {
				if(usuario.getEndereco() != null) {
					if(usuario.getEndereco().getId() == idEndereco) {
						usuario.setEndereco(null);
						repositorioUsuario.save(usuario);
					}
				}
				break;
			}

			//empresa
			for(Empresa empresa: repositorioEmpresa.findAll()) {
				if(empresa.getEndereco() != null) {
					if(empresa.getEndereco().getId() == idEndereco) {
						empresa.setEndereco(null);
						repositorioEmpresa.save(empresa);
					}
				}
				break;
			}
			
			return new ResponseEntity<>("Endereco excluido com sucesso...", HttpStatus.ACCEPTED);
		}
	}

}
