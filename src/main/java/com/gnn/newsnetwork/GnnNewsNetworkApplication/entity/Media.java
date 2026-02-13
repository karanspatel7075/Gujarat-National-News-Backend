package com.gnn.newsnetwork.GnnNewsNetworkApplication.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.MediaType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "media")
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private News news;

    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    private String mediaUrl;

    private String thumbnailUrl;
}

//  News = data
//  Media = files
