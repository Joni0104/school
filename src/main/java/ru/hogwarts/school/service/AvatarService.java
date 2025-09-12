package ru.hogwarts.school.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.repository.AvatarRepository;
import java.util.Optional;

@Service
public class AvatarService {
    private final AvatarRepository avatarRepository;

    public AvatarService(AvatarRepository avatarRepository) {
        this.avatarRepository = avatarRepository;
    }

    public Optional<Avatar> findAvatarByStudentId(Long studentId) {
        return avatarRepository.findByStudentId(studentId);
    }

    public Page<Avatar> getAllAvatars(Integer page, Integer size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return avatarRepository.findAll(pageRequest);
    }

    public Avatar saveAvatar(Avatar avatar) {
        return avatarRepository.save(avatar);
    }
}