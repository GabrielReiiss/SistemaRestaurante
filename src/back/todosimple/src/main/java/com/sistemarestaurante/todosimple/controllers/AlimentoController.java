package com.sistemarestaurante.todosimple.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import com.sistemarestaurante.todosimple.services.AlimentoService;
import com.sistemarestaurante.todosimple.models.Alimento;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/alimento")
@Validated


/* Injeção de dependência (boas práticas/testabilidade)
 Sugestão: trocar field injection (@Autowired no atributo) por constructor injection.
Benefícios: imutabilidade, melhor suporte a testes, evita null em cenários de proxy, favorece DI explícita. */


/* RESTfulness e consistência de rotas
Observação: a base path está singular (/alimento). Em APIs REST, convenciona-se pluralizar recursos.
Sugestão: alterar para /alimentos e padronizar os verbos/URLs:

GET /alimentos/{id} (por id)
GET /alimentos?nome=... (busca por nome via @RequestParam)
POST /alimentos (criar)
PUT /alimentos/{id} (atualizar recurso completo)
PATCH /alimentos/{id}/quantidade (parcial/específico)
DELETE /alimentos/{id} (remover)
GET /alimentos (listar todos) */

/* Tratamento de Optional (evitar NoSuchElementException)
Problema: alimentoService.findByNome(nome) usa alimento.get() sem verificar presença → pode lançar exceção 500.
Sugestão: retornar 404 quando não encontrado. */


/* Semântica de PUT por nome (code smell: identificação frágil)
Smell: usar nome como identificador em PUT/DELETE torna a API frágil (nomes não são estáveis/únicos).
Sugestão: usar ID no path e validar unicidade do nome na camada de regra. Se mantiver por nome, documentar que é único/imutável. */


/* Endpoint de atualização de quantidade (usar PATCH e DTO)
Melhoria: para atualização parcial de um campo, prefira PATCH e um DTO explícito em vez de Map.
Benefícios: validação forte, schema claro, menos erros de chave ausente. */


public class AlimentoController {  
    @Autowired
    private AlimentoService alimentoService;

    @GetMapping("/id/{id_ingrediente}")
    public ResponseEntity<Alimento> findById(@PathVariable Integer id_ingrediente) { 
        Alimento alimento = this.alimentoService.findById(id_ingrediente);
        return ResponseEntity.ok().body(alimento);
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<Alimento> findByNome(@PathVariable String nome) {
        Optional<Alimento> alimento = alimentoService.findByNome(nome);
        return ResponseEntity.ok(alimento.get());
    }

    @PostMapping
    @Validated(Alimento.CreateAlimento.class)
    public ResponseEntity<Void> create(@Valid @RequestBody Alimento alimento) {
        this.alimentoService.create(alimento);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(alimento.getId_ingrediente()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{nome}")
    @Validated(Alimento.UpdateAlimento.class)
    public ResponseEntity<Void> update(@Valid @RequestBody Alimento alimento, @PathVariable String nome) {
        alimentoService.update(alimento, nome);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/quantidade/{nome}")
public ResponseEntity<Void> update(@RequestBody Map<String, Integer> quantidadeMap, @PathVariable String nome) {
    int quantidade = quantidadeMap.get("quantidade");
    alimentoService.updateQuantidade(nome, quantidade);
    return ResponseEntity.noContent().build();
}

    @DeleteMapping("/{nome}")
    public ResponseEntity<Void> delete(@PathVariable String nome) {
        this.alimentoService.delete(nome);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/varios")
    @Validated(Alimento.CreateAlimento.class)
    public ResponseEntity<Void> createMultiple(@Valid @RequestBody List<Alimento> alimentos) {
        for (Alimento alimento : alimentos) {
            this.alimentoService.create(alimento);
        }
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
        return ResponseEntity.created(uri).build();
    }

    @GetMapping("/todos")
    public ResponseEntity<List<Alimento>> getAll() {
        List<Alimento> alimentos = alimentoService.getAll();
        return ResponseEntity.ok(alimentos);
    }

}
