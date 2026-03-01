package cinema.dto

data class SeatsResponseDto (
    val total_rows: Int,
    val total_columns: Int,
    val available_seats: List<SeatDto>
)
