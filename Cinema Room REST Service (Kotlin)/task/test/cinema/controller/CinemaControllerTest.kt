package cinema.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CinemaControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `should return seats`() {
        mockMvc.get("/seats")
            .andExpect {
                status { isOk() }
                content { contentTypeCompatibleWith(MediaType.APPLICATION_JSON) }
                jsonPath("$.total_rows") { value(9) }
                jsonPath("$.total_columns") { value(9) }
                jsonPath("$.available_seats.length()") { value(81) }
            }
    }

    @Test
    fun `should purchase seat`() {
        val requestBody = """
            {
              "row": 1,
              "column": 1
            }
        """.trimIndent()

        mockMvc.post("/purchase") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody
        }.andExpect {
            status { isOk() }
            jsonPath("$.token") { exists() }
            jsonPath("$.ticket.row") { value(1) }
            jsonPath("$.ticket.column") { value(1) }
            jsonPath("$.ticket.price") { value(10) }
        }
    }

    @Test
    fun `should reject out of bounds purchase`() {
        val requestBody = """
            {
              "row": 10,
              "column": 1
            }
        """.trimIndent()

        mockMvc.post("/purchase") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.error") { value("The number of a row or column is out of bounds!") }
        }
    }

    @Test
    fun `should reject already purchased seat`() {
        val requestBody = """
            {
              "row": 2,
              "column": 2
            }
        """.trimIndent()

        mockMvc.post("/purchase") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody
        }.andExpect {
            status { isOk() }
        }

        mockMvc.post("/purchase") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.error") { value("The ticket has been already purchased!") }
        }
    }

    @Test
    fun `should return purchased ticket`() {
        val purchaseBody = """
            {
              "row": 3,
              "column": 3
            }
        """.trimIndent()

        val purchaseResult = mockMvc.post("/purchase") {
            contentType = MediaType.APPLICATION_JSON
            content = purchaseBody
        }.andReturn()

        val responseJson = purchaseResult.response.contentAsString
        val token = """"token":"(.*?)"""".toRegex().find(responseJson)!!.groupValues[1]

        val returnBody = """
            {
              "token": "$token"
            }
        """.trimIndent()

        mockMvc.post("/return") {
            contentType = MediaType.APPLICATION_JSON
            content = returnBody
        }.andExpect {
            status { isOk() }
            jsonPath("$.returned_ticket.row") { value(3) }
            jsonPath("$.returned_ticket.column") { value(3) }
            jsonPath("$.returned_ticket.price") { value(10) }
        }
    }

    @Test
    fun `should reject wrong token`() {
        val requestBody = """
            {
              "token": "wrong-token"
            }
        """.trimIndent()

        mockMvc.post("/return") {
            contentType = MediaType.APPLICATION_JSON
            content = requestBody
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.error") { value("Wrong token!") }
        }
    }
}
