package com.gestion_municipal.gestion_municipal.service;

import com.gestion_municipal.gestion_municipal.entidades.Contribuyente;
import com.gestion_municipal.gestion_municipal.entidades.EstadoRecibo;
import com.gestion_municipal.gestion_municipal.entidades.PeriodoRecibo;
import com.gestion_municipal.gestion_municipal.entidades.Recibo;
import com.gestion_municipal.gestion_municipal.exceptions.ResourceNotFoundException;
import com.gestion_municipal.gestion_municipal.repository.ReciboRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service

public class ReciboService {

    private final ReciboRepository reciboRepository;
    private final ContribuyenteService contribuyenteService;

    public ReciboService(ReciboRepository reciboRepository, ContribuyenteService contribuyenteService){
        this.reciboRepository = reciboRepository;
        this.contribuyenteService = contribuyenteService;
    }
    public List<Recibo> listarTodos() {
        return reciboRepository.findAll();
    }
    public Recibo buscarPorId(Long id) {
        return reciboRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recibo no encontrado: " + id));
    }
    public List<Recibo> listarPorContribuyente(Long contribuyenteId) { // estos 3 son los usuales//
        return reciboRepository.findByContribuyenteId(contribuyenteId);
    }

    public Recibo crear( Long contribuyenteId, Recibo recibo){
        Contribuyente contribuyente = contribuyenteService.buscarPorId(contribuyenteId);
        recibo.setContribuyente(contribuyente);
        recibo.setEstado(EstadoRecibo.PENDIENTE);
        recibo.setPeriodo(PeriodoRecibo.VOLUNTARIO);
        return reciboRepository.save(recibo);
    }//crear uno nuevo con los estado y preiodo por defecto//
    public List<Recibo> pasarVencidosAEjecutiva(LocalDate hoy){//Pasar de Estado todos los pendientes a Ejecucion//
        List<Recibo> vencidos = reciboRepository
                .findByEstadoAndFechaVencimientoBefore(EstadoRecibo.PENDIENTE, hoy);

        vencidos.forEach(recibo ->{//por cada uno que se cumpla lo de arriba, se les cambia estado y periodo//
            recibo.setEstado(EstadoRecibo.EN_EJECUTIVA);
            recibo.setPeriodo(PeriodoRecibo.EJECUTIVO);
        });

        return reciboRepository.saveAll(vencidos);

    }

    public void notificarApremio(Long reciboId, LocalDate fechaNotificacion) {
        Recibo recibo = buscarPorId(reciboId);
        if (recibo.getEstado() != EstadoRecibo.EN_EJECUTIVA) {
            throw new IllegalStateException("Solo se puede notificar apremio a recibos en periodo ejecutivo");
        }
        recibo.setFechaNotificacionApremio(fechaNotificacion);//manda notificacion de apremio a cualquier en ejecutivo//
        reciboRepository.save(recibo);
    }
    public void marcarComoPagado(Long reciboId) {
        Recibo recibo = buscarPorId(reciboId);
        recibo.setEstado(EstadoRecibo.PAGADO);//el marcado pagado en el recibo se guarda como tal//
        reciboRepository.save(recibo);
    }

}
