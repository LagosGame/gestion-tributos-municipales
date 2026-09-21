package com.gestion_municipal.gestion_municipal.entidades;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pagos")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDate fechaPago;

    @NotNull
    @Positive
    private BigDecimal importePagado;

    @Enumerated(EnumType.STRING)
    private MetodoPago metodoPago; // tarjeta, trans, efectivo... etc.//

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recibo_id", nullable = false)//todos los pagos tienen recibos//
    @JsonBackReference("recibo-pagos")
    private Recibo recibo;

    public enum MetodoPago{
        DOMICILACION, TARJETA, TRANSFERENCIA, EFECTIVO
    }
}
