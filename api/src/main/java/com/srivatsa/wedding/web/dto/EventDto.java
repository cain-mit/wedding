package com.srivatsa.wedding.web.dto;

import lombok.*;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventDto {
    private Integer id;
    private String name;
    private OffsetDateTime startTs;
    private OffsetDateTime endTs;
    private String location;
    private String description;
}
