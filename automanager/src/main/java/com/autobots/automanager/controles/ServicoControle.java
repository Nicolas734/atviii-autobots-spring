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

import com.autobots.automanager.entidades.Empresa;
import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.RepositorioServico;
import com.autobots.automanager.repositorios.RepositorioVenda;

@RestController
@RequestMapping("/servico")
public class ServicoControle {

	@Autowired
	private RepositorioServico repositorio;
	@Autowired
	private RepositorioEmpresa repositorioEmpresa;
	@Autowired
	private RepositorioVenda repositorioVenda;
	
	@GetMapping("/buscar")
	public ResponseEntity<List<Servico>> buscarServicos(){
		List<Servico> servicos = repositorio.findAll();
		return new ResponseEntity<List<Servico>>(servicos, HttpStatus.FOUND);
	}
	
	@GetMapping("/buscar/{id}")
	public ResponseEntity<Servico> buscarServico(@PathVariable Long id){
		Servico servico = repositorio.findById(id).orElse(null);
		HttpStatus status = null;
		if(servico == null) {
			status = HttpStatus.NOT_FOUND;
		}else {
			status = HttpStatus.FOUND;
		}
		return new ResponseEntity<Servico>(servico, status);
	}
	
	@PostMapping("/cadastrar/{idEmpresa}")
	public ResponseEntity<Empresa> cadastrarServicoEmpresa(@RequestBody Servico dados, @PathVariable Long idEmpresa){
		dados.setOriginal(true);
		Empresa empresa = repositorioEmpresa.findById(idEmpresa).orElse(null);
		HttpStatus status = null;
		if(empresa == null) {
			status = HttpStatus.NOT_FOUND;
		}else {
			empresa.getServicos().add(dados);
			repositorioEmpresa.save(empresa);
			status = HttpStatus.CREATED;
		}
		return new ResponseEntity<Empresa>(empresa, status);
	}
	
	@DeleteMapping("/excluir/{idServico}")
	public ResponseEntity<?> excluirServico(@PathVariable Long idServico){
		List<Empresa> empresas = repositorioEmpresa.findAll();
		List<Venda> vendas = repositorioVenda.findAll();
		Servico verificador = repositorio.findById(idServico).orElse(null);
		
		if(verificador == null) {
			return new ResponseEntity<>("Servico não encontrado", HttpStatus.NOT_FOUND);
		}else {
			//empresa
			for(Empresa empresa: repositorioEmpresa.findAll()) {
				if(empresa.getServicos().size() > 0) {
					for(Servico servicoEmpresa: empresa.getServicos()) {
						if(servicoEmpresa.getId() == idServico) {
							for(Empresa empresaRegistrado:empresas) {
								empresaRegistrado.getServicos().remove(servicoEmpresa);
							}
						}
					}
				}
			}

			//venda
			for(Venda venda: repositorioVenda.findAll()) {
				if(venda.getServicos().size() > 0) {
					for(Servico servicoVenda: venda.getServicos()) {
						if(servicoVenda.getId() == idServico) {
							for(Venda vendaRegistrada: vendas) {
								vendaRegistrada.getServicos().remove(servicoVenda);
							}
						}
					}
				}
			}
			
			repositorio.deleteById(idServico);
			return new ResponseEntity<>("Serviço excluido com sucesso...",HttpStatus.ACCEPTED);
		}
	}
	
}