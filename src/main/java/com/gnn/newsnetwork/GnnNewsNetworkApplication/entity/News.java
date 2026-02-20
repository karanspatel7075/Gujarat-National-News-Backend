package com.gnn.newsnetwork.GnnNewsNetworkApplication.entity;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.NewsStatus;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.TypeOfNews;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Id;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "news",
        indexes = {
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_category", columnList = "category"),
                @Index(name = "idx_city", columnList = "city"),
                @Index(name = "idx_state", columnList = "state"),
                @Index(name = "idx_createdAt", columnList = "createdAt")
        }
)
public class News implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    @Column(name = "short_description", columnDefinition = "LONGTEXT")
    private String shortDescription;

    @Lob
    @Column(name = "full_context", columnDefinition = "LONGTEXT")
    private String fullContext;

    @Enumerated(EnumType.STRING)
    private TypeOfNews typeOfNews; // Story & Digital
    private String category;

    private String anchorName; // ONLY for DIGITAL news

    @Enumerated(EnumType.STRING)
    private NewsStatus status;

    @ManyToOne
    private Users createdBy;

    @ManyToOne
    private Users approvedBy;

    private LocalDateTime publishedAt;

    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Media> mediaList;
    // Never change to EAGER.
    // Why?  = EAGER loads media everywhere / Slows down app / Bad for scaling

    private String state;   // Gujarat, Maharashtra

    private String city;    // Ahmedabad, Surat

    private String finalVideoUrl;
    private String videoAbsolutePath;
    private String processingStatus; // PENDING, PROCESSING, COMPLETED, FAILED

    private static final long serialVersionUID = 1L;
    
}
