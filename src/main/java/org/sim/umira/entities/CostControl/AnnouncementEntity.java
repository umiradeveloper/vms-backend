package org.sim.umira.entities.CostControl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
@Table(name = "cc_announcement")
public class AnnouncementEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    public String id_announcement;

    @Column(nullable = false, length = 255)
    public String judulAnnouncement;

    @Column(nullable = false, columnDefinition = "TEXT")
    public String isiAnnouncement;

    @Column(name = "dokumen_path")
    public String dokumenPath;
}
