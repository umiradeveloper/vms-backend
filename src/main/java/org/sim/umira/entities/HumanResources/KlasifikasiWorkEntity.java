package org.sim.umira.entities.HumanResources;




import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "hr_klasifikasi_works")
public class KlasifikasiWorkEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_klasifikasi_works;

    public String klasifikasi_works;

    public String nama_klasifikasi_works;

    public Integer is_shift;

    public String jam_masuk;

    public Integer is_office;

    public Integer is_jadwal;

    public Integer is_saturday;


    public String jam_keluar;

    
}
