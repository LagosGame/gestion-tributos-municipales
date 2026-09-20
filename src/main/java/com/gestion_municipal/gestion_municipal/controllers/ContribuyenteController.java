package com.gestion_municipal.gestion_municipal.controllers;


import com.gestion_municipal.gestion_municipal.entidades.Contribuyente;
import com.gestion_municipal.gestion_municipal.service.ContribuyenteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contribuyentes")
public class ContribuyenteController {

    private final ContribuyenteService contribuyenteService;

    public ContribuyenteController(ContribuyenteService contribuyenteService){
        this.contribuyenteService= contribuyenteService;
    }

    @GetMapping
    public List<Contribuyente> listar(){//get todos los contribuyentes//
        return contribuyenteService.listarTodos();
    }
    @GetMapping("/{id}")//buscamos por id//
    public Contribuyente buscar(@PathVariable Long id){//buscamos ahora un contribuyente ( no lista) y ponemos la variable id//
        return contribuyenteService.buscarPorId(id);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)//cuando se crea//
    public Contribuyente crear(@Valid @RequestBody Contribuyente contribuyente) {//cogemos JSON del cuerpo de la peticion HTTP para crearlo y que tenga las validaciones ( @Email , @NotBlank etc//
        return contribuyenteService.crear(contribuyente);
    }

    @PutMapping("/{id}")
    public Contribuyente actualizar(@PathVariable Long id, @Valid @RequestBody Contribuyente contribuyente) {
        return contribuyenteService.actualizar(id, contribuyente);//lo mismo que lo anterior en buscar y en crear cogemos variable y las validaciones con el json para convertir en objeto//
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { // cogemos id para eliminar//
        contribuyenteService.eliminar(id);
        return ResponseEntity.noContent().build();//devuielve un 204 cuando no hay nada//
    }
}
