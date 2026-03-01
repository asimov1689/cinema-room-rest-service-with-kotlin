package cinema.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class SeatsEndpointIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `GET seats returns 200 and correct JSON contract`() {
        // Arrange
        val url = "/seats"

        // Act + Assert
        mockMvc.get(url)
            .andExpect {
                status { isOk() }
                content { contentTypeCompatibleWith("application/json") }

                // Core contract fields + values
                jsonPath("$.total_rows") { value(9) }
                jsonPath("$.total_columns") { value(9) }
                jsonPath("$.available_seats.length()") { value(81) }

                // Spot-check boundaries and ordering
                jsonPath("$.available_seats[0].row") { value(1) }
                jsonPath("$.available_seats[0].column") { value(1) }
                jsonPath("$.available_seats[80].row") { value(9) }
                jsonPath("$.available_seats[80].column") { value(9) }
            }
    }

    @Test
    fun `GET seats uses expected snake_case field names`() {
        // Arrange
        val url = "/seats"

        // Act + Assert
        mockMvc.get(url)
            .andExpect {
                status { isOk() }

                // Required fields exist
                jsonPath("$.total_rows") { exists() }
                jsonPath("$.total_columns") { exists() }
                jsonPath("$.available_seats") { exists() }

                // Guardrail: ensure alternative naming does not slip in
                jsonPath("$.totalRows") { doesNotExist() }
                jsonPath("$.totalColumns") { doesNotExist() }
                jsonPath("$.availableSeats") { doesNotExist() }
            }
    }

    @Test
    fun `GET seats returns valid seat objects with row and column`() {
        // Arrange
        val url = "/seats"

        // Act + Assert
        mockMvc.get(url)
            .andExpect {
                status { isOk() }

                // Validate the shape of a seat object
                jsonPath("$.available_seats[0].row") { exists() }
                jsonPath("$.available_seats[0].column") { exists() }
            }
    }

    @Test
    fun `GET seats sampled seats match expected coordinates`() {
        // Arrange
        val url = "/seats"

        // Act + Assert
        mockMvc.get(url)
            .andExpect {
                status { isOk() }

                // Sample indices: first, middle, last (row-major ordering)
                jsonPath("$.available_seats[0].row") { value(1) }
                jsonPath("$.available_seats[0].column") { value(1) }

                jsonPath("$.available_seats[40].row") { value(5) }
                jsonPath("$.available_seats[40].column") { value(5) }

                jsonPath("$.available_seats[80].row") { value(9) }
                jsonPath("$.available_seats[80].column") { value(9) }
            }
    }
}