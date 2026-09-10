package com.gestion_municipal.gestion_municipal.repository;

import com.gestion_municipal.gestion_municipal.entidades.Inmueble;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InmuebleRepository extends JpaRepository<Inmueble,Long> {
    Optional<Inmueble> findByReferenciaCatastral(String referenciaCatastral);
    List<Inmueble> findByContribuyenteId(Long contribuyenteId);//puede haber varios inmuebles por eso list//
}
