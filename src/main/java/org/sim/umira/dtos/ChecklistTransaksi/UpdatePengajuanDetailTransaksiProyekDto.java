package org.sim.umira.dtos.ChecklistTransaksi;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;

public class UpdatePengajuanDetailTransaksiProyekDto {
      @FormParam("upload_dokumen_transaksi")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    @NotNull(message = "upload_bukti_bayar must be required")
    public FileUpload upload_dokumen_transaksi;


    // @FormParam("nilai_transaksi")
    // @PartType(MediaType.TEXT_PLAIN)
    // @NotNull(message = "nilai_transaksi must be required")
    // public Integer nilai_transaksi;
}
