package br.com.infinet.service.impl;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.infinet.service.ImpostoService;

@Service
public class ImpostoServiceImpl implements ImpostoService {
	@Value(value = "${server.delay}")
	Long delay;
	@Override
	public Double getImposto(Double valor)  {
		
		Double valorImposto = (valor * 1.3) - valor;
		try {
			Thread.sleep(delay);
			
		}catch (Exception e) {
		
		}
		System.out.println("Valor do Imposto: " + valorImposto );
		return valorImposto;
		
	}

}
