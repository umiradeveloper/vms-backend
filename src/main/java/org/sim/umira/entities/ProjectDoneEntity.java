package org.sim.umira.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "project_done")
public class ProjectDoneEntity extends PanacheEntityBase {
     @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_project_done;

    public String nama_project;

    public String deskripsi_project;

    public String url_gambar;
}
