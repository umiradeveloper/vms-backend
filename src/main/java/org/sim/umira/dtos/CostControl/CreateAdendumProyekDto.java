package org.sim.umira.dtos.CostControl;

import java.math.BigInteger;
import java.util.List;

import org.jboss.resteasy.reactive.multipart.FileUpload;

import jakarta.ws.rs.FormParam;

public class CreateAdendumProyekDto {
    @FormParam("id_proyek")
    public String id_proyek;

    @FormParam("nomor_adendum")
    public List<String> nomor_adendum;

    @FormParam("dokumen_adendum")
    public List<FileUpload> dokumen_adendum;

    @FormParam("kerja_tambah")
    public List<BigInteger> kerja_tambah;

    @FormParam("kerja_kurang")
    public List<BigInteger> kerja_kurang;
}
