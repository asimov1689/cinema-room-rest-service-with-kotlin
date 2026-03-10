package cinema.service

import cinema.dto.ErrorResponseDto
import cinema.dto.PurchaseRequestDto
import cinema.dto.PurchaseResponseDto
import cinema.dto.ReturnRequestDto
import cinema.dto.ReturnResponseDto
import cinema.dto.SeatDto
import cinema.dto.SeatsResponseDto
import cinema.dto.StatsResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.UUID

private const val TOTAL_ROWS = 9
private const val TOTAL_COLUMNS = 9
private const val FRONT_ROWS_PRICE = 10
private const val BACK_ROWS_PRICE = 8
private const val STATS_PASSWORD = "super_secret"

@Service
class CinemaService {

    private val availableSeats: MutableList<SeatDto> = createInitialSeats()

    private val purchasedTickets: MutableMap<String, SeatDto> = mutableMapOf()

    fun getSeats(): SeatsResponseDto {
        return SeatsResponseDto(
            total_rows = TOTAL_ROWS,
            total_columns = TOTAL_COLUMNS,
            available_seats = availableSeats.sortedWith(
                compareBy<SeatDto> { it.row }.thenBy { it.column }
            )
        )
    }

    fun purchaseTicket(request: PurchaseRequestDto): ResponseEntity<Any> {
        if (request.row !in 1..TOTAL_ROWS || request.column !in 1..TOTAL_COLUMNS) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDto("The number of a row or column is out of bounds!"))
        }

        val seat = availableSeats.find { it.row == request.row && it.column == request.column }

        if (seat == null) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDto("The ticket has been already purchased!"))
        }

        availableSeats.remove(seat)

        val token = UUID.randomUUID().toString()
        purchasedTickets[token] = seat

        return ResponseEntity.ok(
            PurchaseResponseDto(
                token = token,
                ticket = seat
            )
        )
    }

    fun returnTicket(request: ReturnRequestDto): ResponseEntity<Any> {
        val seat = purchasedTickets.remove(request.token)
            ?: return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponseDto("Wrong token!"))

        availableSeats.add(seat)

        return ResponseEntity.ok(
            ReturnResponseDto(
                returned_ticket = seat
            )
        )
    }

    fun getStats(password: String?): ResponseEntity<Any> {
        if (password != STATS_PASSWORD) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponseDto("The password is wrong!"))
        }

        val currentIncome = purchasedTickets.values.sumOf { it.price }
        val numberOfAvailableSeats = availableSeats.size
        val numberOfPurchasedTickets = purchasedTickets.size

        return ResponseEntity.ok(
            StatsResponseDto(
                current_income = currentIncome,
                number_of_available_seats = numberOfAvailableSeats,
                number_of_purchased_tickets = numberOfPurchasedTickets
            )
        )
    }

    private fun createInitialSeats(): MutableList<SeatDto> {
        return (1..TOTAL_ROWS).flatMap { row ->
            (1..TOTAL_COLUMNS).map { column ->
                SeatDto(
                    row = row,
                    column = column,
                    price = calculatePrice(row)
                )
            }
        }.toMutableList()
    }

    private fun calculatePrice(row: Int): Int {
        return if (row <= 4) FRONT_ROWS_PRICE else BACK_ROWS_PRICE
    }
}