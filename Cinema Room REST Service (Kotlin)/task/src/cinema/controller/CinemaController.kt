package cinema.controller

import cinema.dto.PurchaseRequestDto
import cinema.dto.ReturnRequestDto
import cinema.dto.SeatsResponseDto
import cinema.service.CinemaService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class CinemaController ( private val cinemaService: CinemaService) {
    @GetMapping("/seats")
    fun getSeats(): SeatsResponseDto = cinemaService.getSeats()

    @PostMapping("/purchase")
    fun purchaseTicket(@RequestBody request: PurchaseRequestDto) =
        cinemaService.purchaseTicket(request)

    @PostMapping("/return")
    fun returnTicket(@RequestBody request: ReturnRequestDto) =
        cinemaService.returnTicket(request)
}