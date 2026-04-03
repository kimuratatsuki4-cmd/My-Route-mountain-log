package com.example.mountainlog.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "mountain_notes")
@Data
public class MountainNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mountain_id")
    private Mountain mountain;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    @OneToMany(mappedBy = "mountainNote", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NoteItem> noteItems = new ArrayList<>();

    private LocalDateTime createdAt = LocalDateTime.now();
}
