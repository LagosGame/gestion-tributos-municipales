package com.gestion_municipal.gestion_municipal.service;

import com.gestion_municipal.gestion_municipal.entidades.Recargo;
import com.gestion_municipal.gestion_municipal.entidades.Recibo;
import com.gestion_municipal.gestion_municipal.repository.RecargoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service//es un servicio//
public class RecargoService {

    private static final BigDecimal PORCENTAJE_EJECUTIVO = new BigDecimal("0.05");//constantes de porcentajjes/
    private static final BigDecimal PORCENTAJE_APREMIO_REDUCIDO = new BigDecimal("0.10");
    private static final BigDecimal PORCENTAJE_APREMIO_ORDINARIO = new BigDecimal("0.20");

    private static final BigDecimal TIPO_INTERES_DEMORA_ANUAL = new BigDecimal("0.040625"); //interes legal de demora -  4,0625% anual//
    private static final int PLAZO_DIAS_TRAS_APREMIO = 20;

    private final RecargoRepository recargoRepository;//para coger del repositorio los datos//

    public RecargoService(RecargoRepository recargoRepository){
        this.recargoRepository = recargoRepository;
    }

    public Recargo calcularRecargo(Recibo recibo, LocalDate fechaPagoReal){
        if (!fechaPagoReal.isAfter(recibo.getFechaVencimiento())){//si se paga antes de fecha no hay intereses//
            return null;
        }

        LocalDate fechaApremio = recibo.getFechaNotificacionApremio();//fecha (si ocurre) de notificacion de apremio//
        boolean apremioNotificado = fechaApremio != null && !fechaPagoReal.isBefore(fechaApremio);//fecha apremio no es null y fecha de pago no es anterior a la de apremio ( mismo dia o despues)//

        if (!apremioNotificado){//5%//
            return construirRecargo(recibo, Recargo.TipoRecargo.EJECUTIVO,
                    PORCENTAJE_EJECUTIVO,BigDecimal.ZERO, fechaPagoReal);
        }

        LocalDate finPlazoApremio = fechaApremio.plusDays(PLAZO_DIAS_TRAS_APREMIO);//apremio notificado - nueva fecha 20 dias despues//

        if (!fechaPagoReal.isAfter(finPlazoApremio)){//Si se paga antes del plazo de apremio 10%//
            return construirRecargo(recibo, Recargo.TipoRecargo.APREMIO_REDUCIDO,
                    PORCENTAJE_APREMIO_REDUCIDO,BigDecimal.ZERO, fechaPagoReal);
        }

        BigDecimal intereses = calcularInteresesDemora(recibo, fechaPagoReal);//si no 20% sumand intereses aparte//
        return construirRecargo(recibo, Recargo.TipoRecargo.APREMIO_ORDINARIO,
                PORCENTAJE_APREMIO_ORDINARIO, intereses, fechaPagoReal);
    }

    private BigDecimal calcularInteresesDemora(Recibo recibo, LocalDate fechaPagoReal){
        long diasRetraso = ChronoUnit.DAYS.between(recibo.getFechaVencimiento(), fechaPagoReal);//cuantops dias hay//
        BigDecimal interesDiario = TIPO_INTERES_DEMORA_ANUAL
                .divide(new BigDecimal("365"),10, RoundingMode.HALF_UP);//anujeal que es 4,0625 entre un año 365 dias, cogemos 10 decimales y redondeamos hacia arriba//
        return recibo.getImporte()//importe * intereses diarios * dias de retraso, en 2 decimales//
                .multiply(interesDiario)
                .multiply(BigDecimal.valueOf(diasRetraso))
                .setScale(2,RoundingMode.HALF_UP);
    }

    private Recargo construirRecargo(Recibo recibo, Recargo.TipoRecargo tipo,
                                     BigDecimal porcentaje, BigDecimal intereses,
                                     LocalDate fechaCalculo){
        BigDecimal importeRecargo = recibo.getImporte()//calcular importe
                .multiply(porcentaje)
                .setScale(2,RoundingMode.HALF_UP);

        Recargo recargo = Recargo.builder()//construir recargo//
                .tipo(tipo)
                .porcentaje(porcentaje.multiply(new BigDecimal("100")))// si tenemos 0.05 sube a 5 %//
                .importeCalculado(importeRecargo)
                .interesesDemora(intereses)
                .fechaCalculo(fechaCalculo)
                .recibo(recibo)
                .build();
        return recargoRepository.save(recargo);
    }

}
