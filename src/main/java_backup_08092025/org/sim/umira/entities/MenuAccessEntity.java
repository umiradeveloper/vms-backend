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
@Table(name = "menu_access")
public class MenuAccessEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_menu_access;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code_apps")
    public AppsEntity apps;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code_menu")
    public MenuEntity menu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_role")
    public RoleEntity role;

    


}
