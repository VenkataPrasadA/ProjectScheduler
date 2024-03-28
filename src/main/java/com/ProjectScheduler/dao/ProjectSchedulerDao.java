package com.ProjectScheduler.dao;

import java.util.List;

import com.ProjectScheduler.model.DakMailTracking;
import com.ProjectScheduler.model.DakMailTrackingInsights;
import com.ProjectScheduler.model.DakSmsTracking;
import com.ProjectScheduler.model.DakSmsTrackingInsights;
import com.ProjectScheduler.model.PfmsCommitteSmsTracking;
import com.ProjectScheduler.model.PfmsSmsTracking;
import com.ProjectScheduler.model.PfmsSmsTrackingInsights;
import com.ProjectScheduler.model.ProjectHealth;
import com.ProjectScheduler.model.ProjectHoa;
import com.ProjectScheduler.model.ProjectHoaChanges;
import com.ProjectScheduler.model.PFMSCCMData;
import com.ProjectScheduler.model.PfmsCommitteSmsTrackingInsights;

public interface ProjectSchedulerDao {

	public List<Object[]> GetMailPropertiesByTypeOfHost(String typeOfHost)throws Exception;

	public List<Object[]> getTodaysMeetings(String date)throws Exception;

	public List<Object[]> CommitteeAtendance(String committeescheduleid) throws Exception;

	public List<Object[]> weeklyActionList(int NumberOfDays) throws Exception;
	
	public long GetMailInitiatedCount(String TrackingType)throws Exception;

	public long GetDailyExpectedPendingReplyCount()throws Exception;
	
	public long GetWeeklyExpectedPendingReplyCount()throws Exception;
	
	public long GetSummaryOfDailyDistributedCount()throws Exception;
	
	public long InsertMailTrackRow(DakMailTracking Model)throws Exception;
	
	public List<Object[]> GetDailyPendingReplyEmpData() throws Exception;
	
	public long InsertMailTrackInsights(DakMailTrackingInsights Model)throws Exception;
	
	public long UpdateParticularEmpMailStatus(String MailPurpose,String MailStatus,long empId,long MailTrackingId);
	
	public long UpdateDakMailTrackRow(long MailTrackingId,long SuccessCount,String TrackingType)throws Exception;
	
	public long UpdateNoPendingReply(String TrackingType)throws Exception;
	
	public List<Object[]> GetSummaryDistributedEmpData() throws Exception;

	public List<Object[]> GetWeeklyPendingReplyEmpData() throws Exception;

	public long GetSMSInitiatedCount(String smsTrackingType) throws Exception;
	
	public long InsertSmsTrackRow(DakSmsTracking model) throws Exception;
	
	public Object[] DakCounts(long empId, String actionDate) throws Exception;
	
	public long InsertSmsTrackInsights(DakSmsTrackingInsights insights) throws Exception;
	
	public long UpdateDakSmsTrackRow(long smsTrackingId, long successCount, String trackingType) throws Exception;
	
	public long UpdateNoSmsPendingReply(String trackingType) throws Exception;
	
	public List<Object[]> GetDirectorDailyPendingReplyEmpData(String Lab) throws Exception;
	
	public Object[] DirectorDakCounts(String string) throws Exception;
	
	public List<Object[]> SmsReportList(String fromDate, String toDate) throws Exception;

	public long GetPmsSMSInitiatedCount(String smsTrackingType) throws Exception;

	public long InsertPmsSmsTrackRow(PfmsSmsTracking model) throws Exception;

	public List<Object[]> GetDailyPendingAssigneeEmpData() throws Exception;

	public List<Object[]> ActionAssignedCounts(long empId) throws Exception;

	public long InsertPmsSmsTrackInsights(PfmsSmsTrackingInsights insights) throws Exception;

	public long UpdateDakActionTrackRow(long smsTrackingId, long successCount, String trackingType) throws Exception;

	public long UpdateNoPmsSmsPendingReply(String trackingType) throws Exception;

	public List<Object[]> GetDirectorDailyPendingAssignEmpData(String lab) throws Exception;

	public List<Object[]> DirectorActionAssignedCounts() throws Exception;

	public long GetCommitteSMSInitiatedCount(String smsTrackingType) throws Exception;

	public long dailyCommitteCount(String ScheduleDate) throws Exception;

	public long InsertCommitteSmsTrackRow(PfmsCommitteSmsTracking model) throws Exception;

	public List<Object[]> GetCommitteEmpsDetailstoSendSms() throws Exception;
	
	public List<Object[]> getCommittedata(long empId) throws Exception;
	
	public long InsertCommitteSmsTrackInsights(PfmsCommitteSmsTrackingInsights insights) throws Exception;
	
	public long UpdateCommitteSmsTrackRow(long committeSmsTrackingId, long successCount, String trackingType) throws Exception;
	
	public long UpdateCommitteNoSmsPending(String trackingType) throws Exception;

	public List<Object[]> PmsSmsReportList(String fromDate, String toDate) throws Exception;

	public List<Object[]> SmsCommitteReportList(String fromDate, String toDate) throws Exception;
	
	public long ProjectHoaUpdate(ProjectHoa hoa ) throws Exception;
	
	public int ProjectHoaDelete(String LabCode) throws Exception;

	public List<Object[]> ProjectList(String LabCode) throws Exception;
	
	public int ProjectHoaChangesDelete(String projectId)throws Exception;
	
	public long ProjectHoaChangesInsert(ProjectHoaChanges changes) throws Exception;

	public Object getClusterId(String labCode) throws Exception;
	
	public int CCMDataDelete(String LabCode) throws Exception;
	
	public long CCMDataInsert(PFMSCCMData ccmdata) throws Exception;
	
	public int ProjectHealthDelete(String projectId)throws Exception;

	public Object[] ProjectHealthInsertData(String projectId) throws Exception;

	public long ProjectHealthInsert(ProjectHealth health) throws Exception;
}
