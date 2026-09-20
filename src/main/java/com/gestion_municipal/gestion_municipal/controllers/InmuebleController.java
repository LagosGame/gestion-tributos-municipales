package com.gestion_municipal.gestion_municipal.controllers;

import com.gestion_municipal.gestion_municipal.entidades.Inmueble;
import com.gestion_municipal.gestion_municipal.service.InmuebleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class InmuebleController {
    private final InmuebleService inmuebleService;
    //Repetimos lo hecho en contribuyente a excepcion del listar por contribuyente//
    public InmuebleController(InmuebleService inmuebleService) {
        this.inmuebleService = inmuebleService;
    }
    @GetMapping("/inmuebles")
    public List<Inmueble> listar() {
        return inmuebleService.listarTodos();
    }

    @GetMapping("/inmuebles/{id}")
    public Inmueble buscar(@PathVariable Long id) {
        return inmuebleService.buscarPorId(id);
    }

    //en el get vamos a contribuyentes, de ahi a su id y de ahi a inmuebles para obtener los inmuebles en una lista por id de contribuyente//
    @GetMapping("/contribuyentes/{contribuyenteId}/inmuebles")
    public List<Inmueble> listarPorContribuyente(@PathVariable Long contribuyenteId) {
        return inmuebleService.listarPorContribuyente(contribuyenteId);
    }

    @PostMapping("/contribuyentes/{contribuyenteId}/inmuebles")
    @ResponseStatus(HttpStatus.CREATED)
    public Inmueble crear(@PathVariable Long contribuyenteId, @Valid @RequestBody Inmueble inmueble) {
        return inmuebleService.crear(contribuyenteId, inmueble);
    }

    @DeleteMapping("/inmuebles/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        inmuebleService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
