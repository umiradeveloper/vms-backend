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
@Table(name = "menu")
public class MenuEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_menu;

    public String code_menu;

    public String nama_menu;

    public String menu_title;

    public String icon_menu;

    public String type_menu;

    public String active_menu;

    public String selected_menu;

    public String dircange_menu;

    public String path_menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code_apps")
    public AppsEntity apps;
}
