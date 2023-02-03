package transportation.controller.user;

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
import users.UserPostDto;
import users.UserPutDto;
import users.UserResponseDto;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("v1/users")
@RequiredArgsConstructor
public class UserController {

    private final WebClient userServiceClient;

    @Operation(summary = "Добавить пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос выполнен успешно", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибочный запрос"),
            @ApiResponse(responseCode = "409", description = "Запись уже существует"),
            @ApiResponse(responseCode = "503", description = "Сервис временно недоступен")
    })
    @PostMapping("/add")
    public Mono<UserResponseDto> add(@Valid @RequestBody UserPostDto userPostDto) {
        return userServiceClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("users/add")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userPostDto))
                .retrieve()
                .bodyToMono(UserResponseDto.class);
    }

    @Operation(summary = "Удалить пользователя по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос выполнен успешно", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Ошибочный запрос"),
            @ApiResponse(responseCode = "409", description = "Запись уже существует"),
            @ApiResponse(responseCode = "503", description = "Сервис временно недоступен")
    })
    @DeleteMapping("/{externalId}/delete")
    public Mono<String> deleteById(@PathVariable UUID externalId) {
        return userServiceClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("users/{externalId}/delete")
                        .build(externalId))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
    }

    @Operation(summary = "Обновить данные пользователея")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Запрос выполнен успешно", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибочный запрос"),
            @ApiResponse(responseCode = "409", description = "Запись уже существует"),
            @ApiResponse(responseCode = "503", description = "Сервис временно недоступен")
    })
    @PutMapping("/{externalId}")
    public Mono<UserResponseDto> update(@PathVariable UUID externalId, @Valid @RequestBody UserPutDto userPutDto) {
        return userServiceClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("users/{externalId}")
                        .build(externalId))
                .accept(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userPutDto))
                .retrieve()
                .bodyToMono(UserResponseDto.class);
    }
}
