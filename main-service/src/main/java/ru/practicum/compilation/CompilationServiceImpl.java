//package ru.practicum.compilation;
//
//import ru.practicum.categorie.model.CategoryEntity;
//import ru.practicum.categorie.model.dto.CategoryDto;
//import ru.practicum.event.model.EventEntity;
//import ru.practicum.event.model.EventState;
//import ru.practicum.event.model.dto.EventFullDto;
//import ru.practicum.event.model.dto.NewEventDto;
//import ru.practicum.location.Location;
//import ru.practicum.location.LocationEntity;
//import ru.practicum.user.model.UserEntity;
//import ru.practicum.user.model.dto.UserShortDto;
//
//public class CompilationServiceImpl {
//    @Override
//    public EventFullDto create(Long userId, NewEventDto newEventDto) {
//        UserEntity user = userExistCheck(userId);
//        CategoryEntity category = categoryExistCheck(newEventDto.getCategory());
//        LocationEntity locationEntity = getOrCreateLocation(newEventDto.getLocation());
//        EventEntity entity = eventRepository.save(eventMapper.toEventEntity(newEventDto, category, user, locationEntity, EventState.PENDING));
//        CategoryDto categoryDto = categoryMapper.toCategoryDto(entity.getCategory());
//        UserShortDto userShortDto = userMapper.toUserShortDto(entity.getInitiator());
//        Location location = locationMapper.toLocation(entity.getLocation());
//        return eventMapper.toEventFullDto(entity, categoryDto, userShortDto, location, 0L, 0L);
//    }
//}
