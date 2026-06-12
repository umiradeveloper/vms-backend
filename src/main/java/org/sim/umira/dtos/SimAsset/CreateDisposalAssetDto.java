package org.sim.umira.dtos.SimAsset;

import java.math.BigInteger;

import org.jboss.resteasy.reactive.PartType;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;

public class CreateDisposalAssetDto {

    @FormParam("id_asset")
    @PartType(MediaType.TEXT_PLAIN)
    public String id_asset;

    @FormParam("id_user_approval")
    @PartType(MediaType.TEXT_PLAIN)
    public String id_user_approval;

    @FormParam("alasan")
    @PartType(MediaType.TEXT_PLAIN)
    public String alasan;

    @FormParam("keterangan")
    @PartType(MediaType.TEXT_PLAIN)
    public String keterangan;

    @FormParam("nilai_sisa")
    @PartType(MediaType.TEXT_PLAIN)
    public BigInteger nilai_sisa;

    @FormParam("metode_penghapusan")
    @PartType(MediaType.TEXT_PLAIN)
    public String metode_penghapusan;

    @FormParam("status_disposal")
    @PartType(MediaType.TEXT_PLAIN)
    public String status_disposal;
}
