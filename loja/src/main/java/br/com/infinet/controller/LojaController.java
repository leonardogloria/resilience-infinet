package br.com.infinet.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.infinet.payload.PedidoConcluidoPayload;
import br.com.infinet.payload.PedidoPayload;
import br.com.infinet.service.ImpostoService;

@RestController
@RequestMapping
public class LojaController {
	@Autowired
	ImpostoService impostoService;
	@PostMapping
	public ResponseEntity<PedidoConcluidoPayload> realizaCompra(@RequestBody PedidoPayload pedidoPayload){
		Double valorImposto = this.impostoService.getImposto(pedidoPayload);
		return ResponseEntity.ok(PedidoConcluidoPayload.builder().uuid(UUID.randomUUID().toString()).valorImposto(valorImposto).build());
	}
	
}
