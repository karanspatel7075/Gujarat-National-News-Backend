package com.gnn.newsnetwork.GnnNewsNetworkApplication.repository;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.News;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.NewsStatus;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.TypeOfNews;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News> {
    //
    List<News> findByStatus(NewsStatus status);

    @EntityGraph(attributePaths = {"mediaList"})
    List<News> findByTypeOfNewsAndStatusOrderByCreatedAtDesc(TypeOfNews typeOfNews, NewsStatus status);

    @EntityGraph(attributePaths = {"mediaList"})
    Page<News> findByTypeOfNewsAndStatusOrderByCreatedAtDesc(TypeOfNews typeOfNews, NewsStatus status, Pageable pageable);

    List<News> findByStatusOrderByCreatedAtDesc(NewsStatus status);

    @Query("""
       SELECT DISTINCT n.category
       FROM News n
       WHERE n.status = com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.NewsStatus.PUBLISHED
       """)
    List<String> findDistinctPublishedCategories();

    @Query("""
    SELECT DISTINCT n FROM News n
    LEFT JOIN FETCH n.mediaList
    WHERE n.status = :status
    ORDER BY n.createdAt DESC
""")
    List<News> findPublishedWithMedia(@Param("status") NewsStatus status);

    @Query("""
    SELECT DISTINCT n
    FROM News n
    JOIN FETCH n.mediaList m
    WHERE n.status = :status
    AND m.mediaType = com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.MediaType.VIDEO""")
    Page<News> findPublishedVideoNews(@Param("status") NewsStatus status, Pageable pageable);

    // Filters only Published and returns unique categories
    // DISTINCT is used Because one news may have multiple videos → avoid duplicates.
}
