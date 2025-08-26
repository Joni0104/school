package ru.hogwarts.school.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.service.FacultyService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FacultyController.class)
class FacultyControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacultyService facultyService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Faculty testFaculty = new Faculty(1L, "Гриффиндор", "красный");

    @Test
    void createFaculty_shouldReturnCreatedFaculty() throws Exception {
        when(facultyService.createFaculty(any(Faculty.class))).thenReturn(testFaculty);

        mockMvc.perform(MockMvcRequestBuilders.post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFaculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Гриффиндор"))
                .andExpect(jsonPath("$.color").value("красный"));
    }

    @Test
    void getFaculty_shouldReturnFacultyWhenExists() throws Exception {
        when(facultyService.findFaculty(1L)).thenReturn(Optional.of(testFaculty));

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Гриффиндор"))
                .andExpect(jsonPath("$.color").value("красный"));
    }

    @Test
    void getFaculty_shouldReturnNotFoundWhenNotExists() throws Exception {
        when(facultyService.findFaculty(999L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateFaculty_shouldReturnUpdatedFaculty() throws Exception {
        Faculty updatedFaculty = new Faculty(1L, "Слизерин", "зеленый");
        when(facultyService.editFaculty(eq(1L), any(Faculty.class))).thenReturn(Optional.of(updatedFaculty));

        mockMvc.perform(MockMvcRequestBuilders.put("/faculty/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFaculty)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Слизерин"))
                .andExpect(jsonPath("$.color").value("зеленый"));
    }

    @Test
    void deleteFaculty_shouldReturnOkWhenExists() throws Exception {
        when(facultyService.deleteFaculty(1L)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/faculty/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getFacultiesByColor_shouldReturnFacultiesList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/filter/красный"))
                .andExpect(status().isOk());
    }

    @Test
    void searchFaculties_shouldReturnFacultiesList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/faculty/search")
                        .param("search", "Гриффиндор"))
                .andExpect(status().isOk());
    }
}