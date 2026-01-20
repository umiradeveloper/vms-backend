package org.sim.umira.entities;

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
@Table(name = "menu_mobile")
public class MenuMobileEntity extends PanacheEntityBase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_menu_mobile;

    public String kode_menu;

    public String menu_name;

    public String menu_path;

    public String menu_icon;

   

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_apps")
    public AppsEntity apps;
}
