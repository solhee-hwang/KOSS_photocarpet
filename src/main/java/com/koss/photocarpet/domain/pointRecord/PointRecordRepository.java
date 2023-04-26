package com.koss.photocarpet.domain.pointRecord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointRecordRepository extends JpaRepository<PointRecord,Long> {
}
