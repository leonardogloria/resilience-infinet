package br.com.infinet.payload;

import lombok.Data;

@Data
public class PedidoPayload {
	private Double valor;
	private Long cliente;

}
