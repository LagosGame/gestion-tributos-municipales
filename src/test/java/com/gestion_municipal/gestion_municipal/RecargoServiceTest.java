package com.gestion_municipal.gestion_municipal;


import com.gestion_municipal.gestion_municipal.entidades.Recargo;
import com.gestion_municipal.gestion_municipal.entidades.Recibo;
import com.gestion_municipal.gestion_municipal.entidades.TipoTributo;
import com.gestion_municipal.gestion_municipal.repository.RecargoRepository;
import com.gestion_municipal.gestion_municipal.service.RecargoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)//activa Mockito de JUnit5//
public class RecargoServiceTest {
    @Mock//repositorio falso//
    private RecargoRepository recargoRepository;

    @InjectMocks//lo mete al service real//
    private RecargoService recargoService;

    private Recibo recibo;//recibo de prueba//

    @BeforeEach//antes de cada test se ejecuta esto//
    void setUp() {
        recibo = Recibo.builder()
                .id(1L)
                .tipoTributo(TipoTributo.IBI)
                .importe(new BigDecimal("300.00"))
                .fechaEmision(LocalDate.of(2026, 3, 1))
                .fechaVencimiento(LocalDate.of(2026, 5, 1))
                .build();
        //repositorio, al guardar, devuelve lo mismo que le pasan//
        // lenient() porque no todos los tests llegan a usar este save, cuando no hay recargo no los usa//
        lenient().when(recargoRepository.save(any(Recargo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    @DisplayName("Sin recargo si se paga dentro del periodo voluntario")
    void sinRecargoEnPeriodoVoluntario() {
        Recargo recargo = recargoService.calcularRecargo(recibo, LocalDate.of(2026, 4, 15));
        assertThat(recargo).isNull();//no debe haber recargo//
    }

    @Test
    @DisplayName("Recargo ejecutivo del 5% si se paga tras vencer, antes de notificar apremio")
    void recargoEjecutivoDelCincoPorciento() {
        Recargo recargo = recargoService.calcularRecargo(recibo, LocalDate.of(2026, 5, 20));

        assertThat(recargo.getTipo()).isEqualTo(Recargo.TipoRecargo.EJECUTIVO);
        assertThat(recargo.getImporteCalculado()).isEqualByComparingTo("15.00");//5% de 300//
        assertThat(recargo.getInteresesDemora()).isEqualByComparingTo("0.00");//no lleva intereses//
    }

    @Test
    @DisplayName("Recargo de apremio reducido del 10% si se paga dentro de plazo tras el apremio")
    void recargoApremioReducidoDelDiezPorciento() {
        recibo.setFechaNotificacionApremio(LocalDate.of(2026, 6, 1));

        Recargo recargo = recargoService.calcularRecargo(recibo, LocalDate.of(2026, 6, 10));

        assertThat(recargo.getTipo()).isEqualTo(Recargo.TipoRecargo.APREMIO_REDUCIDO);
        assertThat(recargo.getImporteCalculado()).isEqualByComparingTo("30.00");//10% de 300//
        assertThat(recargo.getInteresesDemora()).isEqualByComparingTo("0.00");
    }

    @Test
    @DisplayName("Recargo de apremio ordinario del 20% + intereses si se paga fuera de plazo tras el apremio")
    void recargoApremioOrdinarioConIntereses() {
        recibo.setFechaNotificacionApremio(LocalDate.of(2026, 6, 1));//apremio notificado//
        //pagamos el 15 de julio, mucho despues del plazo de 20 dias//
        Recargo recargo = recargoService.calcularRecargo(recibo, LocalDate.of(2026, 7, 15));

        assertThat(recargo.getTipo()).isEqualTo(Recargo.TipoRecargo.APREMIO_ORDINARIO);
        assertThat(recargo.getImporteCalculado()).isEqualByComparingTo("60.00");//20% de 300//
        assertThat(recargo.getInteresesDemora()).isGreaterThan(BigDecimal.ZERO);//intereses//
    }
}
