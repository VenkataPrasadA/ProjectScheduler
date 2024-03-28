package com.ProjectScheduler.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ProjectScheduler.model.DakSmsTrackingInsights;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Data
@Table(name ="dak_sms_track_insights")
public class DakSmsTrackingInsights {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private long SmsTrackingInsightsId;
	private long SmsTrackingId;
    private long EmpId;
	private String Message;
	private String SmsPurpose;
	private String SmsStatus;
	private long DakPendingCount;
	private long DakUrgentCount;
	private long DakTodayPending;
	private long DakDelayCount;
	private String SmsSentDate;
	private String CreatedDate;
}