package com.gestion_municipal.gestion_municipal.repository;

import com.gestion_municipal.gestion_municipal.entidades.EstadoRecibo;
import com.gestion_municipal.gestion_municipal.entidades.Recibo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReciboRepository extends JpaRepository<Recibo,Long> {
    List<Recibo> findByContribuyenteId(Long contribuyenteId);
    List<Recibo> findByEstado(EstadoRecibo estado);// filtra el enum//
    List<Recibo> findByEstadoAndFechaVencimientoBefore(EstadoRecibo estado, LocalDate fecha);//buscar cuando la fecha de vencimiento sea < que X y X estado//
}
