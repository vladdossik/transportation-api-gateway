package transportation.controller.bills;

import bill.BillPostDto;
import bill.BillPutDto;
import bill.BillResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import transportation.service.UserService;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("v1/bills")
@RequiredArgsConstructor
public class BillsController {

    private final WebClient billServiceClient;
    private final UserService userService;

    @Operation(summary = "Добавить платеж")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос выполнен успешно", content = @Content(schema = @Schema(implementation = BillResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибочный запрос"),
            @ApiResponse(responseCode = "409", description = "Запись уже существует"),
            @ApiResponse(responseCode = "503", description = "Сервис временно недоступен")
    })
    @PostMapping("/add")
    public Mono<BillResponseDto> add(@Valid @RequestBody BillPostDto billPostDto) {
        return userService.getUser(billPostDto.getUserId())
                        .flatMap(result ->
                                billServiceClient.post()
                                        .uri(uriBuilder -> uriBuilder
                                                .path("bills/add")
                                                .build())
                                        .accept(MediaType.APPLICATION_JSON)
                                        .body(BodyInserters.fromValue(billPostDto))
                                        .retrieve()
                                        .bodyToMono(BillResponseDto.class));
    }

    @Operation(summary = "Удалить платеж по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос выполнен успешно", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Ошибочный запрос"),
            @ApiResponse(responseCode = "409", description = "Запись уже существует"),
            @ApiResponse(responseCode = "503", description = "Сервис временно недоступен")
    })
    @DeleteMapping("/{externalId}/delete")
    public Mono<String> delete(@PathVariable UUID externalId) {
        return billServiceClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("bills/{externalId}/delete")
                        .build(externalId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
    }

    @Operation(summary = "Обновить данные о платеже")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос выполнен успешно", content = @Content(schema = @Schema(implementation = BillResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибочный запрос"),
            @ApiResponse(responseCode = "409", description = "Запись уже существует"),
            @ApiResponse(responseCode = "503", description = "Сервис временно недоступен")
    })
    @PutMapping("{externalId}")
    public Mono<BillResponseDto> update(@Valid @PathVariable UUID externalId, @RequestBody BillPutDto billPutDto) {
        return billServiceClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("bills/{externalId}")
                        .build(externalId))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(billPutDto))
                .retrieve()
                .bodyToMono(BillResponseDto.class);
    }
}
