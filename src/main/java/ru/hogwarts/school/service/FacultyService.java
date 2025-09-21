package ru.hogwarts.school.service;

import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FacultyService {
    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public Optional<Faculty> findFaculty(Long id) {
        return facultyRepository.findById(id);
    }

    public Optional<Faculty> editFaculty(Long id, Faculty faculty) {
        return facultyRepository.findById(id)
                .map(existingFaculty -> {
                    existingFaculty.setName(faculty.getName());
                    existingFaculty.setColor(faculty.getColor());
                    return facultyRepository.save(existingFaculty);
                });
    }

    public boolean deleteFaculty(Long id) {
        if (facultyRepository.existsById(id)) {
            facultyRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Faculty> getFacultiesByColor(String color) {
        return facultyRepository.findByColor(color);
    }

    public List<Faculty> getFacultiesByNameOrColor(String searchString) {
        return facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(searchString, searchString);
    }

    public String getLongestFacultyName() {
        return facultyRepository.findAll().stream()
                .map(Faculty::getName)
                .filter(name -> name != null && !name.isEmpty())
                .max((name1, name2) -> Integer.compare(name1.length(), name2.length()))
                .orElse("");
    }
}