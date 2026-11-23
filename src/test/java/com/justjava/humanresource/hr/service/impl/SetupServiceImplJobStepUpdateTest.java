package com.justjava.humanresource.hr.service.impl;

import com.justjava.humanresource.hr.dto.CreateJobGradeWithStepsCommand;
import com.justjava.humanresource.hr.entity.Department;
import com.justjava.humanresource.hr.entity.JobGrade;
import com.justjava.humanresource.hr.entity.JobStep;
import com.justjava.humanresource.hr.repository.DepartmentRepository;
import com.justjava.humanresource.hr.repository.EmployeeRepository;
import com.justjava.humanresource.hr.repository.JobGradeRepository;
import com.justjava.humanresource.hr.repository.JobStepRepository;
import com.justjava.humanresource.hr.repository.PayGroupRepository;
import com.justjava.humanresource.payroll.service.PayrollChangeOrchestrator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SetupServiceImplJobStepUpdateTest {

    @Mock private DepartmentRepository departmentRepository;
    @Mock private EmployeeRepository employeeRepository;
    @Mock private JobGradeRepository jobGradeRepository;
    @Mock private JobStepRepository jobStepRepository;
    @Mock private PayGroupRepository payGroupRepository;
    @Mock private PayrollChangeOrchestrator payrollChangeOrchestrator;

    private SetupServiceImpl setupService;
    private JobGrade grade;
    private Department department;
    private JobStep firstStep;
    private JobStep secondStep;

    @BeforeEach
    void setUp() {
        setupService = new SetupServiceImpl(
                departmentRepository,
                employeeRepository,
                jobGradeRepository,
                jobStepRepository,
                payGroupRepository,
                payrollChangeOrchestrator);

        department = new Department();
        department.setId(7L);
        grade = new JobGrade();
        grade.setId(4L);
        grade.setName("OVL");
        grade.setDepartment(department);

        firstStep = step(31L, "STEP-A", "1000");
        secondStep = step(32L, "STEP-B", "2000");
        when(jobGradeRepository.findById(4L)).thenReturn(Optional.of(grade));
        when(departmentRepository.findById(7L)).thenReturn(Optional.of(department));
        when(jobGradeRepository.save(grade)).thenReturn(grade);
        lenient().when(jobStepRepository.save(any(JobStep.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void updatesByStepIdWhenSubmittedRowsAreReorderedAndDoesNotRecalculateUnchangedSteps() {
        when(jobStepRepository.findByJobGrade(grade)).thenReturn(List.of(firstStep, secondStep));

        CreateJobGradeWithStepsCommand command = command(
                existingStep(32L, "STEP-B", "2000"),
                existingStep(31L, "STEP-A", "1000"),
                newStep("STEP-C", "3000"));

        var result = setupService.updateJobGradeWithSteps(4L, command);

        assertEquals(List.of(32L, 31L),
                result.getSteps().subList(0, 2).stream().map(step -> step.getId()).toList());
        assertNull(result.getSteps().get(2).getId());
        assertEquals("STEP-A", firstStep.getName());
        assertEquals(0, new BigDecimal("1000").compareTo(firstStep.getGrossSalary()));
        assertEquals("STEP-B", secondStep.getName());
        assertEquals(0, new BigDecimal("2000").compareTo(secondStep.getGrossSalary()));
        verify(jobStepRepository).save(firstStep);
        verify(jobStepRepository).save(secondStep);
        verify(jobStepRepository, never()).delete(any(JobStep.class));
        verifyNoInteractions(payrollChangeOrchestrator);
    }

    @Test
    void recalculatesOnlyEmployeesOnExistingStepWhosePayChanged() {
        when(jobStepRepository.findByJobGrade(grade)).thenReturn(List.of(firstStep, secondStep));

        CreateJobGradeWithStepsCommand command = command(
                existingStep(31L, "STEP-A", "1500"),
                existingStep(32L, "STEP-B", "2000"));

        setupService.updateJobGradeWithSteps(4L, command);

        verify(payrollChangeOrchestrator).recalculateForJobStep(
                org.mockito.ArgumentMatchers.eq(31L), any(java.time.LocalDate.class));
        verify(payrollChangeOrchestrator, never())
                .recalculateForJobStep(
                        org.mockito.ArgumentMatchers.eq(32L), any(java.time.LocalDate.class));
    }

    @Test
    void keepsRemovedStepRowForHistoryButDetachesItFromGrade() {
        when(jobStepRepository.findByJobGrade(grade)).thenReturn(List.of(firstStep, secondStep));
        when(employeeRepository.findByJobStep_Id(32L)).thenReturn(List.of());

        setupService.updateJobGradeWithSteps(4L, command(existingStep(31L, "STEP-A", "1000")));

        assertNull(secondStep.getJobGrade());
        verify(jobStepRepository).save(secondStep);
        verify(jobStepRepository, never()).delete(secondStep);
        verifyNoInteractions(payrollChangeOrchestrator);
    }

    @Test
    void refusesToRemoveStepWithCurrentlyAssignedEmployees() {
        when(jobStepRepository.findByJobGrade(grade)).thenReturn(List.of(firstStep, secondStep));
        when(employeeRepository.findByJobStep_Id(32L)).thenReturn(List.of(new com.justjava.humanresource.hr.entity.Employee()));

        assertThrows(IllegalArgumentException.class,
                () -> setupService.updateJobGradeWithSteps(
                        4L, command(existingStep(31L, "STEP-A", "1000"))));

        verify(jobStepRepository, never()).delete(any(JobStep.class));
        verify(payrollChangeOrchestrator, never()).recalculateForJobStep(any(), any());
    }

    @Test
    void rejectsStepIdThatDoesNotBelongToGrade() {
        when(jobStepRepository.findByJobGrade(grade)).thenReturn(List.of(firstStep, secondStep));

        assertThrows(IllegalArgumentException.class,
                () -> setupService.updateJobGradeWithSteps(
                        4L, command(existingStep(99L, "FOREIGN", "1000"))));

        verify(jobStepRepository, never()).save(any(JobStep.class));
    }

    private JobStep step(Long id, String name, String gross) {
        JobStep step = new JobStep();
        step.setId(id);
        step.setName(name);
        step.setGrossSalary(new BigDecimal(gross));
        step.setDepartment(department);
        step.setJobGrade(grade);
        return step;
    }

    private CreateJobGradeWithStepsCommand command(CreateJobGradeWithStepsCommand.JobStepCommand... steps) {
        CreateJobGradeWithStepsCommand command = new CreateJobGradeWithStepsCommand();
        command.setGradeName("OVL");
        command.setDepartmentId(7L);
        command.setSteps(List.of(steps));
        return command;
    }

    private CreateJobGradeWithStepsCommand.JobStepCommand existingStep(Long id, String name, String gross) {
        CreateJobGradeWithStepsCommand.JobStepCommand step =
                new CreateJobGradeWithStepsCommand.JobStepCommand();
        step.setId(id);
        step.setStepName(name);
        step.setGrossSalary(new BigDecimal(gross));
        return step;
    }

    private CreateJobGradeWithStepsCommand.JobStepCommand newStep(String name, String gross) {
        return existingStep(null, name, gross);
    }
}
