//package ru.practicum.compilation;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.validation.constraints.Positive;
//import jakarta.validation.constraints.PositiveOrZero;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//import ru.practicum.client.StatsClient;
//import ru.practicum.compilation.model.CompilationMapper;
//import ru.practicum.compilation.model.dto.CompilationDto;
//import ru.practicum.compilation.model.dto.NewCompilationDto;
//import ru.practicum.event.EventService;
//import ru.practicum.event.model.EventMapper;
//import ru.practicum.event.model.dto.*;
//import ru.practicum.logger.ColoredCRUDLogger;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//
//@Slf4j
//@Validated
//@RestController
//@RequiredArgsConstructor
//public class CompilationController {
//    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//    private final CompilationService compilationService;
//    private final CompilationMapper compilationMapper;
//    @PostMapping("/admin/compilations")
//    @ResponseStatus(HttpStatus.CREATED)
//    public CompilationDto create(@Validated @RequestBody NewCompilationDto newCompilationDto) {
//        String url = "MAIN /admin/compilations";
//        ColoredCRUDLogger.logPost(url, newCompilationDto.toString());
//        CompilationDto result = compilationService.create(newCompilationDto);
//        ColoredCRUDLogger.logPostComplete(url, result.toString());
//        return result;
//    }
//
////    @PatchMapping("/users/{userId}/events/{eventId}")
////    public EventFullDto update(@PathVariable Long userId,
////                               @PathVariable Long eventId,
////                               @Validated @RequestBody UpdateEventUserRequest updateEvent) {
////        String url = String.format("MAIN /users/{%s}/events/{%s}", userId, eventId);
////        ColoredCRUDLogger.logPatch(url, updateEvent.toString());
////        EventFullDto result = eventService.update(eventMapper.createEventUpdateParam(userId, eventId), updateEvent);
////        ColoredCRUDLogger.logPatchComplete(url, result.toString());
////        return result;
////    }
////
////    @GetMapping("/users/{userId}/events")
////    public List<EventShortDto> getAllByInitiator(@PathVariable Long userId,
////                                                 @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
////                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
////        String url = String.format("MAIN /users/{%s}/events?{%s}&{%s}", userId, from, size);
////        ColoredCRUDLogger.logGet(url);
////        List<EventShortDto> result = eventService.getAllByInitiator(userId, eventMapper.createPaginationEventParam(from, size));
////        ColoredCRUDLogger.logGetComplete(url, "size = " + result.size());
////        return result;
////    }
////
////    @GetMapping("/events/{id}")
////    public EventFullDto getById(@PathVariable Long id, HttpServletRequest request) {
////        String url = String.format("MAIN /events/{%s}", id);
////        ColoredCRUDLogger.logGet(url);
////        statsClient.postStats("ewm-main-service", request.getRequestURI(), request.getRemoteAddr(),
////                LocalDateTime.now());
////        EventFullDto result = eventService.getById(id);
////        ColoredCRUDLogger.logGetComplete(url, result.toString());
////        return result;
////    }
////    @DeleteMapping("/admin/users/{userId}")
////    @ResponseStatus(HttpStatus.NO_CONTENT)
////    public void delete(@PathVariable Long userId) {
////        String url = String.format("MAIN /admin/users/{%s}", userId);
////        ColoredCRUDLogger.logDelete(url);
////        userService.delete(userId);
////        ColoredCRUDLogger.logDeleteComplete(url);
////    }
//}
