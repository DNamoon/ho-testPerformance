package com.starter.performance.controller.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequestDto {

    @NotBlank
    @Pattern(regexp = "^[1-2]$")  //정규식 적용 위해 String으로 타입 변경
    private String reservedTicketNum;

}
