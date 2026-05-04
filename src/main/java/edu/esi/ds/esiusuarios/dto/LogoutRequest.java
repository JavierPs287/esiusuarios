package edu.esi.ds.esiusuarios.dto;

public record LogoutRequest(String token, Long userId, String email) {
}