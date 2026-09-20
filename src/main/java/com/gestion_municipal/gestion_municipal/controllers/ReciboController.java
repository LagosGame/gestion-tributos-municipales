package com.gestion_municipal.gestion_municipal.controllers;

import com.gestion_municipal.gestion_municipal.entidades.Recibo;
import com.gestion_municipal.gestion_municipal.service.ReciboService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReciboController {
    private final ReciboService reciboService;

    public ReciboController(ReciboService reciboService) {
        this.reciboService = reciboService;
    }

    @GetMapping("/recibos")
    public List<Recibo> listar() {
        return reciboService.listarTodos();
    }

    @GetMapping("/recibos/{id}")
    public Recibo buscar(@PathVariable Long id) {
        return reciboService.buscarPorId(id);
    }

    @GetMapping("/contribuyentes/{contribuyenteId}/recibos")
    public List<Recibo> listarPorContribuyente(@PathVariable Long contribuyenteId) {
        return reciboService.listarPorContribuyente(contribuyenteId);
    }



    @PostMapping("/contribuyentes/{contribuyenteId}/recibos")
    @ResponseStatus(HttpStatus.CREATED)
    public Recibo crear(@PathVariable Long contribuyenteId, @Valid @RequestBody Recibo recibo) {
        return reciboService.crear(contribuyenteId, recibo);
    }

    //Todo esto ya lo hemos hecho//


    @PostMapping("/recibos/procesar-vencidos")
    public List<Recibo> procesarVencidos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return reciboService.pasarVencidosAEjecutiva(fecha != null ? fecha : LocalDate.now());
    }// en este post en request param required false significa que es opcional, si no se manda mandamos la feha de hoy
    //y el formato de es el de AAAA-MM-DD con el @DateTimeFOrmat//

    @PostMapping("/recibos/{id}/notificar-apremio")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void notificarApremio(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        reciboService.notificarApremio(id, fecha);
    }//Aqui postemos id y fecha para notificar apremio//

}
