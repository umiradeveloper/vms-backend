package org.sim.umira.entities;



import java.time.LocalDateTime;


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
@Table(name = "users")
public class UserEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_user;
    // public String id_branch;
    // public String id_role;
    public String nama;
    public String nip;
    public String username;
    public String password;
    public String email;
    public String no_hp;
    public Integer isApproval;
    public String approvedBy;
    public LocalDateTime approvedAt;
    public String catatan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_branch")
    public BranchEntity branch;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_role")
    public RoleEntity role;
    
}
