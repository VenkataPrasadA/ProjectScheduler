package com.ProjectScheduler.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ProjectScheduler.model.PFMSCCMData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "pfms_ccm_data")
public class PFMSCCMData implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long CCMDataId; 
	private long ClusterId;
	private String LabCode;
	private long ProjectId;
	private String ProjectCode;
	private long BudgetHeadId;
	private String BudgetHeadDescription;
	private BigDecimal AllotmentCost;
	private BigDecimal Expenditure;
	private BigDecimal Balance;
	private BigDecimal Q1CashOutGo;
	private BigDecimal Q2CashOutGo;
	private BigDecimal Q3CashOutGo;
	private BigDecimal Q4CashOutGo;
	private BigDecimal Required;
	private String CreatedDate;
}
