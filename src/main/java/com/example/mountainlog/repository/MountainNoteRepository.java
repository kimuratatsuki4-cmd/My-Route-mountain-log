package com.example.mountainlog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mountainlog.entity.MountainNote;

@Repository
public interface MountainNoteRepository extends JpaRepository<MountainNote, Long> {
    List<MountainNote> findByMountain_MountainIdOrderByCreatedAtDesc(Integer mountainId);
}
