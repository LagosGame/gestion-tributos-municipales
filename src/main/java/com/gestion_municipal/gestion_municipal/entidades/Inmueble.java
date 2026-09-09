package com.gestion_municipal.gestion_municipal.entidades;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "inmuebles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inmueble {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String referenciaCatastral;

    @NotBlank
    private String direccion;

    @NotNull
    @Positive
    private BigDecimal valorCatastral; // Big decimal para valor mas exacto (en dinero no hay que tener fallos)//

    @Enumerated(EnumType.STRING)
    private TipoInmueble tipo;

    @ManyToOne(fetch = FetchType.LAZY) // Muchos inmuebles a un contribuyente, ponemos lazy ya que no necesitamos a los contribuyentes, eager traeria todos //
    @JoinColumn(name = "contribuyente_id", nullable = false) // nueva columna en la tabla con id del contribuyente)
    @JsonBackReference("contribuyente_inmuebles") // Para no crear un bucle infinito al serailizar la lista de inmuebles con su contribuyente, cortamos al seralizar el inmueble)
    private Contribuyente contribuyente;
}
