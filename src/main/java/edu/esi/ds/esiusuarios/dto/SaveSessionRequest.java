package edu.esi.ds.esiusuarios.dto;

public record SaveSessionRequest(String token, Long userId, String email) {
}
