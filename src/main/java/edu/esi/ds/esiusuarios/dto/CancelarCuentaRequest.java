package edu.esi.ds.esiusuarios.dto;

public record CancelarCuentaRequest(String token, Long userId, String email) {
}
