package unical.demacs.rdm.persistence.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long machineId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(columnDefinition = "TEXT DEFAULT 'DISPONIBILE'")
    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;



}
