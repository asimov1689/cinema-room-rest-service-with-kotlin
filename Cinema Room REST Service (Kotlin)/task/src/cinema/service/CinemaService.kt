package cinema.service

import cinema.dto.SeatDto
import cinema.dto.SeatsResponseDto
import org.springframework.stereotype.Service

private const val TOTAL_ROWS = 9
private const val TOTAL_COLUMNS = 9

@Service
class CinemaService {
    fun getSeats(): SeatsResponseDto {
        val seats = (1..TOTAL_ROWS).flatMap { row ->
            (1..TOTAL_COLUMNS).map { col ->
            SeatDto(row = row, column = col)
            }
        }

        return SeatsResponseDto(
            total_rows = TOTAL_ROWS,
            total_columns = TOTAL_COLUMNS,
            available_seats = seats
        )
    }
}