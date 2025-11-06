package com.kidekdev.albummanager.database.model.view;

import com.kidekdev.albummanager.database.model.common.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewFilterDto {
    private List<String> tags;
    private List<ResourceType> types;
    private List<String> extensions;
    private List<String> nameSubstrings;
}
