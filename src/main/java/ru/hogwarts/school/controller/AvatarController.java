package ru.hogwarts.school.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.service.AvatarService;
import java.util.Optional;

@RestController
@RequestMapping("/avatar")
public class AvatarController {
    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<Avatar> getAvatarByStudentId(@PathVariable Long studentId) {
        Optional<Avatar> avatar = avatarService.findAvatarByStudentId(studentId);
        return avatar.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public Page<Avatar> getAllAvatars(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return avatarService.getAllAvatars(page, size);
    }

    @PostMapping
    public Avatar createAvatar(@RequestBody Avatar avatar) {
        return avatarService.saveAvatar(avatar);
    }
}