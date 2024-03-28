package com.ProjectScheduler.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ProjectScheduler.model.ProjectHealth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "project_health")
public class ProjectHealth {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ProjectHealthId;
	private String LabCode;
	private long ProjectId;
	private String ProjectShortName;
	private String ProjectCode;
	private String PDC;
	private String SanctionDate;
	private Long PMRCHeld;
	private Long PMRCPending;
	private Long PMRCTotal;
	private long PMRCTotalToBeHeld;
	private Long EBHeld;
	private Long EBPending;
	private Long EBTotal;
	private long EBTotalToBeHeld;
	private Long MilPending;
	private Long MilDelayed;
	private Long MilCompleted;
	private Long ActionPending;
	private Long ActionForwarded;
	private Long ActionDelayed;
	private Long ActionCompleted;
	private Long RiskCompleted;
	private Long RiskPending;
	private Double Expenditure;
	private Double OutCommitment;
	private Double Dipl;
	private Double Balance;
	private String ProjectType;
	private String EndUser;
	private Long TodayChanges;
	private Long WeeklyChanges;
	private Long MonthlyChanges;
	private String CreatedBy;
	private String CreatedDate;
	private String ModifiedBy;
	private String ModifiedDate;
}
