package com.autobots.automanager.controles;

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

import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.repositorios.RepositorioUsuario;
import com.autobots.automanager.repositorios.RepositorioVeiculo;
import com.autobots.automanager.repositorios.RepositorioVenda;

@RestController
@RequestMapping("/veiculo")
public class VeiculoControle {
	
	@Autowired
	private RepositorioVeiculo repositorio;
	@Autowired
	private RepositorioUsuario repositorioUsuario;
	@Autowired
	private RepositorioVenda repositorioVenda;
	
	@GetMapping("/buscar")
	public ResponseEntity<List<Veiculo>> buscarVeiculos(){
		List<Veiculo> veiculos = repositorio.findAll();
		return new ResponseEntity<List<Veiculo>>(veiculos,HttpStatus.FOUND);
	}
	
	@GetMapping("/buscar/{id}")
	public ResponseEntity<Veiculo> buscarVeiculo(@PathVariable Long id){
		Veiculo veiculo = repositorio.findById(id).orElse(null);
		HttpStatus status = null;
		if(veiculo == null) {
			status = HttpStatus.NOT_FOUND;
		}else {
			status = HttpStatus.FOUND;
		}
		return new ResponseEntity<Veiculo>(veiculo,status);
	}
	
	@PostMapping("/cadastrar/{idUsuario}")
	public ResponseEntity<Usuario> cadastrarVeiculoCliente(@RequestBody Veiculo dados, @PathVariable Long idUsuario){
		Usuario usuario = repositorioUsuario.findById(idUsuario).orElse(null);
		HttpStatus status = null;
		if(usuario == null) {
			status = HttpStatus.NOT_FOUND;
		}else {
			dados.setProprietario(usuario);
			usuario.getVeiculos().add(dados);
			repositorioUsuario.save(usuario);
			status = HttpStatus.CREATED;
		}
		return new ResponseEntity<Usuario>(usuario,status);
	}
	
	@DeleteMapping("/excluir/{idVeiculo}")
	public ResponseEntity<?> excluirVeiculo(@PathVariable Long idVeiculo){
		List<Usuario> usuarios = repositorioUsuario.findAll();
		Veiculo verificacao = repositorio.findById(idVeiculo).orElse(null);
		
		if(verificacao == null) {

			return new ResponseEntity<>("Veiculo não encontrado...",HttpStatus.NOT_FOUND);

		}else {
			//usuario
			for(Usuario usuario: repositorioUsuario.findAll()) {
				if(!usuario.getVeiculos().isEmpty()) {
					for(Veiculo veiculoUsuario: usuario.getVeiculos()) {
						if(veiculoUsuario.getId() == idVeiculo) {
							for(Usuario usuarioRegistrado: usuarios) {
								usuarioRegistrado.getVeiculos().remove(veiculoUsuario);
							}
						}
					}
				}
			}
			
			//venda
			for(Venda venda: repositorioVenda.findAll()) {
				if(venda.getVeiculo() != null) {
					if(venda.getVeiculo().getId() == idVeiculo) {
						venda.setVeiculo(null);
					}
				}
			}

			usuarios = repositorioUsuario.findAll();
			repositorio.deleteById(idVeiculo);
			return new ResponseEntity<>("Veiculo excluido com sucesso...",HttpStatus.ACCEPTED);
		}
	}
}
