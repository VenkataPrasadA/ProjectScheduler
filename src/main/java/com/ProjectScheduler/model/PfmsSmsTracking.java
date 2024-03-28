package com.ProjectScheduler.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.ProjectScheduler.model.PfmsSmsTracking;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Data
@Table(name = "pfms_sms_track")
public class PfmsSmsTracking {


	@Id
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private long SmsTrackingId;
	private String SmsTrackingType;
	private long SmsExpectedCount;
	private long SmsSentCount;
	private String SmsSentStatus;
	private String CreatedDate;
	private String CreatedTime;
	private String SmsSentDateTime;
}
