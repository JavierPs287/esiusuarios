package edu.esi.ds.esiusuarios.http.dto;

public record LogoutRequest(String token, Long userId, String email) {
}