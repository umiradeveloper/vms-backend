package org.sim.umira.repositories;

import org.sim.umira.entities.RoleEntity;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RoleRpository implements PanacheRepository<RoleEntity> {
    
}
