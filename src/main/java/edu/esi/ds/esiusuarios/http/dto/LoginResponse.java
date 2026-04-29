package edu.esi.ds.esiusuarios.http.dto;

public record LoginResponse(String token, Long userId, String email) {
}
