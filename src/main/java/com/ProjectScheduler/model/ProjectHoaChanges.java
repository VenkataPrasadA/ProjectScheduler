package com.ProjectScheduler.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ProjectScheduler.model.ProjectHoaChanges;

import lombok.Data;

@Entity
@Table(name="project_hoa_changes")
@Data
public class ProjectHoaChanges {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long ChangesId;
	private String ProjectCode;
	private Long ProjectId;
	private Long TodayChanges;
	private Long WeeklyChanges;
	private Long MonthlyChanges;
	private String CreatedBy;
	private String CreatedDate;
	private int IsActive;
}
