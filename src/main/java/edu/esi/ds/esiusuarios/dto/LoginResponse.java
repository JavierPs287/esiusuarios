package edu.esi.ds.esiusuarios.dto;

public record LoginResponse(String token, Long userId, String email) {
}
