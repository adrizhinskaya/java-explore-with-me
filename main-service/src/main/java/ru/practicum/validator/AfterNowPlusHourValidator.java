//package ru.practicum.validator;
//
//import javax.validation.ConstraintValidator;
//import javax.validation.ConstraintValidatorContext;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//public class AfterNowPlusHourValidator implements ConstraintValidator<AfterNowPlusHour, LocalDateTime> {
//    @Override
//    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
//        return value.isAfter(LocalDateTime.now().plusHours(1));
//
//    }
//}
