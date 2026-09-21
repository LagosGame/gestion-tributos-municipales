package com.gestion_municipal.gestion_municipal.entidades;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "recargos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoRecargo tipo;// % de tramo 5,10 o 20//
    private BigDecimal porcentaje;//%aplicado//
    private BigDecimal importeCalculado; // original + porcentaje//
    private BigDecimal interesesDemora; // solo cuando el pocentaje es de 20//
    private LocalDate fechaCalculo; // fecha en que se calculó//

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recibo_id", nullable = false)
    @JsonBackReference("recibo-recargos")
    private Recibo recibo;

    public enum TipoRecargo{
        EJECUTIVO,APREMIO_REDUCIDO,APREMIO_ORDINARIO
    }
}
