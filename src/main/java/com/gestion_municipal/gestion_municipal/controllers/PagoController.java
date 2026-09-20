package com.gestion_municipal.gestion_municipal.controllers;

import com.gestion_municipal.gestion_municipal.entidades.Pago;
import com.gestion_municipal.gestion_municipal.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recibos/{reciboId}/pagos")//Esta dentro de los recibos//
public class PagoController {
    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)//solamente el registrarlo quie le pedimos ids y validaciones//
    public Pago registrarPago(@PathVariable Long reciboId, @Valid @RequestBody Pago pago) {
        return pagoService.registrarPago(reciboId, pago);
    }
}
