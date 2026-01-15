package com.vcontrola.vcontrola.infra.security;

import com.vcontrola.vcontrola.repository.UsuarioRepository;
import com.vcontrola.vcontrola.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;

    @Autowired
    UsuarioRepository repository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Recupera o token do cabeçalho
        var token = this.recoverToken(request);

        // 2. Se o token existir, valida ele
        if (token != null) {
            var login = tokenService.getSubject(token);

            // 3. Se o token for válido (retornou um email)
            if (login != null && !login.isEmpty()) {
                // Busca o usuário no banco (simulando UserDetails)
                // Nota: O findByEmail retorna Optional, então usamos orElse(null) ou tratamos a exceção
                var usuario = repository.findByEmail(login).orElse(null);

                if (usuario != null) {
                    // 4. Cria o objeto de autenticação do Spring (Usuario + Permissões)
                    var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());

                    // 5. Salva no contexto (Diz pro Spring: "Esse cara está logado!")
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        // 6. Chama o próximo filtro (segue o fluxo da requisição)
        filterChain.doFilter(request, response);
    }

    // Método auxiliar para limpar o "Bearer " do cabeçalho
    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}
