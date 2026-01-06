package Bflow.auth.DTO;

public record AuthRequest(
        String email,
        String password
) {}
