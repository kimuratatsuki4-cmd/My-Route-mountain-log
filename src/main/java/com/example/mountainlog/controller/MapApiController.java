package com.example.mountainlog.controller;

import com.example.mountainlog.dto.MountainMapDto;
import com.example.mountainlog.entity.User;
import com.example.mountainlog.service.MountainService;
import com.example.mountainlog.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/map")
public class MapApiController {

    private final MountainService mountainService;
    private final UserService userService;

    public MapApiController(MountainService mountainService, UserService userService) {
        this.mountainService = mountainService;
        this.userService = userService;
    }

    @GetMapping("/mountains")
    public ResponseEntity<List<MountainMapDto>> getMountains(@AuthenticationPrincipal UserDetails userDetails) {
        User user = null;
        if (userDetails != null) {
            Optional<User> userOpt = userService.findUserByEmail(userDetails.getUsername());
            if (userOpt.isPresent()) {
                user = userOpt.get();
            }
        }

        List<MountainMapDto> data = mountainService.getMountainMapData(user);
        return ResponseEntity.ok(data);
    }
}
