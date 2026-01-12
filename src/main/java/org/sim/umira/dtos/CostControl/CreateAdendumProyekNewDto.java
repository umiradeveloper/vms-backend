package org.sim.umira.dtos.CostControl;

import java.math.BigInteger;

import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.ws.rs.FormParam;

public class CreateAdendumProyekNewDto {
     @FormParam("id_proyek")
    public String id_proyek;

    @FormParam("id_adendum")
    public String id_adendum;

    @FormParam("nomor_adendum")
    public String nomor_adendum;

    @FormParam("kerja_tambah")
    public BigInteger kerja_tambah;

    @FormParam("kerja_kurang")
    public BigInteger kerja_kurang;

    @FormParam("dokumen_upload")
    public FileUpload dokumen_adendum;
}
