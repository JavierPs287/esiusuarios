package edu.esi.ds.esiusuarios.http.dto;

public record SaveSessionRequest(String token, Long userId, String email) {
}
