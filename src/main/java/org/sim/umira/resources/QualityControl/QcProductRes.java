package org.sim.umira.resources.QualityControl;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.sim.umira.dtos.QualityControl.QcProductDto;
import org.sim.umira.entities.CostControl.ProyekEntity;
import org.sim.umira.entities.QualityControl.QcProductEntity;
import org.sim.umira.entities.QualityControl.QcStatusEntity;
import org.sim.umira.handlers.ResponseHandler;
import org.sim.umira.jwt.Secured;

import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/QC-product")
@Secured
public class QcProductRes {

    @POST
    @Path("/create-product")
    public Response CreateProduct(@Valid @RequestBody QcProductDto qcProdukDto) {
        ProyekEntity proyek = ProyekEntity.findById(qcProdukDto.id_project);
        QcStatusEntity qcStatus = QcStatusEntity.findById(qcProdukDto.status_produk);

        try {
            QcProductEntity qcProduk = new QcProductEntity();
            qcProduk.proyek = proyek;
            qcProduk.kode_product = qcProdukDto.kode_produk;
            qcProduk.product_category = qcProdukDto.kategori_produk;
            qcProduk.product_description = qcProdukDto.deskripsi_produk;
            qcProduk.product_name = qcProdukDto.nama_produk;
            qcProduk.product_status = qcStatus;
            qcProduk.persist();
            
            
            return Response.ok().entity(ResponseHandler.ok("Create Product Berhasil", null)).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.ok().entity(ResponseHandler.error("Ada Kesalahan Server")).build();
            // TODO: handle exception
        }
        
    }
    
}
