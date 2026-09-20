package com.gestion_municipal.gestion_municipal.service;


import com.gestion_municipal.gestion_municipal.entidades.Contribuyente;
import com.gestion_municipal.gestion_municipal.entidades.Inmueble;
import com.gestion_municipal.gestion_municipal.exceptions.ResourceNotFoundException;
import com.gestion_municipal.gestion_municipal.repository.ContribuyenteRepository;
import com.gestion_municipal.gestion_municipal.repository.InmuebleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InmuebleService {

    private final InmuebleRepository inmuebleRepository;
    private final ContribuyenteService contribuyenteService;

    public InmuebleService(InmuebleRepository inmuebleRepository, ContribuyenteService contribuyenteService){
        this.inmuebleRepository = inmuebleRepository;
        this.contribuyenteService = contribuyenteService;
    }//El vacio//

    public List<Inmueble> listarTodos(){
        return inmuebleRepository.findAll();
    }

    public Inmueble buscarPorId(Long id){
        return inmuebleRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Inmueble no encontrado"));
    }

    public List<Inmueble> listarPorContribuyente(Long contribuyenteId){//Lista por contribuyente//
        return inmuebleRepository.findByContribuyenteId(contribuyenteId);
    }
    public Inmueble crear(Long contribuyenteId, Inmueble inmueble){//creamos inmuble anexado al id de un contribuyente//
        Contribuyente contribuyente = contribuyenteService.buscarPorId(contribuyenteId);
        inmueble.setContribuyente(contribuyente);
        return inmuebleRepository.save(inmueble);
    }
    public void eliminar(Long id){
        Inmueble existente = buscarPorId(id);
        inmuebleRepository.delete(existente);
    }
}
