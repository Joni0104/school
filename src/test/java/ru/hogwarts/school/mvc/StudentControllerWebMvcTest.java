package ru.hogwarts.school.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentService studentService;

    private final Student testStudent = new Student(1L, "Harry Potter", 17);

    @Test
    void createStudent_shouldReturnCreatedStudent() throws Exception {
        // given
        when(studentService.createStudent(any(Student.class))).thenReturn(testStudent);

        // when & then
        mockMvc.perform(post("/student")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Harry Potter"))
                .andExpect(jsonPath("$.age").value(17));

        verify(studentService, times(1)).createStudent(any(Student.class));
    }

    @Test
    void getStudent_shouldReturnStudentWhenExists() throws Exception {
        // given
        when(studentService.findStudent(1L)).thenReturn(Optional.of(testStudent));

        // when & then
        mockMvc.perform(get("/student/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Harry Potter"))
                .andExpect(jsonPath("$.age").value(17));

        verify(studentService, times(1)).findStudent(1L);
    }

    @Test
    void getStudent_shouldReturnNotFoundWhenNotExists() throws Exception {
        // given
        when(studentService.findStudent(999L)).thenReturn(Optional.empty());

        // when & then
        mockMvc.perform(get("/student/999"))
                .andExpect(status().isNotFound());

        verify(studentService, times(1)).findStudent(999L);
    }

    @Test
    void updateStudent_shouldReturnUpdatedStudent() throws Exception {
        // given
        Student updatedStudent = new Student(1L, "Harry James Potter", 18);
        when(studentService.editStudent(eq(1L), any(Student.class))).thenReturn(Optional.of(updatedStudent));

        // when & then
        mockMvc.perform(put("/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedStudent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Harry James Potter"))
                .andExpect(jsonPath("$.age").value(18));

        verify(studentService, times(1)).editStudent(eq(1L), any(Student.class));
    }

    @Test
    void updateStudent_shouldReturnNotFoundWhenNotExists() throws Exception {
        // given
        when(studentService.editStudent(eq(999L), any(Student.class))).thenReturn(Optional.empty());

        // when & then
        mockMvc.perform(put("/student/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testStudent)))
                .andExpect(status().isNotFound());

        verify(studentService, times(1)).editStudent(eq(999L), any(Student.class));
    }

    @Test
    void deleteStudent_shouldReturnOkWhenExists() throws Exception {
        // given
        when(studentService.deleteStudent(1L)).thenReturn(true);

        // when & then
        mockMvc.perform(delete("/student/1"))
                .andExpect(status().isOk());

        verify(studentService, times(1)).deleteStudent(1L);
    }

    @Test
    void deleteStudent_shouldReturnNotFoundWhenNotExists() throws Exception {
        // given
        when(studentService.deleteStudent(999L)).thenReturn(false);

        // when & then
        mockMvc.perform(delete("/student/999"))
                .andExpect(status().isNotFound());

        verify(studentService, times(1)).deleteStudent(999L);
    }

    @Test
    void getStudentsByAge_shouldReturnFilteredStudents() throws Exception {
        // given
        when(studentService.getStudentsByAge(17)).thenReturn(List.of(testStudent));

        // when & then
        mockMvc.perform(get("/student/filter/17"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Harry Potter"))
                .andExpect(jsonPath("$[0].age").value(17));

        verify(studentService, times(1)).getStudentsByAge(17);
    }

    @Test
    void getStudentsByAgeBetween_shouldReturnStudentsInRange() throws Exception {
        // given
        when(studentService.getStudentsByAgeBetween(16, 18)).thenReturn(List.of(testStudent));

        // when & then
        mockMvc.perform(get("/student/filter")
                        .param("min", "16")
                        .param("max", "18"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Harry Potter"));

        verify(studentService, times(1)).getStudentsByAgeBetween(16, 18);
    }

    @Test
    void getStudentFaculty_shouldReturnFaculty() throws Exception {
        // given
        when(studentService.findStudent(1L)).thenReturn(Optional.of(testStudent));

        // when & then
        mockMvc.perform(get("/student/1/faculty"))
                .andExpect(status().isOk());

        verify(studentService, times(1)).findStudent(1L);
    }

    @Test
    void getStudentFaculty_shouldReturnNotFoundWhenStudentNotExists() throws Exception {
        // given
        when(studentService.findStudent(999L)).thenReturn(Optional.empty());

        // when & then
        mockMvc.perform(get("/student/999/faculty"))
                .andExpect(status().isNotFound());

        verify(studentService, times(1)).findStudent(999L);
    }
}