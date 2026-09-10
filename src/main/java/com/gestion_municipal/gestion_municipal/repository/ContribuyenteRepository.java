package com.gestion_municipal.gestion_municipal.repository;

import com.gestion_municipal.gestion_municipal.entidades.Contribuyente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContribuyenteRepository extends JpaRepository<Contribuyente,Long> {
    Optional<Contribuyente> findByNif (String nif); // busca por dni y optional por si no existe//
}
