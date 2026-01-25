package com.vcontrola.vcontrola.controller;

import com.vcontrola.vcontrola.controller.response.ResumoDashBoardResponse;
import com.vcontrola.vcontrola.entity.Usuario;
import com.vcontrola.vcontrola.enums.TipoTransacao;
import com.vcontrola.vcontrola.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@RestController
@RequestMapping("/dashboard")
public class DashBoardController {
    @Autowired
    private TransacaoRepository transacaoRepository;

    @GetMapping("/resumo-mensal")
    public ResponseEntity<ResumoDashBoardResponse> getResumoMensal(Authentication auth) {
        Usuario usuario = (Usuario) auth.getPrincipal();


        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = hoje.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate fimMes = hoje.with(TemporalAdjusters.lastDayOfMonth());

        BigDecimal totalReceitas = transacaoRepository.somarPorTipo(
                usuario.getId(),
                TipoTransacao.RECEITAS,
                inicioMes,
                fimMes
        );


        BigDecimal totalDespesas = transacaoRepository.somarPorTipo(
                usuario.getId(),
                TipoTransacao.GASTOS,
                inicioMes,
                fimMes
        );

        BigDecimal saldoMes = totalReceitas.subtract(totalDespesas);

        return ResponseEntity.ok(new ResumoDashBoardResponse(totalReceitas, totalDespesas, saldoMes));
    }
}
