package com.gnn.newsnetwork.GnnNewsNetworkApplication.entity;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.AdPosition;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;  
import java.time.LocalDateTime;

@Entity
@Table(name = "advertisements")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String imageUrl;

    private String redirectUrl;

    @Enumerated(EnumType.STRING)
    private AdPosition position;
    // TOP_BANNER, RIGHT_TOP, RIGHT_BOTTOM

    private boolean active;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private LocalDateTime createdAt;
}
