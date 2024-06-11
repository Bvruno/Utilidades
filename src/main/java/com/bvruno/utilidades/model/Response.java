package com.bvruno.utilidades.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "response")
@Getter
@Setter
public class Response  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entidad")
    private String entidad;

    @Column(name = "metodo_de_pago")
    private String metodoDePago;

    @Column(name = "billetera")
    private String billetera;

    @Column(name = "monto")
    private BigDecimal monto;

    @Column(name = "fecha_string")
    private String fechaString;

    @Column(name = "destinatario")
    private String destinatario;

    @Column(name = "codigo_operacion")
    private String codigoOperacion;

    @Column(name = "enviado_por")
    private String enviadoPor;

    @Column(name = "img_base64", length = 255000)
    private String imgBase64;

    @Column(name = "error")
    private Boolean error;

    private Boolean imgReviewed;
    private String state;
}
