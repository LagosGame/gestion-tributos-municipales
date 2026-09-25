package com.gestion_municipal.gestion_municipal.controllers;

import com.gestion_municipal.gestion_municipal.entidades.Recargo;
import com.gestion_municipal.gestion_municipal.entidades.Recibo;
import com.gestion_municipal.gestion_municipal.service.ReciboService;
import com.gestion_municipal.gestion_municipal.service.RecargoService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ReciboController {
    private final ReciboService reciboService;
    private final RecargoService recargoService;

    public ReciboController(ReciboService reciboService, RecargoService recargoService) {
        this.reciboService = reciboService;
        this.recargoService = recargoService;
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

    // simula cuanto habria que pagar hoy (o en la fecha indicada) SIN guardar nada//
    @GetMapping("/recibos/{id}/simulacion-pago")
    public Map<String, Object> simularPago(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        Recibo recibo = reciboService.buscarPorId(id);
        LocalDate fechaSimulacion = fecha != null ? fecha : LocalDate.now();
        Recargo recargo = recargoService.simularRecargo(recibo, fechaSimulacion);

        BigDecimal total = recibo.getImporte();
        if (recargo != null) {
            total = total.add(recargo.getImporteCalculado()).add(recargo.getInteresesDemora());
        }

        Map<String, Object> resultado = new LinkedHashMap<>();
        resultado.put("importeOriginal", recibo.getImporte());
        resultado.put("recargo", recargo);
        resultado.put("totalAPagar", total);
        return resultado;
    }
}