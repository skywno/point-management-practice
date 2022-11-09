package me.ezra.pm.point;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface PointCustomRepository {

    Page<ExpiredPointSummary> sumByExpiredDate(LocalDate alarmCriteriaDate, Pageable pageable);

//    Page<ExpiredPointSummary> sumBeforeExpireDate(LocalDate alarmCriteriaDate, Pageable pageable);

    Page<Point> findPointToExpire(LocalDate today, Pageable pageable);

}
