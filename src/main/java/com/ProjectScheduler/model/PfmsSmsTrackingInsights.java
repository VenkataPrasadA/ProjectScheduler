package com.ProjectScheduler.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name ="pfms_sms_track_insights")
public class PfmsSmsTrackingInsights {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private long SmsTrackingInsightsId;
	private long SmsTrackingId;
    private long EmpId;
	private String Message;
	private String SmsPurpose;
	private String SmsStatus;
	private long ActionItemP;
	private long ActionItemTP;
	private long ActionItemDP;
	private long MilestoneActionP;
	private long MilestoneActionTP;
	private long MilestoneActionDP;
	private long MeetingActionP;
	private long MeetingActionTP;
	private long MeetingActionDP;
	private String SmsSentDate;
	private String CreatedDate;
}
