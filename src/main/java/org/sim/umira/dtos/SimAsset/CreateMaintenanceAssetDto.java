package org.sim.umira.dtos.SimAsset;

import java.math.BigInteger;
import java.time.LocalDate;

import org.jboss.resteasy.reactive.PartType;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;

public class CreateMaintenanceAssetDto {
     @FormParam("id_maintenance_asset")
    @PartType(MediaType.TEXT_PLAIN)
    public String id_maintenance_asset;

    @FormParam("tanggal_maintenance")
    @PartType(MediaType.TEXT_PLAIN)
    public LocalDate tanggal_maintenance;

    @FormParam("tipe_maintenance")
    @PartType(MediaType.TEXT_PLAIN)
    public String tipe_maintenance;

    @FormParam("tanggal_selesai")
    @PartType(MediaType.TEXT_PLAIN)
    public LocalDate tanggal_selesai;

    @FormParam("biaya")
    @PartType(MediaType.TEXT_PLAIN)
    public BigInteger biaya;

    @FormParam("deskripsi")
    @PartType(MediaType.TEXT_PLAIN)
    public String deskripsi;

    @FormParam("kondisi_setelah")
    @PartType(MediaType.TEXT_PLAIN)
    public String kondisi_setelah;

    @FormParam("status_maintenance")
    @PartType(MediaType.TEXT_PLAIN)
    public String status_maintenance;

    @FormParam("id_asset")
    @PartType(MediaType.TEXT_PLAIN)
    public String id_asset;
}
