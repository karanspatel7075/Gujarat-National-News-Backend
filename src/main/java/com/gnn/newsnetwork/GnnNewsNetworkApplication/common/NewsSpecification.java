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
                        cb.equal(root.get("state"), state);
    }

    public static Specification<News> hasCity(String city) {
        return (root, query, cb) ->
                city == null ? null :
                        cb.equal(root.get("city"), city);
    }

    public static Specification<News> hasCategory(String category) {
        return (root, query, cb) ->
                category == null ? null :
                        cb.equal(root.get("category"), category);
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
}
