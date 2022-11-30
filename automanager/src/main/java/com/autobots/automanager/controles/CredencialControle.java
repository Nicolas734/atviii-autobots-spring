package com.autobots.automanager.controles;

import java.util.Date;
import java.util.List;
import java.util.Set;

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

import com.autobots.automanager.entidades.Credencial;
import com.autobots.automanager.entidades.CredencialUsuarioSenha;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.repositorios.RepositorioCredencialUsuarioSenha;
import com.autobots.automanager.repositorios.RepositorioUsuario;

@RestController
@RequestMapping("/credencial")
public class CredencialControle {
	
	@Autowired
	private RepositorioCredencialUsuarioSenha repositorioCredencialUsuarioSenha;
	@Autowired
	private RepositorioUsuario repositorioUsuario;
	
	@GetMapping("/buscar")
	public ResponseEntity<?> buscarCredenciaisUsuariosSenhas(){
		List<CredencialUsuarioSenha> credenciais = repositorioCredencialUsuarioSenha.findAll();
		if(credenciais.size() > 0) {
			return new ResponseEntity<List<CredencialUsuarioSenha>>(credenciais, HttpStatus.FOUND);
		}else {
			return new ResponseEntity<String>("Nenhuma credencial encontrada...", HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/buscar/{id}")
	public ResponseEntity<?> buscarCredencialUsuarioSenhaPorId(@PathVariable Long id){
		CredencialUsuarioSenha credencial = repositorioCredencialUsuarioSenha.findById(id).orElse(null);
		if(credencial == null) {
			return new ResponseEntity<String>("credencial não encontrada...", HttpStatus.NOT_FOUND);
		}else {
			return new ResponseEntity<CredencialUsuarioSenha>(credencial, HttpStatus.FOUND);
		}
	}
	
	@GetMapping("/buscar-username")
	public ResponseEntity<?> buscarCredencialUsuarioSenhaPorNomeUsuario(@RequestBody CredencialUsuarioSenha dados){
		List<CredencialUsuarioSenha> credenciais = repositorioCredencialUsuarioSenha.findAll();
		CredencialUsuarioSenha credencial = null;
		for( CredencialUsuarioSenha c:credenciais) {
			if(c.getNomeUsuario().equals(dados.getNomeUsuario())) {
				credencial = c;
			}
		}
		if(credencial == null) {
			return new ResponseEntity<String>("credencial não encontrada...", HttpStatus.NOT_FOUND);
		}else {
			return new ResponseEntity<CredencialUsuarioSenha>(credencial, HttpStatus.FOUND);
		}
	}
	
	@PostMapping("/cadastrar/{idUsuario}")
	public ResponseEntity<?> cadastrarCredencialUserSenha(@RequestBody CredencialUsuarioSenha dados, @PathVariable Long idUsuario){
		Usuario usuario = repositorioUsuario.findById(idUsuario).orElse(null);
		if(usuario == null) {
			return new ResponseEntity<String>("Usuario não encontrado...",HttpStatus.NOT_FOUND);
		}else {
			List<CredencialUsuarioSenha> credenciais = repositorioCredencialUsuarioSenha.findAll();
			Boolean verificador = false;
			for(CredencialUsuarioSenha credencial: credenciais) {
				if(dados.getNomeUsuario().equals(credencial.getNomeUsuario())) {
					verificador = true;
				}
			}
			if (verificador == true) {
				return new ResponseEntity<String>("Credencial ja existente...",HttpStatus.CONFLICT);
			}else {
				dados.setCriacao(new Date());
				usuario.getCredenciais().add(dados);
				repositorioUsuario.save(usuario);
				return new ResponseEntity<Usuario>(usuario,HttpStatus.CREATED);
			}
		}
	}
	
	@DeleteMapping("/excluir/{idCredencial}")
	public ResponseEntity<?> excluirCredencialUserSenha(@PathVariable Long idCredencial){
		CredencialUsuarioSenha verificacao = repositorioCredencialUsuarioSenha.findById(idCredencial).orElse(null);
		if(verificacao == null) {
			return new ResponseEntity<String>("credencial não encontrada...", HttpStatus.NOT_FOUND);
		}else {
			
			for(Usuario usuario:repositorioUsuario.findAll()) {
				if(!usuario.getCredenciais().isEmpty()) {
					for(Credencial credencial: usuario.getCredenciais()) {
						if(credencial.getId() == idCredencial) {
							usuario.getCredenciais().remove(credencial);
							repositorioUsuario.save(usuario);
						}
						break;
					}
				}
			}
			
			return new ResponseEntity<>("Credencial excluida com sucesso...", HttpStatus.ACCEPTED);
		}
	}

}
