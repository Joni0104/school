package ru.hogwarts.school.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private final Student testStudent = new Student(1L, "Harry Potter", 17);

    @Test
    void createStudent_shouldReturnSavedStudent() {
        // given
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);

        // when
        Student result = studentService.createStudent(testStudent);

        // then
        assertNotNull(result);
        assertEquals("Harry Potter", result.getName());
        verify(studentRepository, times(1)).save(testStudent);
    }

    @Test
    void findStudent_shouldReturnStudentWhenExists() {
        // given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));

        // when
        Optional<Student> result = studentService.findStudent(1L);

        // then
        assertTrue(result.isPresent());
        assertEquals("Harry Potter", result.get().getName());
        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    void findStudent_shouldReturnEmptyWhenNotExists() {
        // given
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // when
        Optional<Student> result = studentService.findStudent(999L);

        // then
        assertTrue(result.isEmpty());
        verify(studentRepository, times(1)).findById(999L);
    }

    @Test
    void editStudent_shouldReturnUpdatedStudentWhenExists() {
        // given
        Student updatedStudent = new Student(1L, "Harry James Potter", 18);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(studentRepository.save(any(Student.class))).thenReturn(updatedStudent);

        // when
        Optional<Student> result = studentService.editStudent(1L, updatedStudent);

        // then
        assertTrue(result.isPresent());
        assertEquals("Harry James Potter", result.get().getName());
        assertEquals(18, result.get().getAge());
        verify(studentRepository, times(1)).findById(1L);
        verify(studentRepository, times(1)).save(testStudent);
    }

    @Test
    void editStudent_shouldReturnEmptyWhenNotExists() {
        // given
        Student student = new Student(999L, "Not exists", 20);
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // when
        Optional<Student> result = studentService.editStudent(999L, student);

        // then
        assertTrue(result.isEmpty());
        verify(studentRepository, times(1)).findById(999L);
        verify(studentRepository, never()).save(any());
    }

    @Test
    void deleteStudent_shouldReturnTrueWhenExists() {
        // given
        when(studentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(studentRepository).deleteById(1L);

        // when
        boolean result = studentService.deleteStudent(1L);

        // then
        assertTrue(result);
        verify(studentRepository, times(1)).existsById(1L);
        verify(studentRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteStudent_shouldReturnFalseWhenNotExists() {
        // given
        when(studentRepository.existsById(999L)).thenReturn(false);

        // when
        boolean result = studentService.deleteStudent(999L);

        // then
        assertFalse(result);
        verify(studentRepository, times(1)).existsById(999L);
        verify(studentRepository, never()).deleteById(any());
    }

    @Test
    void getStudentsByAge_shouldReturnFilteredStudents() {
        // given
        when(studentRepository.findByAge(17)).thenReturn(List.of(testStudent));

        // when
        List<Student> result = studentService.getStudentsByAge(17);

        // then
        assertEquals(1, result.size());
        assertEquals("Harry Potter", result.get(0).getName());
        verify(studentRepository, times(1)).findByAge(17);
    }

    @Test
    void getStudentsByAgeBetween_shouldReturnStudentsInRange() {
        // given
        when(studentRepository.findByAgeBetween(16, 18)).thenReturn(List.of(testStudent));

        // when
        List<Student> result = studentService.getStudentsByAgeBetween(16, 18);

        // then
        assertEquals(1, result.size());
        assertEquals("Harry Potter", result.get(0).getName());
        verify(studentRepository, times(1)).findByAgeBetween(16, 18);
    }
}
