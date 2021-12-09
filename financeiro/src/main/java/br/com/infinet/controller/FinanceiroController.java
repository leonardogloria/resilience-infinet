package br.com.infinet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.infinet.payload.ImpostoPayload;
import br.com.infinet.service.ImpostoService;

@RestController
@RequestMapping
public class FinanceiroController {
	@Autowired
	ImpostoService impostoService;
	
	@Value(value = "${server.delay}")
	Long delay;

	@GetMapping()
	public @ResponseBody ResponseEntity<ImpostoPayload> getImposto(@RequestParam Double valor) throws Exception {
		
		Double imposto = 0.0;
		if(delay == 5) throw new Exception();
		imposto = this.impostoService.getImposto(valor);
		return ResponseEntity.ok(ImpostoPayload.builder().valorImposto(imposto).build());
	}
}
