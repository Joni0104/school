package ru.hogwarts.school.mvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FacultyService facultyService;

    private final Faculty testFaculty = new Faculty(1L, "Gryffindor", "Red");

    @Test
    void createFaculty_shouldReturnCreatedFaculty() throws Exception {
        // given
        when(facultyService.createFaculty(any(Faculty.class))).thenReturn(testFaculty);

        // when & then
        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFaculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("Red"));

        verify(facultyService, times(1)).createFaculty(any(Faculty.class));
    }

    @Test
    void getFaculty_shouldReturnFacultyWhenExists() throws Exception {
        // given
        when(facultyService.findFaculty(1L)).thenReturn(Optional.of(testFaculty));

        // when & then
        mockMvc.perform(get("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Gryffindor"))
                .andExpect(jsonPath("$.color").value("Red"));

        verify(facultyService, times(1)).findFaculty(1L);
    }

    @Test
    void getFaculty_shouldReturnNotFoundWhenNotExists() throws Exception {
        // given
        when(facultyService.findFaculty(999L)).thenReturn(Optional.empty());

        // when & then
        mockMvc.perform(get("/faculty/999"))
                .andExpect(status().isNotFound());

        verify(facultyService, times(1)).findFaculty(999L);
    }

    @Test
    void updateFaculty_shouldReturnUpdatedFaculty() throws Exception {
        // given
        Faculty updatedFaculty = new Faculty(1L, "Gryffindor", "Scarlet");
        when(facultyService.editFaculty(eq(1L), any(Faculty.class))).thenReturn(Optional.of(updatedFaculty));

        // when & then
        mockMvc.perform(put("/faculty/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFaculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.color").value("Scarlet"));

        verify(facultyService, times(1)).editFaculty(eq(1L), any(Faculty.class));
    }

    @Test
    void updateFaculty_shouldReturnNotFoundWhenNotExists() throws Exception {
        // given
        when(facultyService.editFaculty(eq(999L), any(Faculty.class))).thenReturn(Optional.empty());

        // when & then
        mockMvc.perform(put("/faculty/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFaculty)))
                .andExpect(status().isNotFound());

        verify(facultyService, times(1)).editFaculty(eq(999L), any(Faculty.class));
    }

    @Test
    void deleteFaculty_shouldReturnOkWhenExists() throws Exception {
        // given
        when(facultyService.deleteFaculty(1L)).thenReturn(true);

        // when & then
        mockMvc.perform(delete("/faculty/1"))
                .andExpect(status().isOk());

        verify(facultyService, times(1)).deleteFaculty(1L);
    }

    @Test
    void deleteFaculty_shouldReturnNotFoundWhenNotExists() throws Exception {
        // given
        when(facultyService.deleteFaculty(999L)).thenReturn(false);

        // when & then
        mockMvc.perform(delete("/faculty/999"))
                .andExpect(status().isNotFound());

        verify(facultyService, times(1)).deleteFaculty(999L);
    }

    @Test
    void getFacultiesByColor_shouldReturnFilteredFaculties() throws Exception {
        // given
        when(facultyService.getFacultiesByColor("Red")).thenReturn(List.of(testFaculty));

        // when & then
        mockMvc.perform(get("/faculty/filter/Red"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Gryffindor"))
                .andExpect(jsonPath("$[0].color").value("Red"));

        verify(facultyService, times(1)).getFacultiesByColor("Red");
    }

    @Test
    void searchFaculties_shouldReturnMatchingFaculties() throws Exception {
        // given
        when(facultyService.getFacultiesByNameOrColor("gryffindor")).thenReturn(List.of(testFaculty));

        // when & then
        mockMvc.perform(get("/faculty/search")
                        .param("search", "gryffindor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Gryffindor"));

        verify(facultyService, times(1)).getFacultiesByNameOrColor("gryffindor");
    }

    @Test
    void searchFaculties_shouldReturnEmptyWhenNoMatches() throws Exception {
        // given
        when(facultyService.getFacultiesByNameOrColor("hufflepuff")).thenReturn(List.of());

        // when & then
        mockMvc.perform(get("/faculty/search")
                        .param("search", "hufflepuff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(facultyService, times(1)).getFacultiesByNameOrColor("hufflepuff");
    }

    @Test
    void getFacultyStudents_shouldReturnStudentsList() throws Exception {
        // given
        when(facultyService.findFaculty(1L)).thenReturn(Optional.of(testFaculty));

        // when & then
        mockMvc.perform(get("/faculty/1/students"))
                .andExpect(status().isOk());

        verify(facultyService, times(1)).findFaculty(1L);
    }

    @Test
    void getFacultyStudents_shouldReturnNotFoundWhenFacultyNotExists() throws Exception {
        // given
        when(facultyService.findFaculty(999L)).thenReturn(Optional.empty());

        // when & then
        mockMvc.perform(get("/faculty/999/students"))
                .andExpect(status().isNotFound());

        verify(facultyService, times(1)).findFaculty(999L);
    }
}
