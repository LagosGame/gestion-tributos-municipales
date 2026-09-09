package com.gestion_municipal.gestion_municipal.entidades;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recibos")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Recibo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TipoTributo tipoTributo;

    @NotNull
    private Integer ejercicioFiscal; // al ser un año podemos poner null con integer con int no//

    @NotNull
    @Positive
    private BigDecimal importe;

    @NotNull
    private LocalDate fechaEmision;

    @NotNull
    private LocalDate fechaVencimiento;


    @Enumerated(EnumType.STRING) // Como esta el recibo//
    @Builder.Default // Sin esto sale null de base//
    private EstadoRecibo estado = EstadoRecibo.PENDIENTE; // con esto sale pendiente//

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PeriodoRecibo periodo = PeriodoRecibo.VOLUNTARIO;// Fase de pago//

    private LocalDate fechaNotificacionApremio; //recibo llega a fecha indicada de apremio//

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contribuyente_id", nullable = false) // recibos pertenecen a 1 contribuyente//
    private Contribuyente contribuyente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inmueble_id")// una multa no tiene inmueble por eso hay nullable//
    private Inmueble inmueble;

    @Builder.Default
    @OneToMany(mappedBy = "recibo", cascade = CascadeType.ALL)// un recibo puede tener varios pagos //
    @JsonManagedReference("recibo-pagos")// mostramos serializacion//
    private List<Pago> pagos = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "recibo", cascade = CascadeType.ALL)// un recibo puede acumular varios recargos//
    @JsonManagedReference("recibo-recargos")// mostramos serializacion//
    private List<Recargo> recargos = new ArrayList<>();

}
