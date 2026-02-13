package com.gnn.newsnetwork.GnnNewsNetworkApplication.controller;

import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.DigitalNewsResponseDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.EditorResponseDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.PageResponse;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.dto.StoryNewsResponseDto;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.News;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.entity.Users;
import com.gnn.newsnetwork.GnnNewsNetworkApplication.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

// Editors Section
// Approve editor
    @PutMapping("/editors/{id}/approve")
    public ResponseEntity<String> approveEditor(@PathVariable Long id) {
        adminService.approveEditors(id);
        return ResponseEntity.ok("Editor approved successfully");
    }

// Reject editor
    @PutMapping("/editors/{id}/reject")
    public ResponseEntity<String> rejectEditor(@PathVariable Long id) {
        adminService.rejectEditor(id);
        return ResponseEntity.ok("Editor rejected successfully");
    }

// Get all editors
    @GetMapping("/editors")
    public ResponseEntity<List<EditorResponseDto>> getAllEditors() {
        return ResponseEntity.ok(adminService.getAllEditors());
    }

// Approved Editors in DTO
    @GetMapping("/editors/approved")
    public ResponseEntity<List<EditorResponseDto>> getApprovedEditors() {
        return ResponseEntity.ok(adminService.getApprovedEditorsDto());
    }

// Rejected / Pending Editors in DTO
    @GetMapping("/editors/rejected")
    public ResponseEntity<List<EditorResponseDto>> getRejectedEditors() {
        return ResponseEntity.ok(adminService.getRejectedEditorsDto());
    }

// Paginated Story News
// List of story news in DTO form
    @GetMapping("/story/pending")
    public ResponseEntity<PageResponse<StoryNewsResponseDto>> getAllStoryNews(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(adminService.getPendingStoryNewsDto(pageable));
    }

// List of digital news in DTO form
    @GetMapping("/digital/pending")
    public ResponseEntity<Page<DigitalNewsResponseDto>> getAllDigitalNews(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(adminService.getPendingDigitalNewsDto(pageable));
    }

// Approve news (Both story and digital news)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{newsId}/approve")
    public ResponseEntity<String> approveNews(@PathVariable Long newsId, @AuthenticationPrincipal Users admin) {
        adminService.approveNews(newsId, admin);
        return ResponseEntity.ok("News approved");
    }

// Reject news
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{newsId}/reject")
    public ResponseEntity<String> rejectNews(@PathVariable Long newsId, @AuthenticationPrincipal Users admin) {
        adminService.rejectNews(newsId, admin);
        return ResponseEntity.ok("News rejected");
    }

// List of approved Story news ( Optional for now because we have to return this in Homepage also )
    @GetMapping("/story/approved")
    public ResponseEntity<Page<StoryNewsResponseDto>> getApprovedStoryNews(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(adminService.getApprovedStoryNewsDto(pageable));
    }
}
