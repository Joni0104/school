package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FacultyService {
    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);
    private final FacultyRepository facultyRepository;

    public FacultyService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    public Faculty createFaculty(Faculty faculty) {
        logger.info("Was invoked method for create faculty");
        logger.debug("Creating faculty: {}", faculty);
        return facultyRepository.save(faculty);
    }

    public Optional<Faculty> findFaculty(Long id) {
        logger.info("Was invoked method for find faculty by id: {}", id);
        Optional<Faculty> faculty = facultyRepository.findById(id);
        if (faculty.isEmpty()) {
            logger.warn("Faculty with id {} not found", id);
        }
        return faculty;
    }

    public Optional<Faculty> editFaculty(Long id, Faculty faculty) {
        logger.info("Was invoked method for edit faculty with id: {}", id);
        logger.debug("Editing faculty data: {}", faculty);

        return facultyRepository.findById(id)
                .map(existingFaculty -> {
                    existingFaculty.setName(faculty.getName());
                    existingFaculty.setColor(faculty.getColor());
                    Faculty savedFaculty = facultyRepository.save(existingFaculty);
                    logger.debug("Faculty successfully updated: {}", savedFaculty);
                    return savedFaculty;
                });
    }

    public boolean deleteFaculty(Long id) {
        logger.info("Was invoked method for delete faculty with id: {}", id);
        if (facultyRepository.existsById(id)) {
            facultyRepository.deleteById(id);
            logger.info("Faculty with id {} successfully deleted", id);
            return true;
        }
        logger.warn("Attempt to delete non-existing faculty with id: {}", id);
        return false;
    }

    public List<Faculty> getFacultiesByColor(String color) {
        logger.info("Was invoked method for get faculties by color: {}", color);
        List<Faculty> faculties = facultyRepository.findByColor(color);
        logger.debug("Found {} faculties with color {}", faculties.size(), color);
        return faculties;
    }

    public List<Faculty> getFacultiesByNameOrColor(String searchString) {
        logger.info("Was invoked method for search faculties by name or color: {}", searchString);
        List<Faculty> faculties = facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase(searchString, searchString);
        logger.debug("Found {} faculties matching search: {}", faculties.size(), searchString);
        return faculties;
    }
}