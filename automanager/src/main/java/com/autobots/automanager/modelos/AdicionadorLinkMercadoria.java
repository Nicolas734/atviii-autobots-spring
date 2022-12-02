package com.autobots.automanager.modelos;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import com.autobots.automanager.controles.MercadoriaControle;
import com.autobots.automanager.entidades.Mercadoria;

public class AdicionadorLinkMercadoria implements AdicionadorLink<Mercadoria>{

	@Override
	public void adicionarLink(List<Mercadoria> lista) {
		for(Mercadoria mercadoria:lista) {
			long id = mercadoria.getId();
			Link linkProprio = WebMvcLinkBuilder
					.linkTo(WebMvcLinkBuilder
							.methodOn(MercadoriaControle.class)
							.buscarMercadoria(id))
					.withSelfRel();
			mercadoria.add(linkProprio);
		}
	}

	@Override
	public void adicionarLink(Mercadoria objeto) {
		Link linkProprio = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(MercadoriaControle.class)
						.buscarMercadorias())
				.withRel("Lista de clientes");
		objeto.add(linkProprio);
	}

	@Override
	public void adiconarLinkUpdate(Mercadoria objeto) {
		Link linkProprio = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(MercadoriaControle.class)
						.atualizarMercadoria(objeto.getId(), objeto))
				.withSelfRel();
		objeto.add(linkProprio);
	}

	@Override
	public void adicionarLinkDelete(Mercadoria objeto) {
		Link linkProprio = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(MercadoriaControle.class)
						.excluirMercadoriaEmpresa(objeto.getId()))
				.withSelfRel();
		objeto.add(linkProprio);
	}

}
