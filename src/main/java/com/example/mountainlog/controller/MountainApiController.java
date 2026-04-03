package com.example.mountainlog.controller;

import com.example.mountainlog.entity.Mountain;
import com.example.mountainlog.service.MountainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/mountains")
public class MountainApiController {

    private final MountainService mountainService;

    public MountainApiController(MountainService mountainService) {
        this.mountainService = mountainService;
    }

    /**
     * 山をキーワードで検索する REST API
     * フロントエンドからのAjaxリクエストで使用される
     * 
     * GET /api/mountains/search?keyword=富士
     * → 山名・読みがな・都道府県に「富士」を含む山のリストを返す
     */
    @GetMapping("/search")
    public ResponseEntity<List<Mountain>> search(@RequestParam("keyword") String keyword) {
        List<Mountain> mountains = mountainService.searchMountains(keyword);
        return ResponseEntity.ok(mountains);
    }

    /**
     * IDで山の詳細情報を取得する REST API
     * 山選択時にフォームへ自動入力するために使用
     * 
     * GET /api/mountains/1
     * → mountain_id=1 の山情報を返す
     */
    @GetMapping("/{id}")
    public ResponseEntity<Mountain> getById(@PathVariable("id") Integer id) {
        Optional<Mountain> mountain = mountainService.findById(id);
        return mountain.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
