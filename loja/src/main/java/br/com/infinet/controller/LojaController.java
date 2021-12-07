package br.com.infinet.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import br.com.infinet.payload.ImpostoPayload;
import br.com.infinet.payload.PedidoConcluidoPayload;
import br.com.infinet.payload.PedidoPayload;

@RestController
@RequestMapping
public class LojaController {
	@PostMapping
	public ResponseEntity<PedidoConcluidoPayload> realizaCompra(@RequestBody PedidoPayload pedidoPayload){
		RestTemplate restTemplate = new RestTemplate();
		
		ImpostoPayload imposto = restTemplate.getForObject("http://localhost:8080/?valor=" + pedidoPayload.getValor(), ImpostoPayload.class);
		return ResponseEntity.ok(PedidoConcluidoPayload.builder().uuid(UUID.randomUUID().toString()).build());
	}
	
}