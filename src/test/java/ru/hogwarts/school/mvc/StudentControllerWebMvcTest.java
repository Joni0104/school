package ru.hogwarts.school.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
class StudentControllerWebMvcTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    private final List<Student> testStudents = List.of(
            new Student(1L, "Гарри Поттер", 17),
            new Student(2L, "Гермиона Грейнджер", 17),
            new Student(3L, "Рон Уизли", 17),
            new Student(4L, "Драко Малфой", 17),
            new Student(5L, "Луна Лавгуд", 16),
            new Student(6L, "Невилл Долгопупс", 17)
    );

    @Test
    void printStudentsParallel_shouldReturnOk() throws Exception {
        // given
        when(studentService.getAllStudents()).thenReturn(testStudents);

        // when & then
        mockMvc.perform(get("/student/print-parallel"))
                .andExpect(status().isOk());

        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    void printStudentsSynchronized_shouldReturnOk() throws Exception {
        // given
        when(studentService.getAllStudents()).thenReturn(testStudents);

        // when & then
        mockMvc.perform(get("/student/print-synchronized"))
                .andExpect(status().isOk());

        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    void printStudentsParallel_shouldHandleInsufficientStudents() throws Exception {
        // given
        when(studentService.getAllStudents()).thenReturn(List.of(
                new Student(1L, "Гарри Поттер", 17),
                new Student(2L, "Гермиона Грейнджер", 17)
        ));

        // when & then
        mockMvc.perform(get("/student/print-parallel"))
                .andExpect(status().isOk());

        verify(studentService, times(1)).getAllStudents();
    }
}