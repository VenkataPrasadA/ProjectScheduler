package com.ProjectScheduler.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ProjectScheduler.model.PfmsCommitteSmsTrackingInsights;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@Table(name = "pfms_sms_committe_track_insights")
public class PfmsCommitteSmsTrackingInsights {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private long CommitteSmsTrackingInsightsId;
	private long CommitteSmsTrackingId;
    private long EmpId;
	private String Message;
	private String SmsPurpose;
	private String SmsStatus;
	private String SmsSentDate;
	private String CreatedDate;
}
