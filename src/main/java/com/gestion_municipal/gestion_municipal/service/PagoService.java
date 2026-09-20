package com.gestion_municipal.gestion_municipal.service;

import com.gestion_municipal.gestion_municipal.entidades.EstadoRecibo;
import com.gestion_municipal.gestion_municipal.entidades.Pago;
import com.gestion_municipal.gestion_municipal.entidades.Recargo;
import com.gestion_municipal.gestion_municipal.entidades.Recibo;
import com.gestion_municipal.gestion_municipal.exceptions.ResourceNotFoundException;
import com.gestion_municipal.gestion_municipal.repository.PagoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;

    private final ReciboService reciboService;

    private final RecargoService recargoService;

    public PagoService(PagoRepository pagoRepository, ReciboService reciboService, RecargoService recargoService) {
        this.pagoRepository = pagoRepository;
        this.reciboService = reciboService;
        this.recargoService = recargoService;
    }

    @Transactional // si salta algun error lanzamos un rollback//
    public Pago registrarPago(Long reciboId, Pago datosPago) {
        Recibo recibo = reciboService.buscarPorId(reciboId);//buscamos el id//

        if (recibo.getEstado() == EstadoRecibo.PAGADO) {//si esta martcado como pagado salta el exception//
            throw new IllegalStateException("El recibo " + reciboId + " ya esta pagado");
        }

        Recargo recargo = recargoService.calcularRecargo(recibo, datosPago.getFechaPago());//calculamos el recargo

        BigDecimal totalEsperado = recibo.getImporte();// consegimos el importe y el total se convierte en el total mas los intereses//
        if (recargo != null) {
            totalEsperado = totalEsperado
                    .add(recargo.getImporteCalculado())
                    .add(recargo.getInteresesDemora());
        }

        if (datosPago.getImportePagado().compareTo(totalEsperado) < 0) {//Si es negativo, salta error//
            throw new IllegalStateException(
                    "El importe pagado (" + datosPago.getImportePagado() +
                            ") es inferior a la deuda total incluyendo recargos (" + totalEsperado + ")");
        }

        datosPago.setRecibo(recibo);//sacamos recibo//
        Pago pagoGuardado = pagoRepository.save(datosPago);//guardamos el pago//

        reciboService.marcarComoPagado(reciboId);// y marcamos como pagado//

        return pagoGuardado;
    }

    public Pago buscarPorId(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado: " + id));
    }
}
