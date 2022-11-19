package com.autobots.automanager.controle;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.autobots.automanager.entitades.Empresa;
import com.autobots.automanager.repositorios.RepositorioEmpresa;

@RestController
@RequestMapping("/empresa")
public class EmpresaControle {
	
	@Autowired
	private RepositorioEmpresa repositorio;
	
	@GetMapping("/buscar")
	public ResponseEntity<List<Empresa>> buscarEmpresas() {
		List<Empresa> empresas = repositorio.findAll();
		return new ResponseEntity<List<Empresa>>(empresas,HttpStatus.FOUND);
	}
	
	@GetMapping("/buscar/{id}")
	public ResponseEntity<Empresa> buscarEmpresa(@PathVariable Long id){
		Empresa empresa = repositorio.findById(id).orElse(null);
		HttpStatus status = null;
		if(empresa == null) {
			status = HttpStatus.NOT_FOUND;
		}else {
			status = HttpStatus.FOUND;
		}
		return new ResponseEntity<Empresa>(empresa,status);
	}
	
	@PostMapping("/cadastrar")
	public ResponseEntity<Empresa> cadastrarEmpresa(@RequestBody Empresa dados){
		dados.setCadastro(new Date());
		Empresa empresa = repositorio.save(dados);
		return new ResponseEntity<Empresa>(empresa,HttpStatus.CREATED);
	}

}
