package com.autobots.automanager.controle;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entitades.Email;
import com.autobots.automanager.repositorios.RepositorioEmail;

@RestController
@RequestMapping("/email")
public class EmailControle {
	
	@Autowired
	private RepositorioEmail repositorio;
	
	@GetMapping("/buscar")
	public ResponseEntity<List<Email>> buscarEmails(){
		List<Email> emails = repositorio.findAll();
		return new ResponseEntity<List<Email>>(emails,HttpStatus.FOUND);
	}
	
	@GetMapping("/buscar/{id}")
	public ResponseEntity<Email> buscarEmail(@PathVariable Long id){
		Email email = repositorio.findById(id).orElse(null);
		HttpStatus status = null;
		if(email == null) {
			status = HttpStatus.NOT_FOUND;
		}else {
			status = HttpStatus.FOUND;
		}
		return new ResponseEntity<Email>(email,status);
	}
}
