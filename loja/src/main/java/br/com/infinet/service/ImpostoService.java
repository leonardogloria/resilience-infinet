package br.com.infinet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.com.infinet.payload.ImpostoPayload;
import br.com.infinet.payload.PedidoPayload;

@Service
public class ImpostoService {
	@Autowired
	RestTemplate restTemplate;
	
	public Double getImposto(PedidoPayload pedidoPayload) {
		//RestTemplate restTemplate = new RestTemplate();
		ImpostoPayload imposto = restTemplate.getForObject("http://imposto/?valor=" + pedidoPayload.getValor(),
				ImpostoPayload.class);
		return imposto.getValorImposto();
	}

	public Double getImpostoFallback(PedidoPayload pedidoPayload) {
		System.out.println("Fallback called");
		return 1.0D;

	
	}
	public Double lastFallBack() {
		return 1.0D;
		
	}

}
