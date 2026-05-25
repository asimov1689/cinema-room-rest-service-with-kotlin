package cinema.service

import cinema.dto.ErrorResponseDto
import cinema.dto.PurchaseRequestDto
import cinema.dto.PurchaseResponseDto
import cinema.dto.ReturnRequestDto
import cinema.dto.ReturnResponseDto
import cinema.dto.StatsResponseDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class CinemaServiceTest {

    private lateinit var cinemaService: CinemaService

    @BeforeEach
    fun setUp() {
        cinemaService = CinemaService("super_secret")
    }

    @Test
    fun `should return all seats initially`() {
        // Arrange

        // Act
        val result = cinemaService.getSeats()

        // Assert
        assertEquals(9, result.total_rows)
        assertEquals(9, result.total_columns)
        assertEquals(81, result.available_seats.size)
    }

    @Test
    fun `should purchase a valid ticket`() {
        // Arrange
        val request = PurchaseRequestDto(row = 1, column = 1)

        // Act
        val response = cinemaService.purchaseTicket(request)

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)

        val body = response.body as PurchaseResponseDto
        assertNotNull(body.token)
        assertEquals(1, body.ticket.row)
        assertEquals(1, body.ticket.column)
        assertEquals(10, body.ticket.price)

        val seatsAfterPurchase = cinemaService.getSeats()
        assertEquals(80, seatsAfterPurchase.available_seats.size)
        assertFalse(seatsAfterPurchase.available_seats.any { it.row == 1 && it.column == 1 })
    }

    @Test
    fun `should reject purchase when seat is out of bounds`() {
        // Arrange
        val request = PurchaseRequestDto(row = 10, column = 1)

        // Act
        val response = cinemaService.purchaseTicket(request)

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)

        val body = response.body as ErrorResponseDto
        assertEquals("The number of a row or column is out of bounds!", body.error)
    }

    @Test
    fun `should reject purchase when seat is already purchased`() {
        // Arrange
        val request = PurchaseRequestDto(row = 2, column = 2)
        cinemaService.purchaseTicket(request)

        // Act
        val response = cinemaService.purchaseTicket(request)

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)

        val body = response.body as ErrorResponseDto
        assertEquals("The ticket has been already purchased!", body.error)
    }

    @Test
    fun `should return a purchased ticket`() {
        // Arrange
        val purchaseRequest = PurchaseRequestDto(row = 3, column = 3)
        val purchaseResponse = cinemaService.purchaseTicket(purchaseRequest)
        val purchaseBody = purchaseResponse.body as PurchaseResponseDto
        val returnRequest = ReturnRequestDto(token = purchaseBody.token)

        // Act
        val returnResponse = cinemaService.returnTicket(returnRequest)

        // Assert
        assertEquals(HttpStatus.OK, returnResponse.statusCode)

        val body = returnResponse.body as ReturnResponseDto
        assertEquals(3, body.returned_ticket.row)
        assertEquals(3, body.returned_ticket.column)
        assertEquals(10, body.returned_ticket.price)

        val seatsAfterReturn = cinemaService.getSeats()
        assertTrue(seatsAfterReturn.available_seats.any { it.row == 3 && it.column == 3 })
    }

    @Test
    fun `should reject return with wrong token`() {
        // Arrange
        val request = ReturnRequestDto(token = "wrong-token")

        // Act
        val response = cinemaService.returnTicket(request)

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)

        val body = response.body as ErrorResponseDto
        assertEquals("Wrong token!", body.error)
    }

    @Test
    fun `should reject stats without password`() {
        // Arrange

        // Act
        val response = cinemaService.getStats(null)

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)

        val body = response.body as ErrorResponseDto
        assertEquals("The password is wrong!", body.error)
    }

    @Test
    fun `should reject stats with wrong password`() {
        // Arrange

        // Act
        val response = cinemaService.getStats("wrong_password")

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.statusCode)

        val body = response.body as ErrorResponseDto
        assertEquals("The password is wrong!", body.error)
    }

    @Test
    fun `should return stats with correct password`() {
        // Arrange
        cinemaService.purchaseTicket(PurchaseRequestDto(row = 1, column = 1))
        cinemaService.purchaseTicket(PurchaseRequestDto(row = 1, column = 2))

        // Act
        val response = cinemaService.getStats("super_secret")

        // Assert
        assertEquals(HttpStatus.OK, response.statusCode)

        val body = response.body as StatsResponseDto
        assertEquals(20, body.current_income)
        assertEquals(79, body.number_of_available_seats)
        assertEquals(2, body.number_of_purchased_tickets)
    }
}