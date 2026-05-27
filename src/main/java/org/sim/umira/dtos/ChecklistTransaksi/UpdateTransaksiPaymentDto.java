package org.sim.umira.dtos.ChecklistTransaksi;

import java.math.BigInteger;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;

public class UpdateTransaksiPaymentDto {

    @FormParam("nilai_bayar")
    @PartType(MediaType.TEXT_PLAIN)
    public BigInteger nilai_bayar;

    @FormParam("upload_dokumen_bukti_bayar")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    // @NotNull(message = "upload_dokumen_bukti_bayar must be required")
    public FileUpload upload_dokumen_transaksi;
}
