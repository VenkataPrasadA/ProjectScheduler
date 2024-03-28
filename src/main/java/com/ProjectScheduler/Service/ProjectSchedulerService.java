package com.ProjectScheduler.Service;

import java.util.List;

import com.ProjectScheduler.dto.MailConfigurationDto;
import com.ProjectScheduler.model.ProjectHoa;
import com.ProjectScheduler.model.CCMView;
import com.ProjectScheduler.model.FinanceChanges;
import com.ProjectScheduler.model.IbasLabMaster;

public interface ProjectSchedulerService {

	public MailConfigurationDto getMailConfigByTypeOfHost(String typeOfHost)throws Exception;
	
	public List<Object[]> getTodaysMeetings(String date)throws Exception;
	
	public List<Object[]> CommitteeAtendance(String committeescheduleid) throws Exception;

	public List<Object[]> weeklyActionList( int NumberOfDays) throws Exception;
	
	public long GetMailInitiatedCount(String TrackingType)throws Exception;
	
	public long InsertMailTrackInitiator(String TrackingType)throws Exception;
	
	public List<Object[]> GetDailyPendingReplyEmpData() throws Exception;
	
	public long InsertDailyPendingInsights(long MailTrackingId)throws Exception;
	
	public long UpdateParticularEmpMailStatus(String MailPurpose,String MailStatus,long empId,long MailTrackingId) ;
	
	public long updateMailSuccessCount(long MailTrackingId,long MailSendSucessCount,String TrackingType)throws Exception;
	
	public long UpdateNoPendingReply(String TrackingType)throws Exception;
	
	public List<Object[]> GetSummaryDistributedEmpData() throws Exception;
	
	public long InsertSummaryDistributedInsights(long MailTrackingId)throws Exception;

	public List<Object[]> GetWeeklyPendingReplyEmpData() throws Exception;
	
	public long InsertWeeklyPendingInsights(long MailTrackingId)throws Exception;

	public long GetSMSInitiatedCount(String SmsTrackingType) throws Exception;

	public long InsertSmsTrackInitiator(String  TrackingType) throws Exception;

	public long InsertDailySmsPendingInsights(long smsTrackingId) throws Exception;
	
	public long updateSmsSuccessCount(long smsTrackingId, long SuccessCount, String TrackingType) throws Exception;
	
	public long UpdateNoSmsPendingReply(String TrackingType) throws Exception;
	
	public long DirectorInsertSmsTrackInitiator(String TrackingType) throws Exception;
	
	public List<Object[]> GetDirectorDailyPendingReplyEmpData(String Lab) throws Exception;
	
	public long DirectorInsertDailySmsPendingInsights(long smsTrackingId) throws Exception;
	
	public Object[] DirectorDakCounts(String actionDue) throws Exception;
	
	public List<Object[]> SmsReportList(String fromDate, String toDate) throws Exception;

	public long GetPmsSMSInitiatedCount(String SmsTrackingType) throws Exception;

	public long InsertPmsSmsTrackInitiator(String TrackingType) throws Exception;

	public List<Object[]> GetDailyPendingAssigneeEmpData() throws Exception;

	public long InsertDailyPmsSmsPendingInsights(long smsTrackingId) throws Exception;

	public long updatePmsSmsSuccessCount(long smsTrackingId, long SuccessCount, String TrackingType) throws Exception;

	public long UpdateNoPmsSmsPendingReply(String TrackingType) throws Exception;

	public long DirectorPmsInsertSmsTrackInitiator(String TrackingType) throws Exception;

	public List<Object[]> GetDirectorDailyPendingAssignEmpData(String Lab) throws Exception;

	public long DirectorInsertDailyPmsSmsPendingInsights(long smsTrackingId) throws Exception;

	public long GetCommitteSMSInitiatedCount(String SmsTrackingType) throws Exception;

	public long InsertCommitteSmsTrackInitiator(String TrackingType) throws Exception;

	public List<Object[]> GetCommitteEmpsDetailstoSendSms() throws Exception;

	public long InsertDailyCommitteSmsInsights(long committeSmsTrackingId) throws Exception;
	
	public long updateCommitteSmsSuccessCount(long committeSmsTrackingId, long SuccessCount, String TrackingType) throws Exception;
	
	public long UpdateCommitteNoSmsPending(String TrackingType) throws Exception;

	public List<Object[]> PmsSmsReportList(String fromDate, String toDate) throws Exception;

	public List<Object[]> SmsCommitteReportList(String fromDate, String toDate) throws Exception;
	
	public long ProjectHoaUpdate(List<ProjectHoa> hoa, String Username, List<IbasLabMaster> LabDetails) throws Exception;
	
	public long ProjectFinanceChangesUpdate(List<FinanceChanges> Monthly, List<FinanceChanges> Weekly, List<FinanceChanges> Today, String UserId,String LabCode) throws Exception;

	public Object getClusterId(String labCode) throws Exception;
	
	public long CCMViewDataUpdate(List<CCMView> CCMViewData, String LabCode, String ClusterId, String UserId, String EmpId) throws Exception;

	public long ProjectHealthUpdate(String labCode, String UserId) throws Exception;
	
}
