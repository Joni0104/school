package ru.hogwarts.school.mvc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.service.StudentService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
class StudentControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Test
    void printStudentsParallel_shouldReturnOk() throws Exception {
        // given
        doNothing().when(studentService).printStudentsParallel();

        // when & then
        mockMvc.perform(get("/student/print-parallel"))
                .andExpect(status().isOk());

        verify(studentService, times(1)).printStudentsParallel();
    }

    @Test
    void printStudentsSynchronized_shouldReturnOk() throws Exception {
        // given
        doNothing().when(studentService).printStudentsSynchronized();

        // when & then
        mockMvc.perform(get("/student/print-synchronized"))
                .andExpect(status().isOk());

        verify(studentService, times(1)).printStudentsSynchronized();
    }
}