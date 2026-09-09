package com.gestion_municipal.gestion_municipal.entidades;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity // Es una tabla//
@Table(name = "contribuyentes") // Como se llama//
@Data // Lombok para no escribir set, get etc...//
@NoArgsConstructor //Constructores vacios//
@AllArgsConstructor // COnstructores con campos//
@Builder // Crear objetos//
public class Contribuyente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID generado automaticamente de 0 que aumenta el valor  con cada nuevo contribuyente//
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false, length = 9) // DNI 00000000X 9 espacios, unicos no null//
    private String nif;

    @NotBlank
    private String nombre;

    @NotBlank
    private  String apellidos;

    private String direccion;

    @Email
    private String email;

    private String telefono;

    @Builder.Default // Sin esto nos da null la lista//
    @OneToMany(mappedBy = "contribuyente", cascade = CascadeType.ALL)// el Contrario al many to One, le decimos que ya esta mapeado en el campo de contribuyente de inmueble y el cascado hacemos que cada entidad guarde actualice o borre de manera conjunta//
    @JsonManagedReference("contribuyente-inmuebles") // lo mismo que lo anterior cuando serialices contribuyente dame lista de inmuebles pero cuando se serialice inmuebles no me des lista de contribuyentes//
    private List<Inmueble> inmuebles = new ArrayList<>(); // Lista de inmuebles en Array//

}
