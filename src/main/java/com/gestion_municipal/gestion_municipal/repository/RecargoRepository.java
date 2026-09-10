package com.gestion_municipal.gestion_municipal.repository;

import com.gestion_municipal.gestion_municipal.entidades.Recargo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecargoRepository extends JpaRepository<Recargo,Long> {
    List<Recargo> findByReciboId(Long reciboId);
}
