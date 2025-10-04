package com.sistemarestaurante.todosimple.controllers;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.sistemarestaurante.todosimple.models.Comanda;
import com.sistemarestaurante.todosimple.services.ComandaService;


/* createComanda: OK com 201 Created, mas padronizar DTOs
Melhoria: evitar expor a entidade diretamente. Use ComandaRequest/ComandaResponse (DTOs) e mapeie no service.
Benefícios: contrato estável, controle de campos, segurança. */


/* getAll: paginação e filtros (escala e performance)
Smell: GET /todos sem paginação pode retornar listas grandes.
Sugestão: adotar Pageable e filtros via query string. */


/* PUT /{id}: status de retorno e semântica
Problema: PUT retornando 201 Created com Location geralmente não é esperado; PUT deve ser idempotente e retornar 204 No Content ou 200 OK com o recurso atualizado.
Sugestão: retornar 204 (ou 200 com body) e não recriar URI. */


/* Convenções de nomes (Java)
Observação: getId_comanda() foge ao padrão Java Beans.
Sugestão: renomear para getIdComanda()/setIdComanda() (e idComanda no campo). */


/* @Validated por grupos na entidade (acoplamento de camadas)
Smell: grupos de validação (CreateComanda, UpdateComanda) declarados na entidade acoplam domínio à camada web.
Sugestão: mover validações para DTOs (request), deixando a entidade mais limpa. */

@RestController
@RequestMapping("/comanda")
public class ComandaController {

    @Autowired
    private ComandaService comandaService;

    @GetMapping("/{id}")
    public ResponseEntity<Comanda> findById(@PathVariable Long id) {
        Comanda comanda = this.comandaService.findById(id);
        return ResponseEntity.ok().body(comanda);
    }

    @PostMapping
@Validated(Comanda.CreateComanda.class)
public ResponseEntity<Comanda> createComanda(@Valid @RequestBody Comanda comanda) {
    Comanda novaComanda = comandaService.createComanda(comanda);
    URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}").buildAndExpand(novaComanda.getId_comanda()).toUri();

    // Retorne a comanda criada para que o cliente possa ler os dados
    return ResponseEntity.created(uri).body(novaComanda);
}
     @GetMapping("/todos")
    public ResponseEntity<List<Comanda>> getAll() {
        List<Comanda> comanda = comandaService.getAll();
        return ResponseEntity.ok(comanda);
    }
    
    @PutMapping("/{id}")
    @Validated(Comanda.UpdateComanda.class)
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody Comanda comanda) {
        Comanda comandaAtualizada = this.comandaService.update(id, comanda);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(comandaAtualizada.getId_comanda()).toUri();
        return ResponseEntity.created(uri).build();
    }
  

}