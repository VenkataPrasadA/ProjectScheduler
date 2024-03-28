package com.ProjectScheduler.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ProjectScheduler.model.PfmsCommitteSmsTracking;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Data
@Table(name = "pfms_sms_committe_track")
public class PfmsCommitteSmsTracking {

	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private long CommitteSmsTrackingId;
	private String SmsTrackingType;
	private long SmsExpectedCount;
	private long SmsSentCount;
	private String SmsSentStatus;
	private String CreatedDate;
	private String CreatedTime;
	private String SmsSentDateTime;
}
