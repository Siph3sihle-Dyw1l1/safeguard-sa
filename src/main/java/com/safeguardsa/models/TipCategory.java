/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.safeguardsa.models;

import jakarta.persistence.*;
/**
 *
 * @author ntsak
 */
@Entity
@Table(name = "tip_categories")
public class TipCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // CRIME, ASSAULT, THEFT, SUSPICIOUS, OTHER

    @Column(name = "pin_colour")
    private String pinColour; // red, darkred, orange, gold, grey

    @Column(length = 500)
    private String description;

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPinColour() { return pinColour; }
    public void setPinColour(String pinColour) { this.pinColour = pinColour; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
