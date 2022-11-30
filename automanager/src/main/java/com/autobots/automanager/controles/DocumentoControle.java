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

import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.repositorios.RepositorioDocumento;
import com.autobots.automanager.repositorios.RepositorioUsuario;

@RestController
@RequestMapping("/documento")
public class DocumentoControle {

	@Autowired
	private RepositorioDocumento repositorio;
	@Autowired
	private RepositorioUsuario repositorioUsuario;
	
	@GetMapping("/buscar")
	public ResponseEntity<List<Documento>> buscarDocumentos(){
		List<Documento> documentos = repositorio.findAll();
		return new ResponseEntity<List<Documento>>(documentos,HttpStatus.FOUND);
	}
	
	@GetMapping("/buscar/{id}")
	public ResponseEntity<Documento> buscarDocumento(@PathVariable Long id){
		Documento documento = repositorio.findById(id).orElse(null);
		HttpStatus status = null;
		if(documento == null) {
			status = HttpStatus.NOT_FOUND;
		}else {
			status = HttpStatus.FOUND;
		}
		return new ResponseEntity<Documento>(documento,status);
	}
	
	@DeleteMapping("/excluir/{idDocumento}")
	public ResponseEntity<?> excluirDocumento(@PathVariable Long idDocumento){
		Documento verificacao = repositorio.findById(idDocumento).orElse(null);
		
		if(verificacao == null) {
			return new ResponseEntity<>("Documento não econtrado...", HttpStatus.NOT_FOUND);
		}else {
			for(Usuario usuario: repositorioUsuario.findAll()) {
				if(!usuario.getDocumentos().isEmpty()) {
					for(Documento documento: usuario.getDocumentos()) {
						if(documento.getId() == idDocumento) {
							usuario.getDocumentos().remove(documento);
							repositorioUsuario.save(usuario);
						}
						break;
					}
				}
			}
			
			return new ResponseEntity<>("Documento excluido com sucesso...", HttpStatus.ACCEPTED);
		}
	}
}
