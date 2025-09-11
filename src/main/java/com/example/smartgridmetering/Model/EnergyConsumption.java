package com.example.smartgridmetering.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "energy_consumption_info")
public class EnergyConsumption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime datetime;  // hourly timestamp

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal energyConsumed;

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getDatetime() { return datetime; }
    public void setDatetime(LocalDateTime datetime) { this.datetime = datetime; }

    public BigDecimal getEnergyConsumed() { return energyConsumed; }
    public void setEnergyConsumed(BigDecimal energyConsumed) { this.energyConsumed = energyConsumed; }
}

