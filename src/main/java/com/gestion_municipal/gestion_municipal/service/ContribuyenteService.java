package com.gestion_municipal.gestion_municipal.service;

import com.gestion_municipal.gestion_municipal.entidades.Contribuyente;
import com.gestion_municipal.gestion_municipal.exceptions.ResourceNotFoundException;
import com.gestion_municipal.gestion_municipal.repository.ContribuyenteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContribuyenteService {

    private final ContribuyenteRepository contribuyenteRepository;

    public ContribuyenteService(ContribuyenteRepository contribuyenteRepository) {
        this.contribuyenteRepository= contribuyenteRepository;
    }

    public List<Contribuyente> listarTodos(){
        return contribuyenteRepository.findAll();
    }
    public Contribuyente buscarPorId(Long id){
        return contribuyenteRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("Contribuyente no encontrado"));
    }
    public Contribuyente crear(Contribuyente contribuyente){
        contribuyenteRepository.findByNif(contribuyente.getNif()).ifPresent(c ->{//buscamos si el contribuyente ya existe
            throw new ResourceNotFoundException("Ya existe un contribuyente con NIF " + contribuyente.getNif());//si existe lanzamos una excepcion
        });
        return contribuyenteRepository.save(contribuyente);//guardamos el nuevo contribuyente
    }

    public Contribuyente actualizar(Long id, Contribuyente datos){
        Contribuyente existente = buscarPorId(id);//usamos el metodo de arriba//
        existente.setNombre(datos.getNombre());//cogemos a existente y le ponemos el nombre de datos
        existente.setApellidos(datos.getApellidos());
        existente.setDireccion(datos.getDireccion());
        existente.setEmail(datos.getEmail());
        existente.setTelefono(datos.getTelefono());
        return contribuyenteRepository.save(existente);//guardamos con datos nuevos

    }

    public void eliminar(Long id){
        Contribuyente existente = buscarPorId(id);//usamos metodo
        contribuyenteRepository.delete(existente);//boprramos
    }

}
