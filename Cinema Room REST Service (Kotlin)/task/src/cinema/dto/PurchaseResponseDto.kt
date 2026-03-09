package cinema.dto

data class PurchaseResponseDto(
    val token: String,
    val ticket: SeatDto
)