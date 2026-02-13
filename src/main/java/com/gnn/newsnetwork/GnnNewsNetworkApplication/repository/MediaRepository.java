package com.gnn.newsnetwork.GnnNewsNetworkApplication.repository;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media, Long> {
    //
    List<Media> findByNewsId(Long id);
}
