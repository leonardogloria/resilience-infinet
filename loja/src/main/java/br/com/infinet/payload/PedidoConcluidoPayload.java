package br.com.infinet.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PedidoConcluidoPayload {
	private String uuid;
}
