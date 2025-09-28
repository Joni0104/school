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

    @Test
    void getTotalCount_shouldReturnStudentCount() {
        // given
        when(studentRepository.countAllStudents()).thenReturn(10);

        // when
        Integer result = studentService.getTotalCount();

        // then
        assertEquals(10, result);
        verify(studentRepository, times(1)).countAllStudents();
    }

    @Test
    void getAverageAge_shouldReturnAverageAge() {
        // given
        when(studentRepository.findAverageAge()).thenReturn(17.5);

        // when
        Double result = studentService.getAverageAge();

        // then
        assertEquals(17.5, result);
        verify(studentRepository, times(1)).findAverageAge();
    }

    @Test
    void getLastFiveStudents_shouldReturnLastFiveStudents() {
        // given
        Student student1 = new Student(1L, "Student1", 17);
        Student student2 = new Student(2L, "Student2", 18);
        when(studentRepository.findLastFiveStudents()).thenReturn(List.of(student1, student2));

        // when
        List<Student> result = studentService.getLastFiveStudents();

        // then
        assertEquals(2, result.size());
        verify(studentRepository, times(1)).findLastFiveStudents();
    }

    @Test
    void getStudentsNamesStartingWithA_shouldReturnFilteredSortedNames() {
        // given
        Student student1 = new Student(1L, "Анна", 20);
        Student student2 = new Student(2L, "алексей", 21);
        Student student3 = new Student(3L, "Борис", 22);
        Student student4 = new Student(4L, "АРИНА", 19);
        Student student5 = new Student(5L, "", 23); // пустое имя
        Student student6 = new Student(6L, null, 24); // null имя

        when(studentRepository.findAll()).thenReturn(List.of(student1, student2, student3, student4, student5, student6));

        // when
        List<String> result = studentService.getStudentsNamesStartingWithA();

        // then
        assertEquals(3, result.size());
        assertEquals("АЛЕКСЕЙ", result.get(0));
        assertEquals("АННА", result.get(1));
        assertEquals("АРИНА", result.get(2));
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void getStudentsNamesStartingWithA_shouldReturnEmptyListWhenNoMatches() {
        // given
        Student student1 = new Student(1L, "Борис", 22);
        Student student2 = new Student(2L, "Виктор", 23);

        when(studentRepository.findAll()).thenReturn(List.of(student1, student2));

        // when
        List<String> result = studentService.getStudentsNamesStartingWithA();

        // then
        assertTrue(result.isEmpty());
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void getAverageAgeStream_shouldReturnAverageAge() {
        // given
        Student student1 = new Student(1L, "Анна", 20);
        Student student2 = new Student(2L, "Алексей", 30);

        when(studentRepository.findAll()).thenReturn(List.of(student1, student2));

        // when
        Double result = studentService.getAverageAgeStream();

        // then
        assertEquals(25.0, result);
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void getAverageAgeStream_shouldReturnZeroWhenNoStudents() {
        // given
        when(studentRepository.findAll()).thenReturn(List.of());

        // when
        Double result = studentService.getAverageAgeStream();

        // then
        assertEquals(0.0, result);
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    void calculateSum_shouldReturnCorrectSum() {
        // when
        Long result = studentService.calculateSum();

        // then
        assertEquals(500000500000L, result); // 1_000_000 * 1_000_001 / 2
    }
}
