package com.kidekdev.albummanager.database.model.journal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JournalMessageDto {

    private JournalMessageType type;
    private String text;
    private List<UUID> attachments;
    private Long commentOn;
}
