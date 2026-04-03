package com.example.mountainlog.repository;

import com.example.mountainlog.entity.ActivityDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityDetailRepository extends JpaRepository<ActivityDetail, Integer> {
}
