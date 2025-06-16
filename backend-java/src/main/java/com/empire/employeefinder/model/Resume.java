package com.empire.employeefinder.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resumes")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class Resume {

    @Id
    @SequenceGenerator(name = "resumesIdSeq", sequenceName = "resume_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resumesIdSeq")
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "id")
    private Employee employee;

    @OneToOne(mappedBy = "resume", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private File file;
}
