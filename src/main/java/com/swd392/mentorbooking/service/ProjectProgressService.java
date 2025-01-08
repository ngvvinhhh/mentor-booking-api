package com.swd392.mentorbooking.service;

import com.swd392.mentorbooking.dto.Response;
import com.swd392.mentorbooking.dto.projectprogress.*;
import com.swd392.mentorbooking.entity.*;
import com.swd392.mentorbooking.entity.Enum.AccountStatusEnum;
import com.swd392.mentorbooking.entity.Enum.TaskPriorityEnum;
import com.swd392.mentorbooking.entity.Enum.TaskStatusEnum;
import com.swd392.mentorbooking.exception.group.NotFoundException;
import com.swd392.mentorbooking.exception.service.CreateServiceException;
import com.swd392.mentorbooking.repository.*;
import com.swd392.mentorbooking.utils.AccountUtils;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectProgressService {

    @Autowired
    ProjectProgressRepository projectProgressRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    private AccountUtils accountUtils;

    @Autowired
    private ProgressColumnRepository progressColumnRepository;

    @Autowired
    private ProgressCardRepository progressCardRepository;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ProgressTaskRepository progressTaskRepository;

    public Response<List<ProjectProgress>> getAllProjectProgress() {
        List<ProjectProgress> projectProgressList = projectProgressRepository.findAllByIsDeletedFalse(Sort.by(Sort.Direction.ASC, "createdAt")); // Sort by creation date

        return new Response<>(200, "All Project Progress retrieved successfully!", projectProgressList);
    }

    public Response<ProjectProgress> createProjectProgress(@Valid ProjectProgressRequest projectProgressRequest) {
        Group existingGroup = groupRepository.findById(projectProgressRequest.getGroupId())
                .orElseThrow(() -> new NotFoundException("Group not found with id: " + projectProgressRequest.getGroupId()));

        ProjectProgress projectProgress = new ProjectProgress();
        projectProgress.setDescription(projectProgressRequest.getDescription());
        projectProgress.setGroup(existingGroup);
        projectProgress.setCreatedAt(LocalDateTime.now());
        projectProgress.setIsDeleted(false);

        try {
            projectProgressRepository.save(projectProgress);
        } catch (Exception e) {
            throw new CreateServiceException("There was something wrong when creating the Project Progress, please try again...");
        }

        return new Response<>(200, "Project Progress updated successfully!", projectProgress);

    }

    public Response<ProjectProgress> updateProjectProgress(Long id, @Valid UpdateProgressRequest projectProgressRequest) {
        // Find ProjectProgress by ID
        ProjectProgress existingProjectProgress = projectProgressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ProjectProgress not found with id: " + id));

        // Update
        existingProjectProgress.setDescription(projectProgressRequest.getDescription());

        // Save
        try {
            projectProgressRepository.save(existingProjectProgress);
        } catch (Exception e) {
            throw new CreateServiceException("There was something wrong when updating the Project Progress, please try again...");
        }

        return new Response<>(200, "Project Progress updated successfully!", existingProjectProgress);
    }

    public Response<Void> deleteProjectProgress(Long id) {

        ProjectProgress existingProjectProgress = projectProgressRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ProjectProgress not found with id: " + id));

        existingProjectProgress.setIsDeleted(true);

        try {
            projectProgressRepository.save(existingProjectProgress);
        } catch (Exception e) {
            throw new CreateServiceException("There was something wrong when deleting the Project Progress, please try again...");
        }

        return new Response<>(200, "Project Progress marked as deleted successfully!", null);
    }

    public Response<ColumnCreateRequestDTO> createColumn(Long projectProgressId) {

        Account account = accountUtils.getCurrentAccount();

        if (account == null) {
            return new Response<>(401, "Please login first!", null);
        }
        if (account.getStatus().equals(AccountStatusEnum.UNVERIFIED)) {
            return new Response<>(403, "Please verify your account first!", null);
        }
        if (account.getIsDeleted().equals(true)) {
            return new Response<>(410, "Your account has been deleted, please contact support for more information!", null);
        }

        ProjectProgress projectProgress = projectProgressRepository.findById(projectProgressId).orElse(null);

        if (projectProgress == null) {
            return new Response<>(404, "Project Progress not found with id: " + projectProgressId, null);
        }

        ProgressColumn progressColumn = new ProgressColumn();
        progressColumn.setProjectProgress(projectProgress);
        progressColumn.setTitle("Column " + projectProgress.getProgressColumns().size() + 1);
        progressColumn.setOrderIndex(projectProgress.getProgressColumns().size() + 1);
        progressColumnRepository.save(progressColumn);

        ColumnCreateRequestDTO columnCreateRequestDTO = ColumnCreateRequestDTO.builder()
                .id(progressColumn.getId())
                .projectProgressId(projectProgressId)
                .title(progressColumn.getTitle())
                .orderIndex(progressColumn.getOrderIndex())
                .build();

        return new Response<>(200, "Column created successfully!", columnCreateRequestDTO);
    }

    public Response<ColumnUpdateRequestDTO> updateColumn(Long columnId, ColumnUpdateRequestDTO columnUpdateRequestDTO) {

        Account account = accountUtils.getCurrentAccount();

        if (account == null) {
            return new Response<>(401, "Please login first!", null);
        }
        if (account.getStatus().equals(AccountStatusEnum.UNVERIFIED)) {
            return new Response<>(403, "Please verify your account first!", null);
        }
        if (account.getIsDeleted().equals(true)) {
            return new Response<>(410, "Your account has been deleted, please contact support for more information!", null);
        }

        ProgressColumn progressColumn = progressColumnRepository.findById(columnId).orElse(null);
        if (progressColumn == null) {
            return new Response<>(404, "Column not found with id: " + columnId, null);
        }

        if (columnUpdateRequestDTO.getTitle() != null) {
            progressColumn.setTitle(columnUpdateRequestDTO.getTitle());
        }
        if (columnUpdateRequestDTO.getOrderIndex() != null) {
            progressColumn.setOrderIndex(columnUpdateRequestDTO.getOrderIndex());
        }

        progressColumnRepository.save(progressColumn);

        return new Response<>(200, "Update column successfully!", null);
    }

    public Response<String> deleteColumn(Long columnId) {

        Account account = accountUtils.getCurrentAccount();

        if (account == null) {
            return new Response<>(401, "Please login first!", null);
        }
        if (account.getStatus().equals(AccountStatusEnum.UNVERIFIED)) {
            return new Response<>(403, "Please verify your account first!", null);
        }
        if (account.getIsDeleted().equals(true)) {
            return new Response<>(410, "Your account has been deleted, please contact support for more information!", null);
        }

        ProgressColumn progressColumn = progressColumnRepository.findById(columnId).orElse(null);
        if (progressColumn == null) {
            return new Response<>(404, "Column not found with id: " + columnId, null);
        }

        progressColumnRepository.delete(progressColumn);

        return new Response<>(200, "Deleted column successfully!", null);
    }

    public Response<ProgressCard> createCard(Long columnId) {

        Account account = accountUtils.getCurrentAccount();

        if (account == null) {
            return new Response<>(401, "Please login first!", null);
        }
        if (account.getStatus().equals(AccountStatusEnum.UNVERIFIED)) {
            return new Response<>(403, "Please verify your account first!", null);
        }
        if (account.getIsDeleted().equals(true)) {
            return new Response<>(410, "Your account has been deleted, please contact support for more information!", null);
        }

        ProgressColumn progressColumn = progressColumnRepository.findById(columnId).orElse(null);
        if (progressColumn == null) {
            return new Response<>(404, "No column found to create card!", null);
        }

        try {
            ProgressCard progressCard = ProgressCard.builder()
                    .progressColumn(progressColumn)
                    .title("Random card")
                    .description("Random card description")
                    .cover(null)
                    .orderIndex(progressColumn.getProgressCards().size() + 1)
                    .account(null)
                    .build();
            return new Response<>(200, "Card created successfully!", progressCardRepository.save(progressCard));
        } catch (Exception e) {
            return new Response<>(500, "Something went wrong while creating card!", null);
        }
    }

    public Response<ProgressCard> updateCard(Long cardId, UpdateCardRequestDTO updateCardRequestDTO) {

        Account account = accountUtils.getCurrentAccount();

        if (account == null) {
            return new Response<>(401, "Please login first!", null);
        }
        if (account.getStatus().equals(AccountStatusEnum.UNVERIFIED)) {
            return new Response<>(403, "Please verify your account first!", null);
        }
        if (account.getIsDeleted().equals(true)) {
            return new Response<>(410, "Your account has been deleted, please contact support for more information!", null);
        }

        if (cardId == null) {
            return new Response<>(400, "Card ID is required", null);
        }

        ProgressCard progressCard = progressCardRepository.findById(cardId).orElse(null);
        if (progressCard == null) {
            return new Response<>(404, "Card not found with id: " + cardId, null);
        }

        // Chỉ cập nhật các trường nếu chúng khác null
        if (updateCardRequestDTO.getTitle() != null) {
            progressCard.setTitle(updateCardRequestDTO.getTitle());
        }
        if (updateCardRequestDTO.getDescription() != null) {
            progressCard.setDescription(updateCardRequestDTO.getDescription());
        }
        if (updateCardRequestDTO.getCover() != null) {
            progressCard.setCover(updateCardRequestDTO.getCover());
        }
        if (updateCardRequestDTO.getOrderIndex() != null) {
            progressCard.setOrderIndex(updateCardRequestDTO.getOrderIndex());
        }
        if (updateCardRequestDTO.getAccountId() != null) {
            progressCard.setAccount(accountRepository.findById(updateCardRequestDTO.getAccountId()).orElse(null));
        }

        try {
            return new Response<>(200, "Card updated successfully!", progressCardRepository.save(progressCard));
        } catch (Exception e) {
            return new Response<>(500, "Something went wrong while updating the card!", null);

        }

    }

    public Response<String> deleteCard(Long cardId) {

        Account account = accountUtils.getCurrentAccount();

        if (account == null) {
            return new Response<>(401, "Please login first!", null);
        }
        if (account.getStatus().equals(AccountStatusEnum.UNVERIFIED)) {
            return new Response<>(403, "Please verify your account first!", null);
        }
        if (account.getIsDeleted().equals(true)) {
            return new Response<>(410, "Your account has been deleted, please contact support for more information!", null);
        }

        ProgressCard progressCard = progressCardRepository.findById(cardId).orElse(null);
        if (progressCard == null) {
            return new Response<>(404, "Card not found with id: " + cardId, null);
        }

        progressCardRepository.delete(progressCard);

        return new Response<>(200, "Deleted card successfully!", null);
    }

    public Response<ProjectProgressDTO> getMyProjectProgress() {
        Account account = accountUtils.getCurrentAccount();
        if (account == null) {
            return new Response<>(401, "Please login first!", null);
        }
        if (account.getStatus().equals(AccountStatusEnum.UNVERIFIED)) {
            return new Response<>(403, "Please verify your account first!", null);
        }
        if (account.getIsDeleted().equals(true)) {
            return new Response<>(410, "Your account has been deleted, please contact support for more information!", null);
        }

        Group group = groupRepository.findByStudentsContaining(account).orElse(null);
        if (group == null) {
            return new Response<>(401, "You don't belong to any group!", null);
        }

        ProjectProgress projectProgress = projectProgressRepository.findByGroup(group).orElse(null);
        if (projectProgress == null) {
            return new Response<>(401, "Your group doesn't have any project!", null);
        }
        ProjectProgressDTO response = convertToDTO(projectProgress);
        return new Response<>(200, "Retrieved project progress successfully!", response);
    }

    public ProjectProgressDTO convertToDTO(ProjectProgress projectProgress) {
        ProjectProgressDTO projectProgressDTO = new ProjectProgressDTO();

        // Thiết lập thông tin cơ bản
        projectProgressDTO.setId(projectProgress.getId().toString());
        projectProgressDTO.setTitle(projectProgress.getDescription()); // Giả sử title = description
        projectProgressDTO.setDescription(projectProgress.getDescription());
        projectProgressDTO.setType("Some Type");  // Thêm thông tin loại nếu cần

        // Thiết lập thứ tự các cột
        projectProgressDTO.setColumnOrderIds(projectProgress.getProgressColumns().stream()
                .sorted((col1, col2) -> col1.getOrderIndex().compareTo(col2.getOrderIndex()))
                .map(col -> col.getId().toString())
                .collect(Collectors.toList()));

        // Thiết lập các cột
        List<ProjectColumnDTO> columnDTOs = projectProgress.getProgressColumns().stream()
                .map(this::convertToColumnDTO)
                .collect(Collectors.toList());

        projectProgressDTO.setColumns(columnDTOs);

        return projectProgressDTO;
    }

    public ProjectColumnDTO convertToColumnDTO(ProgressColumn progressColumn) {
        ProjectColumnDTO columnDTO = new ProjectColumnDTO();

        columnDTO.setId(progressColumn.getId().toString());
        columnDTO.setBoardId(progressColumn.getId().toString()); // Giả sử bạn cần ID dưới dạng String
        columnDTO.setTitle(progressColumn.getTitle());

        // Thiết lập thứ tự các thẻ (cards)
        columnDTO.setCardOrderIds(progressColumn.getProgressCards().stream()
                .sorted((card1, card2) -> card1.getOrderIndex().compareTo(card2.getOrderIndex()))
                .map(card -> card.getId().toString())
                .collect(Collectors.toList()));

        // Thiết lập các thẻ
        List<ProgressCardDTO> cardDTOs = progressColumn.getProgressCards().stream()
                .map(this::convertToCardDTO)
                .collect(Collectors.toList());

        columnDTO.setCards(cardDTOs);

        return columnDTO;
    }

    public ProgressCardDTO convertToCardDTO(ProgressCard progressCard) {
        ProgressCardDTO cardDTO = new ProgressCardDTO();

        cardDTO.setCardId(progressCard.getId());
        cardDTO.setProjectProgressId(progressCard.getProgressColumn().getProjectProgress().getId().toString());
        cardDTO.setProjectColumnId(progressCard.getProgressColumn().getId().toString());
        cardDTO.setTitle(progressCard.getTitle());
        cardDTO.setDescription(progressCard.getDescription());
        cardDTO.setCover(progressCard.getCover());

        // Lấy memberIds từ account nếu cần
        if (progressCard.getAccount() != null) {
            cardDTO.setMemberIds(List.of(progressCard.getAccount().getId().toString()));
        }

        return cardDTO;
    }

    public Response<ProgressTaskResponseDTO> checkValidateAccount() {
        Account account = accountUtils.getCurrentAccount();
        if (account == null) {
            return new Response<>(401, "Please login first!", null);
        }
        if (account.getStatus().equals(AccountStatusEnum.UNVERIFIED)) {
            return new Response<>(403, "Please verify your account first!", null);
        }
        if (account.getIsDeleted().equals(true)) {
            return new Response<>(403, "Your account have been deleted!", null);
        }
        if (!account.isAccountNonExpired()) {
            return new Response<>(403, "Your account has been expired, please login again!", null);
        }
        return new Response<>(200, "OK", null);
    }

    public Response<ProgressTaskResponseDTO> createTask() {
        Response<ProgressTaskResponseDTO> response = checkValidateAccount();
        if (response.getCode() != 200) {
            return response;
        }
        Account account = accountUtils.getCurrentAccount();

        //Find student's group
        Group group = groupRepository.findByStudentsContaining(account).orElse(null);
        if (group == null) {
            return new Response<>(403, "You don't belong to any group!", null);
        }

        //Find group project progress
        ProjectProgress projectProgress = projectProgressRepository.findByGroup(group).orElse(null);
        if (projectProgress == null) {
            return new Response<>(400, "Something went wrong, please try again later...!", null);
        }

        int keyNumber = projectProgress.getProgressTasks().size() + 1;
        String key = "NBT - " + keyNumber;
        ProgressTask progressTask = ProgressTask.builder()
                    .projectProgress(projectProgress)
                    .taskKey(key)
                    .taskSummary("")
                    .taskStatus(TaskStatusEnum.TO_DO)
                    .dueDate(LocalDate.now())
                    .taskPriority(TaskPriorityEnum.Low)
                    .assignee(null)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .isDeleted(false)
                    .build();
        try {
            progressTaskRepository.save(progressTask);
        } catch (Exception e) {
            return new Response<>(400, "Something went wrong when saving the task, please try again later...!", null);
        }
        ProgressTaskResponseDTO data = taskResponse(progressTask);
        return new Response<>(200, "Task created successfully!", data);
    }

    public Response<ProgressTaskResponseDTO> updateTaskStatus(Long taskId, UpdateTaskStatusDTO updateTaskStatusDTO) {
        Response<ProgressTaskResponseDTO> response = checkValidateAccount();
        if (response.getCode() != 200) {
            return response;
        }
        Account account = accountUtils.getCurrentAccount();

        //Find student's group
        Group group = groupRepository.findByStudentsContaining(account).orElse(null);
        if (group == null) {
            return new Response<>(403, "You don't belong to any group!", null);
        }

        //Find group project progress
        ProjectProgress projectProgress = projectProgressRepository.findByGroup(group).orElse(null);
        if (projectProgress == null) {
            return new Response<>(400, "Something went wrong, please try again later...!", null);
        }

        //Find task
        ProgressTask progressTask = progressTaskRepository.findById(taskId).orElse(null);
        progressTask.setTaskStatus(updateTaskStatusDTO.getTaskStatus());
        try {
            progressTaskRepository.save(progressTask);
        } catch (Exception e) {
            return new Response<>(400, "Something went wrong when updating the task, please try again later...!", null);
        }
        ProgressTaskResponseDTO data = taskResponse(progressTask);
        return new Response<>(200, "Task updated successfully!", data);
    }

    public Response<ProgressTaskResponseDTO> updateTaskAssginee(Long taskId, UpdateTaskAssigneeRequestDTO updateTaskAssigneeRequestDTO) {
        Response<ProgressTaskResponseDTO> response = checkValidateAccount();
        if (response.getCode() != 200) {
            return response;
        }
        Account account = accountUtils.getCurrentAccount();

        //Find student's group
        Group group = groupRepository.findByStudentsContaining(account).orElse(null);
        if (group == null) {
            return new Response<>(403, "You don't belong to any group!", null);
        }

        //Find group project progress
        ProjectProgress projectProgress = projectProgressRepository.findByGroup(group).orElse(null);
        if (projectProgress == null) {
            return new Response<>(400, "Something went wrong, please try again later...!", null);
        }

        //Find task
        ProgressTask progressTask = progressTaskRepository.findById(taskId).orElse(null);
        if (progressTask == null) {
            return new Response<>(400, "There is no task with such ID!", null);
        }

        Account assignee = accountRepository.findById(updateTaskAssigneeRequestDTO.getAssigneeId()).orElse(null);
        if (assignee == null) {
            return new Response<>(400, "There is no account with such ID to assign task!", null);
        }

        progressTask.setAssignee(assignee);
        try {
            progressTaskRepository.save(progressTask);
        } catch (Exception e) {
            return new Response<>(400, "Something went wrong when updating the task, please try again later...!", null);
        }
        ProgressTaskResponseDTO data = taskResponse(progressTask);
        return new Response<>(200, "Task updated successfully!", data);
    }

    public Response<ProgressTaskResponseDTO> updateTaskDueDate(Long taskId, UpdateTaskDueDateRequestDTO updateTaskDueDateRequestDTO) {
        Response<ProgressTaskResponseDTO> response = checkValidateAccount();
        if (response.getCode() != 200) {
            return response;
        }
        Account account = accountUtils.getCurrentAccount();

        //Find student's group
        Group group = groupRepository.findByStudentsContaining(account).orElse(null);
        if (group == null) {
            return new Response<>(403, "You don't belong to any group!", null);
        }

        //Find group project progress
        ProjectProgress projectProgress = projectProgressRepository.findByGroup(group).orElse(null);
        if (projectProgress == null) {
            return new Response<>(400, "Something went wrong, please try again later...!", null);
        }

        //Find task
        ProgressTask progressTask = progressTaskRepository.findById(taskId).orElse(null);
        if (progressTask == null) {
            return new Response<>(400, "There is no task with such ID!", null);
        }
        if (updateTaskDueDateRequestDTO.getDueDate() != null) {
            progressTask.setDueDate(updateTaskDueDateRequestDTO.getDueDate());
        }
        try {
            progressTaskRepository.save(progressTask);
        } catch (Exception e) {
            return new Response<>(400, "Something went wrong when updating the task, please try again later...!", null);
        }
        ProgressTaskResponseDTO data = taskResponse(progressTask);
        return new Response<>(200, "Task updated successfully!", data);
    }

    public Response<ProgressTaskResponseDTO> updateTaskPriority(Long taskId, TaskPriorityEnum taskPriorityEnum) {
        Response<ProgressTaskResponseDTO> response = checkValidateAccount();
        if (response.getCode() != 200) {
            return response;
        }
        Account account = accountUtils.getCurrentAccount();

        //Find student's group
        Group group = groupRepository.findByStudentsContaining(account).orElse(null);
        if (group == null) {
            return new Response<>(403, "You don't belong to any group!", null);
        }

        //Find group project progress
        ProjectProgress projectProgress = projectProgressRepository.findByGroup(group).orElse(null);
        if (projectProgress == null) {
            return new Response<>(400, "Something went wrong, please try again later...!", null);
        }

        //Find task
        ProgressTask progressTask = progressTaskRepository.findById(taskId).orElse(null);
        if (progressTask == null) {
            return new Response<>(400, "There is no task with such ID!", null);
        }
        if (taskPriorityEnum != null) {
            progressTask.setTaskPriority(taskPriorityEnum);
        }
        try {
            progressTaskRepository.save(progressTask);
        } catch (Exception e) {
            return new Response<>(400, "Something went wrong when updating the task, please try again later...!", null);
        }
        ProgressTaskResponseDTO data = taskResponse(progressTask);
        return new Response<>(200, "Task updated successfully!", data);
    }

    public Response<ProgressTaskResponseDTO> deleteTask(Long taskId) {
        Response<ProgressTaskResponseDTO> response = checkValidateAccount();
        if (response.getCode() != 200) {
            return response;
        }
        Account account = accountUtils.getCurrentAccount();

        //Find student's group
        Group group = groupRepository.findByStudentsContaining(account).orElse(null);
        if (group == null) {
            return new Response<>(403, "You don't belong to any group!", null);
        }

        //Find group project progress
        ProjectProgress projectProgress = projectProgressRepository.findByGroup(group).orElse(null);
        if (projectProgress == null) {
            return new Response<>(400, "Something went wrong, please try again later...!", null);
        }

        //Find task
        ProgressTask progressTask = progressTaskRepository.findById(taskId).orElse(null);
        if (progressTask == null) {
            return new Response<>(400, "There is no task with such ID!", null);
        }
        try {
            progressTaskRepository.delete(progressTask);
        } catch (Exception e) {
            return new Response<>(400, "Something went wrong when deleting the task, please try again later...!", null);
        }
        return new Response<>(200, "Task deleted successfully!", null);
    }

    private ProgressTaskResponseDTO taskResponse(ProgressTask progressTask) {
        ProgressTaskResponseDTO.ProgressTaskResponseDTOBuilder builder = ProgressTaskResponseDTO.builder()
                .taskId(progressTask.getId())
                .projectProgressId(progressTask.getProjectProgress().getId())
                .taskKey(progressTask.getTaskKey())
                .taskSummary(progressTask.getTaskSummary())
                .taskStatus(progressTask.getTaskStatus())
                .dueDate(progressTask.getDueDate())
                .taskPriority(progressTask.getTaskPriority());

        if (progressTask.getAssignee() != null) {
            builder.assigneeName(progressTask.getAssignee().getName())
                    .assigneeAvatar(progressTask.getAssignee().getAvatar());
        }

        return builder.build();
    }

    public Response<ProjectProgressTasksViewDTO> viewGroupTasks() {
        Account account = accountUtils.getCurrentAccount();
        if (account == null) {
            return new Response<>(401, "Please login first!", null);
        }
        if (account.getStatus().equals(AccountStatusEnum.UNVERIFIED)) {
            return new Response<>(403, "Please verify your account first!", null);
        }
        if (account.getIsDeleted().equals(true)) {
            return new Response<>(403, "Your account have been deleted!", null);
        }
        if (!account.isAccountNonExpired()) {
            return new Response<>(403, "Your account has been expired, please login again!", null);
        }

        //Find student's group
        Group group = groupRepository.findByStudentsContaining(account).orElse(null);
        if (group == null) {
            return new Response<>(403, "You don't belong to any group!", null);
        }

        //Find group project progress
        ProjectProgress projectProgress = projectProgressRepository.findByGroup(group).orElse(null);
        if (projectProgress == null) {
            return new Response<>(400, "Something went wrong, please try again later...!", null);
        }

        List<ProgressTask> progressTasks = progressTaskRepository.findByProjectProgressOrderByTaskKeyAsc(projectProgress);
        ProjectProgressTasksViewDTO data = new ProjectProgressTasksViewDTO();
        data.setProjectProgressId(projectProgress.getId());
        data.setGroupId(group.getId());
        for (Account student : group.getStudents()) {
            GroupStudentList groupStudentList = GroupStudentList.builder()
                    .accountId(student.getId())
                    .accountName(student.getName())
                    .accountAvatar(student.getAvatar())
                    .accountEmail(student.getEmail())
                    .build();
            data.getStudentList().add(groupStudentList);
        }
        for (ProgressTask progressTask : progressTasks) {
            ProgressTaskResponseDTO progressTaskResponse = taskResponse(progressTask);
            data.getTaskList().add(progressTaskResponse);
        }
        return new Response<>(200, "Task viewed successfully!", data);
    }

    public Response<ProgressTaskResponseDTO> updateTaskSummary(Long taskId, UpdateSummaryRequestDTO updateSummaryRequestDTO) {
        Response<ProgressTaskResponseDTO> response = checkValidateAccount();
        if (response.getCode() != 200) {
            return response;
        }
        Account account = accountUtils.getCurrentAccount();

        //Find student's group
        Group group = groupRepository.findByStudentsContaining(account).orElse(null);
        if (group == null) {
            return new Response<>(403, "You don't belong to any group!", null);
        }

        //Find group project progress
        ProjectProgress projectProgress = projectProgressRepository.findByGroup(group).orElse(null);
        if (projectProgress == null) {
            return new Response<>(400, "Something went wrong, please try again later...!", null);
        }

        //Find task
        ProgressTask progressTask = progressTaskRepository.findById(taskId).orElse(null);
        if (progressTask == null) {
            return new Response<>(400, "There is no task with such ID!", null);
        }
        if (updateSummaryRequestDTO.getSummary() != null) {
            progressTask.setTaskSummary(updateSummaryRequestDTO.getSummary());
        }
        try {
            progressTaskRepository.save(progressTask);
        } catch (Exception e) {
            return new Response<>(400, "Something went wrong when updating the task, please try again later...!", null);
        }
        ProgressTaskResponseDTO data = taskResponse(progressTask);
        return new Response<>(200, "Task updated successfully!", data);
    }
}
