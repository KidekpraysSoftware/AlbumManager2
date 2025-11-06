package com.kidekdev.albummanager.database.service;

import com.kidekdev.albummanager.database.model.journal.JournalDto;
import com.kidekdev.albummanager.database.model.journal.JournalMessageDto;
import com.kidekdev.albummanager.database.model.journal.JournalMessageType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JournalDatabaseServiceTest extends BaseDatabaseServiceTest {

    private static final UUID JOURNAL_ID = UUID.fromString("9fd4b8c8-9a1b-43f5-9b37-9a1c5f782b52");
    private static final long EXISTING_MESSAGE_TIME = 1762201875123L;

    @Test
    void getJournalByIdReturnsExampleEntries() {
        Optional<JournalDto> maybeJournal = databaseService.getJournalById(JOURNAL_ID);

        assertThat(maybeJournal).isPresent();
        JournalDto journal = maybeJournal.orElseThrow();
        assertThat(journal.getEntries()).hasSize(3);
        assertThat(journal.getEntries())
                .containsKey(String.valueOf(EXISTING_MESSAGE_TIME));
    }

    @Test
    void addNewMessageAppendsEntryToJournal() {
        JournalMessageDto newMessage = new JournalMessageDto(
                JournalMessageType.USER,
                "Новая заметка",
                List.of(),
                null
        );
        JournalDto journal = databaseService.getJournalById(JOURNAL_ID).orElseThrow();
        int originalSize = journal.getEntries() != null ? journal.getEntries().size() : 0;

        SaveResult result = databaseService.addNewMessage(JOURNAL_ID, newMessage);

        assertThat(result.isSuccess()).isTrue();
        JournalDto updatedJournal = databaseService.getJournalById(JOURNAL_ID).orElseThrow();
        assertThat(updatedJournal.getEntries()).hasSize(originalSize + 1);
        assertThat(updatedJournal.getEntries().values()).contains(newMessage);
    }

    @Test
    void editMessageUpdatesStoredValue() {
        JournalMessageDto updatedMessage = new JournalMessageDto(
                JournalMessageType.SYSTEM,
                "Обновленное сообщение",
                List.of(UUID.randomUUID()),
                null
        );

        SaveResult result = databaseService.editMessage(JOURNAL_ID, EXISTING_MESSAGE_TIME, updatedMessage);

        assertThat(result.isSuccess()).isTrue();
        Map<String, JournalMessageDto> entries = databaseService.getJournalById(JOURNAL_ID)
                .map(JournalDto::getEntries)
                .orElse(Map.of());
        assertThat(entries.get(String.valueOf(EXISTING_MESSAGE_TIME))).isEqualTo(updatedMessage);
    }

    @Test
    void deleteMessageRemovesEntry() {
        JournalDto journal = databaseService.getJournalById(JOURNAL_ID).orElseThrow();
        int originalSize = journal.getEntries().size();

        SaveResult result = databaseService.deleteMessage(JOURNAL_ID, EXISTING_MESSAGE_TIME);

        assertThat(result.isSuccess()).isTrue();
        Map<String, JournalMessageDto> entries = databaseService.getJournalById(JOURNAL_ID)
                .map(JournalDto::getEntries)
                .orElseThrow();
        assertThat(entries).hasSize(originalSize - 1);
        assertThat(entries).doesNotContainKey(String.valueOf(EXISTING_MESSAGE_TIME));
    }
}
