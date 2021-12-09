package br.com.infinet.controller;


import java.time.Duration;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.infinet.payload.PedidoConcluidoPayload;
import br.com.infinet.payload.PedidoPayload;
import br.com.infinet.service.ImpostoService;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;

@RestController
@RequestMapping
public class LojaController {
	@Autowired
	ImpostoService impostoService;
	@Autowired
	CircuitBreakerFactory circuitBreakerFactory;


	@PostMapping
	public ResponseEntity<PedidoConcluidoPayload> realizaCompra(@RequestBody PedidoPayload pedidoPayload) {
		org.springframework.cloud.client.circuitbreaker.CircuitBreaker
		circuitBreaker = circuitBreakerFactory.create("impostoCB");
		
		Double imposto = circuitBreaker.run( () -> impostoService.getImposto(pedidoPayload),
				throwable -> impostoService.getImpostoFallback(pedidoPayload)
				);
		
		//Double imposto = this.impostoService.getImposto(pedidoPayload);
		return ResponseEntity.ok(PedidoConcluidoPayload.builder().valorImposto(imposto).uuid(UUID.randomUUID().toString()).build());
	}
	@PostMapping("/compra2")
	public ResponseEntity<PedidoConcluidoPayload> realizaCompraRetry(@RequestBody PedidoPayload pedidoPayload){
		Logger logger = Logger.getLogger(LojaController.class.getName());
		logger.info(pedidoPayload.getCliente().toString());
		logger.info(pedidoPayload.getValor().toString());
		RetryConfig config = RetryConfig.custom().maxAttempts(5).waitDuration(Duration.ofSeconds(2)).build();
		RetryRegistry registry = RetryRegistry.of(config);
		Retry retry = registry.retry("impostoRetry");
		
		Function<Void,Double> decorateFunction = Retry.decorateFunction(retry, (Integer) -> {
			return impostoService.getImposto(pedidoPayload);
			
		});
		Double imposto = decorateFunction.apply(null);
		return ResponseEntity.ok(PedidoConcluidoPayload.builder().valorImposto(imposto).uuid(UUID.randomUUID().toString()).build());
		
		
	}
	@PostMapping("/compra3")
	public ResponseEntity<PedidoConcluidoPayload> realizaCompraRateLimiter(@RequestBody PedidoPayload pedidoPayload){
		RateLimiterConfig rateLimiterConfig = RateLimiterConfig.custom().limitForPeriod(1000).timeoutDuration(Duration.ofMillis(60)).build();
		RateLimiterRegistry registry = RateLimiterRegistry.of(rateLimiterConfig);
		RateLimiter rateLimiter = registry.rateLimiter("impostoRateLimiter");
		
		Supplier<Double> decorateSupplier = RateLimiter.decorateSupplier(rateLimiter, ()-> impostoService.getImposto(pedidoPayload));
		Double imposto = decorateSupplier.get();
		
		
		return ResponseEntity.ok(PedidoConcluidoPayload.builder().valorImposto(3D).uuid(UUID.randomUUID().toString()).build());

	}
	
	@PostMapping("/compra4")
	public ResponseEntity<PedidoConcluidoPayload> realizaCompraBulkhead(@RequestBody PedidoPayload pedidoPayload){
		BulkheadConfig bulkheadConfig = BulkheadConfig.custom().maxConcurrentCalls(10).maxWaitDuration(Duration.ofMillis(700)).build();
		BulkheadRegistry register = BulkheadRegistry.of(bulkheadConfig);
		Bulkhead bulkhead = register.bulkhead("impostoBH");
		Supplier<Double>  decorated = () -> impostoService.getImposto(pedidoPayload);
		Supplier<Double> supplier = Bulkhead.decorateSupplier(bulkhead, decorated);
		supplier.get();

		
		return ResponseEntity.ok(PedidoConcluidoPayload.builder().valorImposto(3D).uuid(UUID.randomUUID().toString()).build());

	}
	
		

}
