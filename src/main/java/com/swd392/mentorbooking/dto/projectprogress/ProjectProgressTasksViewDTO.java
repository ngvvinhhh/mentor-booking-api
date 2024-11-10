package com.swd392.mentorbooking.dto.projectprogress;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectProgressTasksViewDTO {
    private long projectProgressId;
    private long groupId;
    private List<GroupStudentList> studentList = new ArrayList<>();
    private List<ProgressTaskResponseDTO> taskList = new ArrayList<>();
}
