package cinema.controller
import  cinema.dto.SeatsResponseDto
import cinema.service.CinemaService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class CinemaController ( private val cinemaService: CinemaService) {
    @GetMapping("/seats")
    fun getSeats(): SeatsResponseDto = cinemaService.getSeats()
    }
