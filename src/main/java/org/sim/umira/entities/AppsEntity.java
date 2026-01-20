package org.sim.umira.entities;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "apps")
public class AppsEntity extends PanacheEntityBase{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_apps;

    public String code_apps;

    public String apps_name;
}
