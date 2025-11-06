package com.kidekdev.albummanager.database.model.tag;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class GlobalTagGroupsDto {

    private static final ObjectMapper YAML_MAPPER;

    static {
        YAML_MAPPER = new ObjectMapper(new YAMLFactory());
        YAML_MAPPER.findAndRegisterModules();
    }

    private final Map<String, String> tagGroups = new LinkedHashMap<>();

    public GlobalTagGroupsDto() {
    }

    public GlobalTagGroupsDto(Map<String, String> tagGroups) {
        setTagGroups(tagGroups);
    }

    public Map<String, String> getTagGroups() {
        return Map.copyOf(tagGroups);
    }

    @JsonProperty("tagGroups")
    public final void setTagGroups(Map<String, String> tagGroups) {
        this.tagGroups.clear();
        if (tagGroups != null) {
            this.tagGroups.putAll(tagGroups);
        }
    }

    @JsonAnySetter
    public void addTagGroup(String tagName, String groupName) {
        if (tagName == null || groupName == null) {
            return;
        }
        this.tagGroups.put(tagName, groupName);
    }

    public static GlobalTagGroupsDto load(Path rootPath) {
        Path tagGroupsFile = rootPath.resolve("global").resolve("taggroups.yaml");

        if (!Files.exists(tagGroupsFile)) {
            return new GlobalTagGroupsDto(Map.of());
        }

        try {
            GlobalTagGroupsDto dto = YAML_MAPPER.readValue(tagGroupsFile.toFile(), GlobalTagGroupsDto.class);
            return new GlobalTagGroupsDto(dto.tagGroups);
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "Failed to load tag groups from " + tagGroupsFile.toAbsolutePath(),
                    e
            );
        }
    }
}
