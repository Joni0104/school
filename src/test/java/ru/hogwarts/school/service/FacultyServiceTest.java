package ru.hogwarts.school.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FacultyServiceTest {

    @Mock
    private FacultyRepository facultyRepository;

    @InjectMocks
    private FacultyService facultyService;

    private final Faculty testFaculty = new Faculty(1L, "Gryffindor", "Red");

    @Test
    void createFaculty_shouldReturnSavedFaculty() {
        // given
        when(facultyRepository.save(any(Faculty.class))).thenReturn(testFaculty);

        // when
        Faculty result = facultyService.createFaculty(testFaculty);

        // then
        assertNotNull(result);
        assertEquals("Gryffindor", result.getName());
        verify(facultyRepository, times(1)).save(testFaculty);
    }

    @Test
    void findFaculty_shouldReturnFacultyWhenExists() {
        // given
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(testFaculty));

        // when
        Optional<Faculty> result = facultyService.findFaculty(1L);

        // then
        assertTrue(result.isPresent());
        assertEquals("Gryffindor", result.get().getName());
        verify(facultyRepository, times(1)).findById(1L);
    }

    @Test
    void findFaculty_shouldReturnEmptyWhenNotExists() {
        // given
        when(facultyRepository.findById(999L)).thenReturn(Optional.empty());

        // when
        Optional<Faculty> result = facultyService.findFaculty(999L);

        // then
        assertTrue(result.isEmpty());
        verify(facultyRepository, times(1)).findById(999L);
    }

    @Test
    void editFaculty_shouldReturnUpdatedFacultyWhenExists() {
        // given
        Faculty updatedFaculty = new Faculty(1L, "Gryffindor", "Scarlet");
        when(facultyRepository.findById(1L)).thenReturn(Optional.of(testFaculty));
        when(facultyRepository.save(any(Faculty.class))).thenReturn(updatedFaculty);

        // when
        Optional<Faculty> result = facultyService.editFaculty(1L, updatedFaculty);

        // then
        assertTrue(result.isPresent());
        assertEquals("Scarlet", result.get().getColor());
        verify(facultyRepository, times(1)).findById(1L);
        verify(facultyRepository, times(1)).save(testFaculty);
    }

    @Test
    void editFaculty_shouldReturnEmptyWhenNotExists() {
        // given
        Faculty faculty = new Faculty(999L, "Not exists", "Black");
        when(facultyRepository.findById(999L)).thenReturn(Optional.empty());

        // when
        Optional<Faculty> result = facultyService.editFaculty(999L, faculty);

        // then
        assertTrue(result.isEmpty());
        verify(facultyRepository, times(1)).findById(999L);
        verify(facultyRepository, never()).save(any());
    }

    @Test
    void deleteFaculty_shouldReturnTrueWhenExists() {
        // given
        when(facultyRepository.existsById(1L)).thenReturn(true);
        doNothing().when(facultyRepository).deleteById(1L);

        // when
        boolean result = facultyService.deleteFaculty(1L);

        // then
        assertTrue(result);
        verify(facultyRepository, times(1)).existsById(1L);
        verify(facultyRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteFaculty_shouldReturnFalseWhenNotExists() {
        // given
        when(facultyRepository.existsById(999L)).thenReturn(false);

        // when
        boolean result = facultyService.deleteFaculty(999L);

        // then
        assertFalse(result);
        verify(facultyRepository, times(1)).existsById(999L);
        verify(facultyRepository, never()).deleteById(any());
    }

    @Test
    void getFacultiesByColor_shouldReturnFilteredFaculties() {
        // given
        when(facultyRepository.findByColor("Red")).thenReturn(List.of(testFaculty));

        // when
        List<Faculty> result = facultyService.getFacultiesByColor("Red");

        // then
        assertEquals(1, result.size());
        assertEquals("Gryffindor", result.get(0).getName());
        verify(facultyRepository, times(1)).findByColor("Red");
    }

    @Test
    void getFacultiesByNameOrColor_shouldReturnMatchingFaculties() {
        // given
        when(facultyRepository.findByNameIgnoreCaseOrColorIgnoreCase("gryffindor", "gryffindor"))
                .thenReturn(List.of(testFaculty));

        // when
        List<Faculty> result = facultyService.getFacultiesByNameOrColor("gryffindor");

        // then
        assertEquals(1, result.size());
        assertEquals("Gryffindor", result.get(0).getName());
        verify(facultyRepository, times(1))
                .findByNameIgnoreCaseOrColorIgnoreCase("gryffindor", "gryffindor");
    }

    @Test
    void getLongestFacultyName_shouldReturnLongestName() {
        // given
        Faculty faculty1 = new Faculty(1L, "Гриффиндор", "Красный");
        Faculty faculty2 = new Faculty(2L, "Слизерин", "Зеленый");
        Faculty faculty3 = new Faculty(3L, "Когтевран", "Синий");

        when(facultyRepository.findAll()).thenReturn(List.of(faculty1, faculty2, faculty3));

        // when
        String result = facultyService.getLongestFacultyName();

        // then
        assertEquals("Гриффиндор", result);
        verify(facultyRepository, times(1)).findAll();
    }

    @Test
    void getLongestFacultyName_shouldReturnEmptyWhenNoFaculties() {
        // given
        when(facultyRepository.findAll()).thenReturn(List.of());

        // when
        String result = facultyService.getLongestFacultyName();

        // then
        assertEquals("", result);
        verify(facultyRepository, times(1)).findAll();
    }
}