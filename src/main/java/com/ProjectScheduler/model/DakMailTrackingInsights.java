package com.ProjectScheduler.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

	@NoArgsConstructor
	@Data
	@Entity
	@Table(name="dak_mail_track_insights")
	public class DakMailTrackingInsights {
		@Id
		@GeneratedValue(strategy= GenerationType.IDENTITY)
		private long MailTrackingInsightsId;
		private long MailTrackingId;
	    private long EmpId;
		private String DakNos;
		private String MailPurpose;
		private String MailStatus;
		private String MailSentDate;
		private String CreatedDate;
	}