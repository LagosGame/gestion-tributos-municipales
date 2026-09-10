package com.gestion_municipal.gestion_municipal.repository;

import com.gestion_municipal.gestion_municipal.entidades.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago,Long> {
    List<Pago> findByReciboId(Long reciboId);
}
