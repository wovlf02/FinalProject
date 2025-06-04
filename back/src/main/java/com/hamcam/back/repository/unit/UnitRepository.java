package com.hamcam.back.repository.unit;

import com.hamcam.back.entity.unit.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    // subject별 단원명만 리스트로 반환
    @Query("SELECT u.name FROM Unit u WHERE u.subject = :subject")
    List<String> findNamesBySubject(String subject);
}
