package com.sistemarestaurante.todosimple.controllers;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.sistemarestaurante.todosimple.models.Despesa;
import com.sistemarestaurante.todosimple.services.DespesaService;


/* findById: retornar 404 quando não encontrado
Problema: se despesaService.findById(id) lançar exceção não mapeada, vira 500.
Sugestões:
findOptionalById(id) e mapear para 404, ou
manter exceção de domínio e tratá-la via @ControllerAdvice. */


/* Validação forte e tipos para valores monetários
Sugestões:
Em valores de dinheiro, prefira BigDecimal em vez de Double (evita erro de ponto flutuante).
Anotar campos no DTO: @PositiveOrZero, @Digits(integer=12, fraction=2), @NotBlank para descrição/categoria. */


/* Observabilidade
Sugestão: adicionar logs estruturados (nível, requestId, despesaId) nos pontos de criação/atualização/remoção; ajuda no suporte e auditoria. */


/* Limpeza de comentários e imports
Smell: comentário “Retorne a comanda criada…” é copy-paste. Ajustar para despesa.
Sugestão: revisar imports não usados ao final para evitar warnings. */


/* Segurança e CORS (se houver front separado)
Smell: comentário “Retorne a comanda criada…” é copy-paste. Ajustar para despesa.
Sugestão: revisar imports não usados ao final para evitar warnings. */

@RestController
@RequestMapping("/despesa")
public class DespesaController {

    @Autowired
    private DespesaService despesaService;

    @GetMapping("/{id}")
    public ResponseEntity<Despesa> findById(@PathVariable Long id) {
        Despesa despesa = this.despesaService.findById(id);
        return ResponseEntity.ok().body(despesa);
    }

    // Método GET para retornar todas as despesas
    @GetMapping
    public List<Despesa> getAllDespesas() {
        return despesaService.getAllDespesas();
    }

    @PostMapping
    @Validated(Despesa.CreateDespesa.class)
    public ResponseEntity<Despesa> createDespesa(@Valid @RequestBody Despesa despesa) {
        Despesa novaDespesa = despesaService.createDespesa(despesa);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(novaDespesa.getId_despesa()).toUri();

        // Retorne a comanda criada para que o cliente possa ler os dados
        return ResponseEntity.created(uri).body(novaDespesa);
    }

    @PutMapping("/{id}")
    @Validated(Despesa.UpdateDespesa.class)
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody Despesa despesa) {
        Despesa despesaAtualizada = this.despesaService.update(id, despesa);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(despesaAtualizada.getId_despesa()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        despesaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}