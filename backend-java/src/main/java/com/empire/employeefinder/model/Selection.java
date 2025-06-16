package com.empire.employeefinder.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@EqualsAndHashCode
@Table(name = "selections")
public class Selection {

    @Id
    @SequenceGenerator(name = "selectionsIdSeq", sequenceName = "selection_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "selectionsIdSeq")
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(name = "is_submitted")
    private boolean submitted = false;

    @ManyToMany
    @JoinTable(
            name = "chosen_candidates",
            joinColumns = @JoinColumn(name = "selection_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id"))
    private List<Employee> chosenCandidates = new ArrayList<>();

    public Selection(Company company) {
        this.company = company;
    }

    public void addEmployeeToChosenCandidates(Employee employee) {
        if (!chosenCandidates.contains(employee)) {
            chosenCandidates.add(employee);
        }
    }

    public void removeEmployeeFromChosenCandidates(Employee employee) {
        chosenCandidates.remove(employee);
    }
}
