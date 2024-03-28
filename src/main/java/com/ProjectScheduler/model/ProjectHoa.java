package com.ProjectScheduler.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table (name="project_hoa")
public class ProjectHoa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long ProjectHoaId;
	private Long ProjectId;
	private String ProjectCode;
	private Long BudgetHeadId;
	private Long ProjectSanctionId;
	private Long BudgetItemId;
	private String ReFe;
	private Double SanctionCost;
	private Double Expenditure;
	private Double OutCommitment;
	private Double Dipl;
	private Double Balance;
	private String CreatedBy;
	private String CreatedDate;
	private String ModifiedBy;
	private String ModifiedDate;
	private String LabCode;
}
