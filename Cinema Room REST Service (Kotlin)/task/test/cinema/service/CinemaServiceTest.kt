package cinema.service


import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CinemaServiceTest {

    @Test
    fun `getSeats returns 9x9 cinema and 81 available seats`() {
        // Arrange
        val service = CinemaService()

        // Act
        val result = service.getSeats()


      // Assert
        assertEquals(9, result.total_rows)
        assertEquals(9, result.total_columns)
        assertEquals(81, result.available_seats.size)
    }

    @Test
    fun `getSeats contains boundary seats (1,1) and (9,9)`() {
        // Arrange
        val service = CinemaService()

        // Act
        val result = service.getSeats()
        val first = result.available_seats.first()
        val last = result.available_seats.last()

        // Assert
        assertEquals(1, first.row)
        assertEquals(1, first.column)
        assertEquals(9, last.row)
        assertEquals(9, last.column)
    }
    @Test
    fun `getSeats contains only valid coordinates within 1 to 9`() {
        // Arrange
        val service = CinemaService()

        // Act
        val result = service.getSeats()

        // Assert
        assertTrue(result.available_seats.all { it.row in 1..9 })
        assertTrue(result.available_seats.all { it.column in 1..9 })
    }
}