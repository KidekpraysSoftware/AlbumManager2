package com.kidekdev.albummanager.database.model.journal;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class JournalDto {

    @EqualsAndHashCode.Include
    private UUID id;
    private Boolean isActive;
    private UUID linkedBy;
    @JsonAnySetter
    private Map<String, JournalMessageDto> entries;

}
