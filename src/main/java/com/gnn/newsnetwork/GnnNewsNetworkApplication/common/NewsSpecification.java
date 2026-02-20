package com.gnn.newsnetwork.GnnNewsNetworkApplication.common;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.News;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.NewsStatus;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.enums.TypeOfNews;
import org.springframework.data.jpa.domain.Specification;

public class NewsSpecification {

    // If category = null → ignored
    // If category = "Sports" → adds condition

    public static Specification<News> hasState(String state) {
        return (root, query, cb) ->
                state == null ? null :
                        cb.equal(cb.lower(root.get("state")), state.toLowerCase());
    }

    public static Specification<News> hasCity(String city) {
        return (root, query, cb) ->
                city == null ? null :
                        cb.equal(cb.lower(root.get("city")), city.toLowerCase());
    }

    public static Specification<News> hasCategory(String category) {
        return (root, query, cb) ->
                category == null ? null :
                        cb.equal(cb.lower(root.get("category")), category.toLowerCase());
    }

    public static Specification<News> hasType(TypeOfNews type) {
        return (root, query, cb) ->
                type == null ? null :
                        cb.equal(root.get("typeOfNews"), type);
    }

    public static Specification<News> isApproved() {
        return (root, query, cb) ->
                cb.equal(root.get("status"), NewsStatus.PUBLISHED);
    }

    public static Specification<News> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;

            String likePattern = "%" + keyword.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("title").as(String.class)), likePattern),
                    cb.like(cb.lower(root.get("shortDescription").as(String.class)), likePattern),
                    cb.like(cb.lower(root.get("category").as(String.class)), likePattern),
                    cb.like(cb.lower(root.get("city").as(String.class)), likePattern),
                    cb.like(cb.lower(root.get("state").as(String.class)), likePattern)
            );
        };
    }


}
