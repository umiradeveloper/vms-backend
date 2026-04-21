package org.sim.umira.entities.QualityControl;


import org.sim.umira.entities.CostControl.ProyekEntity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "qc_product")
public class QcProductEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_product;

    public String product_name;


    public String kode_product;


    public String product_category;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_status")
    @JsonBackReference
    public QcStatusEntity product_status;


    public String product_description;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_project")
    @JsonBackReference
    public ProyekEntity proyek;




    
}
