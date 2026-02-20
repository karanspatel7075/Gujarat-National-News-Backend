package com.gnn.newsnetwork.GnnNewsNetworkApplication.repository;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.Advertisement;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.AdPosition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
    List<Advertisement> findByActiveTrueAndPositionAndStartDateBeforeAndEndDateAfter(
            AdPosition position,
            LocalDateTime now1,
            LocalDateTime now2
    );
}
