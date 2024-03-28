package com.ProjectScheduler.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ProjectScheduler.dao.ProjectSchedulerDao;
import com.ProjectScheduler.dto.MailConfigurationDto;
import com.ProjectScheduler.model.CCMView;
import com.ProjectScheduler.model.DakMailTracking;
import com.ProjectScheduler.model.DakMailTrackingInsights;
import com.ProjectScheduler.model.DakSmsTracking;
import com.ProjectScheduler.model.DakSmsTrackingInsights;
import com.ProjectScheduler.model.PfmsSmsTracking;
import com.ProjectScheduler.model.PfmsSmsTrackingInsights;
import com.ProjectScheduler.model.ProjectHoa;
import com.ProjectScheduler.model.FinanceChanges;
import com.ProjectScheduler.model.ProjectHoaChanges;
import com.ProjectScheduler.model.ProjectHealth;
import com.ProjectScheduler.model.PFMSCCMData;
import com.ProjectScheduler.model.IbasLabMaster;
import com.ProjectScheduler.model.PfmsCommitteSmsTrackingInsights;
import com.ProjectScheduler.model.PfmsCommitteSmsTracking;



@Service
public class ProjectSchedulerServiceImpl implements ProjectSchedulerService {

	 private  SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 private  SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
		
	ReversibleEncryptionAlg rea = new ReversibleEncryptionAlg();
	@Autowired
	ProjectSchedulerDao dao;
	
	@Override
	public MailConfigurationDto getMailConfigByTypeOfHost(String typeOfHost) throws Exception {
		 List<Object[]> mailPropertiesByTypeOfHost = dao.GetMailPropertiesByTypeOfHost(typeOfHost);
		    if (mailPropertiesByTypeOfHost != null && !mailPropertiesByTypeOfHost.isEmpty()) {
		        Object[] obj = mailPropertiesByTypeOfHost.get(0); // Assuming you only expect one result

		        if (obj[2] != null && obj[3] != null && obj[4] != null && obj[5] != null) {
		            MailConfigurationDto dto = new MailConfigurationDto();
		            dto.setHost(obj[2].toString());
		            dto.setPort(obj[3].toString());
		            dto.setUsername(obj[4].toString());
		            // Assuming you have a method to decrypt the password using aes algorithm
		            String decryptedPassword = rea.decryptByAesAlg(obj[5].toString());
		            dto.setPassword(decryptedPassword);

		            return dto;
		        } else {
		            return null;
		        }
		    } else {
		        return null;
		    }
	}


	@Override
	public List<Object[]> getTodaysMeetings(String date) throws Exception {
		return dao.getTodaysMeetings(date);
	}
	
	@Override
	public List<Object[]> CommitteeAtendance(String committeescheduleid) throws Exception {
		return dao.CommitteeAtendance(committeescheduleid);
	}
	
	@Override
	public List<Object[]> weeklyActionList(int NumberOfDays) throws Exception {
		return dao.weeklyActionList(NumberOfDays);
	}
	
	@Override
	public long GetMailInitiatedCount(String TrackingType) throws Exception {
		return dao.GetMailInitiatedCount(TrackingType);
	}
	
	@Override
	public long InsertMailTrackInitiator(String TrackingType) throws Exception {
		long rowAddResult = 0;
		DakMailTracking Model  = new DakMailTracking();
		Model.setTrackingType(TrackingType);
		if(TrackingType!=null && TrackingType.equalsIgnoreCase("D")) {
	          long dailyPendingCount = dao.GetDailyExpectedPendingReplyCount();
	          Model.setMailExpectedCount(dailyPendingCount);
		}else if(TrackingType!=null && TrackingType.equalsIgnoreCase("W")) {
			long weeklyPendingCount = 	dao.GetWeeklyExpectedPendingReplyCount();
			 Model.setMailExpectedCount(weeklyPendingCount);
		
		}else if(TrackingType!=null && TrackingType.equalsIgnoreCase("S")) {
			long summaryDistributedCount = 	dao.GetSummaryOfDailyDistributedCount();
			 Model.setMailExpectedCount(summaryDistributedCount);
		}
		Model.setMailSentCount(0);
		Model.setMailSentStatus("N");
		Model.setCreatedDate(sdf2.format(new Date()));
		Model.setCreatedTime(new SimpleDateFormat("HH:mm:ss").format(new Date()));
	    rowAddResult = dao.InsertMailTrackRow(Model);
		return rowAddResult;
	}
	
	@Override
	public List<Object[]> GetDailyPendingReplyEmpData() throws Exception {
		return dao.GetDailyPendingReplyEmpData();
	}
	
	public long InsertDailyPendingInsights(long MailTrackingId) throws Exception {
	    long TrackingInsightsResult = 0;

	    List<Object[]> PendingReplyEmpsDetailstoSendMail = dao.GetDailyPendingReplyEmpData();
	    if (PendingReplyEmpsDetailstoSendMail != null && PendingReplyEmpsDetailstoSendMail.size() > 0) {
	        Map<Integer, Set<String>> empDakNosMap = new HashMap();

	        for (Object[] rowData : PendingReplyEmpsDetailstoSendMail) {
	            int empId = Integer.parseInt(rowData[1].toString());
	            String dakNo = rowData[4].toString();

	            if (!empDakNosMap.containsKey(empId)) {
	                empDakNosMap.put(empId, new HashSet<>());
	            }
	            empDakNosMap.get(empId).add(dakNo);
	        }
	        for (Map.Entry<Integer, Set<String>> entry : empDakNosMap.entrySet()) {
	            int empId = entry.getKey();
	            Set<String> dakNosSet = entry.getValue();
	            // Create a new instance of DakMailTrackingInsights for each entry
	            DakMailTrackingInsights Insights = new DakMailTrackingInsights();
	            Insights.setMailTrackingId(MailTrackingId);
	            Insights.setMailPurpose("D");
	            Insights.setMailStatus("N");
	            Insights.setCreatedDate(sdf1.format(new Date()));
	            Insights.setEmpId(empId);
	            Insights.setDakNos(String.join(",", dakNosSet));
	            // Insert the row into the table for this entry
	            long result = dao.InsertMailTrackInsights(Insights);
	            TrackingInsightsResult = result;
	        }
	    }
	    return TrackingInsightsResult;
	}
	
	@Override
	public long UpdateParticularEmpMailStatus(String MailPurpose,String MailStatus,long empId,long MailTrackingId) {
		return dao.UpdateParticularEmpMailStatus(MailPurpose,MailStatus,empId,MailTrackingId);
	}
	
	@Override
	public long updateMailSuccessCount(long MailTrackingId,long MailSendSucessCount,String TrackingType) throws Exception {
		long rowUpdateResult = dao.UpdateDakMailTrackRow(MailTrackingId,MailSendSucessCount,TrackingType);
	    return rowUpdateResult;
	}
	
	@Override
	public long UpdateNoPendingReply(String TrackingType)throws Exception{
		return dao.UpdateNoPendingReply(TrackingType);
	}
	
	@Override
	public List<Object[]> GetSummaryDistributedEmpData() throws Exception {
		return dao.GetSummaryDistributedEmpData();
	}

	@Override
	public long InsertSummaryDistributedInsights(long MailTrackingId)throws Exception{
    	   long TrackingInsightsResult = 0;
    	   List<Object[]> SummaryDistributedtoSendMail = dao.GetSummaryDistributedEmpData();
   	    if (SummaryDistributedtoSendMail != null && SummaryDistributedtoSendMail.size() > 0) {
   	         Map<Integer, Set<String>> empDakNosMap = new HashMap();
   	         for (Object[] rowData : SummaryDistributedtoSendMail) {
   	            int empId = Integer.parseInt(rowData[1].toString());
   	            String dakNo = rowData[4].toString();
                   if (!empDakNosMap.containsKey(empId)) {
   	                empDakNosMap.put(empId, new HashSet<>());
   	        }
              empDakNosMap.get(empId).add(dakNo);
   	      }
   	     for (Map.Entry<Integer, Set<String>> entry : empDakNosMap.entrySet()) {
   	            int empId = entry.getKey();
   	            Set<String> dakNosSet = entry.getValue();
   	            // Create a new instance of DakMailTrackingInsights for each entry
   	            DakMailTrackingInsights Insights = new DakMailTrackingInsights();
   	            Insights.setMailTrackingId(MailTrackingId);
   	            Insights.setMailPurpose("S");
   	            Insights.setMailStatus("N");
   	            Insights.setCreatedDate(sdf1.format(new Date()));
   	            Insights.setEmpId(empId);
   	            Insights.setDakNos(String.join(",", dakNosSet));

   	            // Insert the row into the table for this entry
   	            long result = dao.InsertMailTrackInsights(Insights);
   	            TrackingInsightsResult = result;
   	        }
   	    }
   	 return TrackingInsightsResult;
	}
	
	@Override
	public List<Object[]> GetWeeklyPendingReplyEmpData() throws Exception {
		return dao.GetWeeklyPendingReplyEmpData();
	}
	
	@Override
	public long InsertWeeklyPendingInsights(long MailTrackingId)throws Exception{
	    long TrackingInsightsResult = 0;
	    List<Object[]> PendingReplyEmpsDetailstoSendMail = dao.GetWeeklyPendingReplyEmpData();
	    if (PendingReplyEmpsDetailstoSendMail != null && PendingReplyEmpsDetailstoSendMail.size() > 0) {
	        Map<Integer, Set<String>> empDakNosMap = new HashMap();
	        for (Object[] rowData : PendingReplyEmpsDetailstoSendMail) {
	            int empId = Integer.parseInt(rowData[1].toString());
	            String dakNo = rowData[4].toString();
                if (!empDakNosMap.containsKey(empId)) {
	                empDakNosMap.put(empId, new HashSet<>());
	            }
	            empDakNosMap.get(empId).add(dakNo);
	     }
	        for (Map.Entry<Integer, Set<String>> entry : empDakNosMap.entrySet()) {
	            int empId = entry.getKey();
	            Set<String> dakNosSet = entry.getValue();
	            // Create a new instance of DakMailTrackingInsights for each entry
	            DakMailTrackingInsights Insights = new DakMailTrackingInsights();
	            Insights.setMailTrackingId(MailTrackingId);
	            Insights.setMailPurpose("W");
	            Insights.setMailStatus("N");
	            Insights.setCreatedDate(sdf1.format(new Date()));
	            Insights.setEmpId(empId);
	            Insights.setDakNos(String.join(",", dakNosSet));
	            // Insert the row into the table for this entry
	            long result = dao.InsertMailTrackInsights(Insights);
	            TrackingInsightsResult = result;
	        }
	    }
	    return TrackingInsightsResult;
	}
	
	@Override
	public long GetSMSInitiatedCount(String SmsTrackingType) throws Exception {
		return dao.GetSMSInitiatedCount(SmsTrackingType);
	}
	
	@Override
	public long InsertSmsTrackInitiator(String SmsTrackingType) throws Exception {
		long rowAddResult = 0;
		DakSmsTracking Model  = new DakSmsTracking();
		Model.setSmsTrackingType(SmsTrackingType);
	    long dailyPendingCount = dao.GetDailyExpectedPendingReplyCount();
	    Model.setSmsExpectedCount(dailyPendingCount);
		Model.setSmsSentCount(0);
		Model.setSmsSentStatus("N");
		Model.setCreatedDate(sdf2.format(new Date()));
		Model.setCreatedTime(new SimpleDateFormat("HH:mm:ss").format(new Date()));
	    rowAddResult = dao.InsertSmsTrackRow(Model);
		return rowAddResult;
	}
	
	@Override
	public long InsertDailySmsPendingInsights(long smsTrackingId) throws Exception {
		  long TrackingInsightsResult = 0;
		  long count=0;
		    List<Object[]> PendingReplyEmpsDetailstoSendSms = dao.GetDailyPendingReplyEmpData();
		    if (PendingReplyEmpsDetailstoSendSms != null && PendingReplyEmpsDetailstoSendSms.size() > 0) {
		        Map<Integer, Set<String>> empDakNosMap = new HashMap();
		        for (Object[] rowData : PendingReplyEmpsDetailstoSendSms) {
		            int empId = Integer.parseInt(rowData[1].toString());
		            String MobileNo = rowData[7].toString();
		            if (!empDakNosMap.containsKey(empId)) {
		                empDakNosMap.put(empId, new HashSet<>());
		            }
		            empDakNosMap.get(empId).add(MobileNo);
		        }

		        for (Map.Entry<Integer, Set<String>> entry : empDakNosMap.entrySet()) {
		            int empId = entry.getKey();
		            Set<String> MobileNo = entry.getValue();
		            String message="";
		            String mobileNumber = MobileNo.isEmpty() ? null : MobileNo.iterator().next();
		            Object[] DakCounts =dao.DakCounts(empId,LocalDate.now().toString());
		            if(mobileNumber != null && !mobileNumber.equalsIgnoreCase("0") && mobileNumber.trim().length()>0 && mobileNumber.trim().length()==10 && Integer.parseInt(DakCounts[0].toString())>0) {
		            message = "Good Morning DAK  DP= " +DakCounts[0].toString() + "  DU= "+DakCounts[1].toString() +"  DT= " +DakCounts[2].toString()+" DD= "+DakCounts[3].toString() +" -DMS Team.";
		            // Create a new instance of DakMailTrackingInsights for each entry
		            DakSmsTrackingInsights Insights = new DakSmsTrackingInsights();
		            Insights.setSmsTrackingId(smsTrackingId);
		            Insights.setSmsPurpose("D");
		            Insights.setSmsStatus("S");
		            Insights.setDakPendingCount(Long.parseLong(DakCounts[0].toString()));
		            Insights.setDakUrgentCount(Long.parseLong(DakCounts[1].toString()));
		            Insights.setDakTodayPending(Long.parseLong(DakCounts[2].toString()));
		            Insights.setDakDelayCount(Long.parseLong(DakCounts[3].toString()));
		            Insights.setMessage(message);
		            Insights.setCreatedDate(sdf1.format(new Date()));
		            Insights.setSmsSentDate(sdf1.format(new Date()));
		            Insights.setEmpId(empId);
		            // Insert the row into the table for this entry
		            long result = dao.InsertSmsTrackInsights(Insights);
		            TrackingInsightsResult = result;
		            if(TrackingInsightsResult>0) {
		            	count++;
		            }
		        }
		    }
		    }
		    return count;
	}
	
	@Override
	public long updateSmsSuccessCount(long smsTrackingId, long SuccessCount, String TrackingType) throws Exception {
		long rowUpdateResult = dao.UpdateDakSmsTrackRow(smsTrackingId,SuccessCount,TrackingType);
	    return rowUpdateResult;
	}
	
	@Override
	public long UpdateNoSmsPendingReply(String TrackingType) throws Exception {
		return dao.UpdateNoSmsPendingReply(TrackingType);
	}
	
	@Override
	public long DirectorInsertSmsTrackInitiator(String TrackingType) throws Exception {
		long rowAddResult = 0;
		DakSmsTracking Model  = new DakSmsTracking();
		Model.setSmsTrackingType(TrackingType);
	    Model.setSmsExpectedCount(1);
		Model.setSmsSentCount(0);
		Model.setSmsSentStatus("N");
		Model.setCreatedDate(sdf2.format(new Date()));
		Model.setCreatedTime(new SimpleDateFormat("HH:mm:ss").format(new Date()));
	    rowAddResult = dao.InsertSmsTrackRow(Model);
		return rowAddResult;
	}
	
	@Override
	public List<Object[]> GetDirectorDailyPendingReplyEmpData(String Lab) throws Exception {
		return dao.GetDirectorDailyPendingReplyEmpData(Lab);
	}

	@Override
	public long DirectorInsertDailySmsPendingInsights(long smsTrackingId) throws Exception {
		 long TrackingInsightsResult = 0;
		 long count=0;
		 List<Object[]> DirectorPendingReplyEmpsDetailstoSendSms = dao.GetDirectorDailyPendingReplyEmpData("LRDE");
		    if (DirectorPendingReplyEmpsDetailstoSendSms != null && DirectorPendingReplyEmpsDetailstoSendSms.size() > 0) {
		        for (Object[] rowData : DirectorPendingReplyEmpsDetailstoSendSms) {
		            int empId = Integer.parseInt(rowData[0].toString());
		            String MobileNo=rowData[1].toString();
		            Object[] DirectorDakCounts =dao.DirectorDakCounts(LocalDate.now().toString());
		            // Create a new instance of DakMailTrackingInsights for each entry
		            if(MobileNo != null && !MobileNo.equalsIgnoreCase("0") && MobileNo.trim().length()>0 && MobileNo.trim().length()==10 && Integer.parseInt(DirectorDakCounts[0].toString())>0) {
		            String message = "Good Morning DAK  DP= " +DirectorDakCounts[0].toString() + "  DU= "+DirectorDakCounts[1].toString() +"  DT= " +DirectorDakCounts[2].toString()+" DD= "+DirectorDakCounts[3].toString() +" -DMS Team.";
		            DakSmsTrackingInsights Insights = new DakSmsTrackingInsights();
		            Insights.setSmsTrackingId(smsTrackingId);
		            Insights.setSmsPurpose("D");
		            Insights.setSmsStatus("S");
		            Insights.setDakPendingCount(Long.parseLong(DirectorDakCounts[0].toString()));
		            Insights.setDakUrgentCount(Long.parseLong(DirectorDakCounts[1].toString()));
		            Insights.setDakTodayPending(Long.parseLong(DirectorDakCounts[2].toString()));
		            Insights.setDakDelayCount(Long.parseLong(DirectorDakCounts[3].toString()));
		            Insights.setMessage(message);
		            Insights.setCreatedDate(sdf1.format(new Date()));
		            Insights.setSmsSentDate(sdf1.format(new Date()));
		            Insights.setEmpId(empId);
		            // Insert the row into the table for this entry
		            long result = dao.InsertSmsTrackInsights(Insights);
		            TrackingInsightsResult = result;
		            if(TrackingInsightsResult>0) {
		            	count++;
		            }
		        }
		    }
		    }
		    return count;
	}

	@Override
	public Object[] DirectorDakCounts(String actionDue) throws Exception {
		return dao.DirectorDakCounts(actionDue);
	}
	
	@Override
	public List<Object[]> SmsReportList(String fromDate, String toDate) throws Exception {
		return dao.SmsReportList(fromDate,toDate);
	}
	
	@Override
	public long GetPmsSMSInitiatedCount(String SmsTrackingType) throws Exception {
		return dao.GetPmsSMSInitiatedCount(SmsTrackingType);
	}
	
	@Override
	public long InsertPmsSmsTrackInitiator(String TrackingType) throws Exception {
		long rowAddResult = 0;
		PfmsSmsTracking Model  = new PfmsSmsTracking();
		Model.setSmsTrackingType(TrackingType);
	    long dailyPendingCount = dao.GetDailyExpectedPendingReplyCount();
	    Model.setSmsExpectedCount(dailyPendingCount);
		Model.setSmsSentCount(0);
		Model.setSmsSentStatus("N");
		Model.setCreatedDate(sdf2.format(new Date()));
		Model.setCreatedTime(new SimpleDateFormat("HH:mm:ss").format(new Date()));
		
	    rowAddResult = dao.InsertPmsSmsTrackRow(Model);
		
		return rowAddResult;
	}
	
	@Override
	public List<Object[]> GetDailyPendingAssigneeEmpData() throws Exception {
		return dao.GetDailyPendingAssigneeEmpData();
	}
	
	@Override
	public long InsertDailyPmsSmsPendingInsights(long smsTrackingId) throws Exception {
		 long TrackingInsightsResult = 0;
		 long count=0;
		    List<Object[]> PendingAssingEmpsDetailstoSendSms = dao.GetDailyPendingAssigneeEmpData();
		    if (PendingAssingEmpsDetailstoSendSms != null && PendingAssingEmpsDetailstoSendSms.size() > 0) {
		        Map<Integer, Set<String>> empActionItemMap = new HashMap();

		        for (Object[] rowData : PendingAssingEmpsDetailstoSendSms) {
		            int empId = Integer.parseInt(rowData[1].toString());
		            String Mobileno = rowData[4].toString();
		            
		            if (!empActionItemMap.containsKey(empId)) {
		            	empActionItemMap.put(empId, new HashSet<>());
		            }

		            empActionItemMap.get(empId).add(Mobileno);
		        }

		        for (Map.Entry<Integer, Set<String>> entry : empActionItemMap.entrySet()) {
		            int empId = entry.getKey();
		            Set<String> Mobileno = entry.getValue();
		            
		            String mobileNumber = Mobileno.isEmpty() ? null : Mobileno.iterator().next();
		           // Object[] ActionAssignCounts =dao.ActionAssignCounts(empId,LocalDate.now().toString());
		            if(mobileNumber != null && !mobileNumber.equalsIgnoreCase("0") && mobileNumber.trim().length()>0 && mobileNumber.trim().length()==10) {
		            List<Object[]> ActionAssignCount=dao.ActionAssignedCounts(empId);
		            // Create a new instance of DakMailTrackingInsights for each entry
		            PfmsSmsTrackingInsights Insights = new PfmsSmsTrackingInsights();
		            Insights.setSmsTrackingId(smsTrackingId);
		            Insights.setSmsPurpose("D");
		            Insights.setSmsStatus("S");
		            String message=null;
		            String Action=null;
		            String Meeting=null;
		            String Milestone=null;
		            
		            System.out.println("Mobileno:"+mobileNumber);
		            for(Object[] obj:ActionAssignCount) {
		            	if(obj[0].toString().equalsIgnoreCase("ActionItems")) {
				            Insights.setActionItemP(Long.parseLong(obj[2].toString()));
				            Insights.setActionItemTP(Long.parseLong(obj[3].toString()));
				            Insights.setActionItemDP(Long.parseLong(obj[4].toString()));
				            if(Long.parseLong(obj[2].toString())>0) {
				            Action="Action Pending - "+Long.parseLong(obj[2].toString()) +" / Action Delay - "+ Long.parseLong(obj[4].toString()) +" / Action Today - "+ Long.parseLong(obj[3].toString())+" / ";
				            }
		            	}else if(obj[0].toString().equalsIgnoreCase("MeetingActions")) {
		            		Insights.setMeetingActionP(Long.parseLong(obj[2].toString()));
				            Insights.setMeetingActionTP(Long.parseLong(obj[3].toString()));
				            Insights.setMeetingActionDP(Long.parseLong(obj[4].toString()));
				            if(Long.parseLong(obj[2].toString())>0) {
				            Meeting="Meeting Pending - "+Long.parseLong(obj[2].toString()) +" /  Meeting Delay - "+ Long.parseLong(obj[4].toString())+ " /  Meeting Today - "+Long.parseLong(obj[3].toString())+" ";
				            }
		            	}else {
		            		Insights.setMilestoneActionP(Long.parseLong(obj[2].toString()));
				            Insights.setMilestoneActionTP(Long.parseLong(obj[3].toString()));
				            Insights.setMilestoneActionDP(Long.parseLong(obj[4].toString()));
				            if(Long.parseLong(obj[2].toString())>0) {
				            Milestone="MileStone Pending - "+Long.parseLong(obj[2].toString()) +" / MileStone Delay - "+ Long.parseLong(obj[4].toString())+ " / Milestone Today - "+Long.parseLong(obj[3].toString())+" / ";
				            }
		            	}
		            //	message="Good Morning,\nPMS P / D / T  \n" +(Action != null ? "AI  "+ Action : "") + "\n"+ (Meeting != null ?"MS  "+ Meeting : "") +"\n"+(Milestone != null ?"MT  "+ Milestone : "")+"\n-PMS Team.";
		            	message = "Good Morning PMS  " ;
		            	if(Action!=null) {
		            		 message += Action+" " ;
		            	}
		            	if (Milestone != null) {
		            	    message +=  Milestone+" " ;
		            	}
		            	if (Meeting != null) {
		            	    message += Meeting+" " ;
		            	}
		            	message += " - PMS Team.";

		            	Insights.setMessage(message);
		            	System.out.println("message:12343524"+message);
		            }
		            Insights.setCreatedDate(sdf1.format(new Date()));
		            Insights.setSmsSentDate(sdf1.format(new Date()));
		            Insights.setEmpId(empId);

		            // Insert the row into the table for this entry
		            long result = dao.InsertPmsSmsTrackInsights(Insights);
		            TrackingInsightsResult = result;
		            if(TrackingInsightsResult>0) {
		            	count++;
		            }
		        }
		        }
		    }

		    return count;
	}
	
	@Override
	public long updatePmsSmsSuccessCount(long smsTrackingId, long SuccessCount, String TrackingType) throws Exception {
		long rowUpdateResult = dao.UpdateDakActionTrackRow(smsTrackingId,SuccessCount,TrackingType);
	    return rowUpdateResult;
	}
	
	@Override
	public long UpdateNoPmsSmsPendingReply(String TrackingType) throws Exception {
		return dao.UpdateNoPmsSmsPendingReply(TrackingType);
	}
	
	@Override
	public long DirectorPmsInsertSmsTrackInitiator(String TrackingType) throws Exception {
		long rowAddResult = 0;
		PfmsSmsTracking Model  = new PfmsSmsTracking();
		Model.setSmsTrackingType(TrackingType);
	    Model.setSmsExpectedCount(1);
		Model.setSmsSentCount(0);
		Model.setSmsSentStatus("N");
		Model.setCreatedDate(sdf2.format(new Date()));
		Model.setCreatedTime(new SimpleDateFormat("HH:mm:ss").format(new Date()));
		
	    rowAddResult = dao.InsertPmsSmsTrackRow(Model);
		
		return rowAddResult;
	}
	
	@Override
	public List<Object[]> GetDirectorDailyPendingAssignEmpData(String Lab) throws Exception {
		return dao.GetDirectorDailyPendingAssignEmpData(Lab);
	}
	
	@Override
	public long DirectorInsertDailyPmsSmsPendingInsights(long smsTrackingId) throws Exception {
		long TrackingInsightsResult = 0;
		 long count=0;
		 List<Object[]> DirectorPendingAssignEmpsDetailstoSendSms = dao.GetDirectorDailyPendingAssignEmpData("LRDE");
		    if (DirectorPendingAssignEmpsDetailstoSendSms != null && DirectorPendingAssignEmpsDetailstoSendSms.size() > 0) {

		        for (Object[] rowData : DirectorPendingAssignEmpsDetailstoSendSms) {
		            int empId = Integer.parseInt(rowData[0].toString());
		           // Object[] DirectorActionAsssignCounts =dao.DirectorActionAssignCounts(LocalDate.now().toString());
		            // Create a new instance of DakMailTrackingInsights for each entry
		            String Mobileno=rowData[1].toString();
		            if( Mobileno != null && !Mobileno.toString().equalsIgnoreCase("0") && Mobileno.toString().trim().length()>0 && Mobileno.toString().trim().length()==10) {
		            List<Object[]> DirectorActionAssignCounts=dao.DirectorActionAssignedCounts();
		            PfmsSmsTrackingInsights Insights = new PfmsSmsTrackingInsights();
		            Insights.setSmsTrackingId(smsTrackingId);
		            Insights.setSmsPurpose("D");
		            Insights.setSmsStatus("S");
		            String message=null;
		            String Action=null;
		            String Meeting=null;
		            String Milestone=null;
		            for(Object[] obj:DirectorActionAssignCounts) {
		            	if(obj[0].toString().equalsIgnoreCase("ActionItems")) {
				            Insights.setActionItemP(Long.parseLong(obj[2].toString()));
				            Insights.setActionItemTP(Long.parseLong(obj[3].toString()));
				            Insights.setActionItemDP(Long.parseLong(obj[4].toString()));
				            if(Long.parseLong(obj[2].toString())>0) {
				            	 Action="Action Pending - "+Long.parseLong(obj[2].toString()) +" / Action Delay - "+ Long.parseLong(obj[4].toString()) +" / Action Today - "+ Long.parseLong(obj[3].toString())+" / ";
					        }
		            	}else if(obj[0].toString().equalsIgnoreCase("MeetingActions")) {
		            		Insights.setMeetingActionP(Long.parseLong(obj[2].toString()));
				            Insights.setMeetingActionTP(Long.parseLong(obj[3].toString()));
				            Insights.setMeetingActionDP(Long.parseLong(obj[4].toString()));
				            if(Long.parseLong(obj[2].toString())>0) {
				            	Meeting="Meeting Pending - "+Long.parseLong(obj[2].toString()) +" /  Meeting Delay - "+ Long.parseLong(obj[4].toString())+ " /  Meeting Today - "+Long.parseLong(obj[3].toString())+" ";
					        }
		            	}else {
		            		Insights.setMilestoneActionP(Long.parseLong(obj[2].toString()));
				            Insights.setMilestoneActionTP(Long.parseLong(obj[3].toString()));
				            Insights.setMilestoneActionDP(Long.parseLong(obj[4].toString()));
				            if(Long.parseLong(obj[2].toString())>0) {
				            	Milestone="MileStone Pending - "+Long.parseLong(obj[2].toString()) +" / MileStone Delay - "+ Long.parseLong(obj[4].toString())+ " / Milestone Today - "+Long.parseLong(obj[3].toString())+" / ";
					        }
		            	}
		            	message = "Good Morning PMS " ;
		            	if(Action!=null) {
		            		 message += Action+" " ;
		            	}
		            	if (Milestone != null) {
		            	    message += Milestone+" ";
		            	}
		            	if (Meeting != null) {
		            	    message += Meeting+" " ;
		            	}
		            	message += " - PMS Team.";

		            	Insights.setMessage(message);
		            	System.out.println("message:12343524"+message);
		            }
		            Insights.setCreatedDate(sdf1.format(new Date()));
		            Insights.setSmsSentDate(sdf1.format(new Date()));
		            Insights.setEmpId(empId);

		            // Insert the row into the table for this entry
		            long result = dao.InsertPmsSmsTrackInsights(Insights);
		            TrackingInsightsResult = result;
		            if(TrackingInsightsResult>0) {
		            	count++;
		            }
		            }
		        }
		    }

		    return count;
	}
	
	@Override
	public long GetCommitteSMSInitiatedCount(String SmsTrackingType) throws Exception {
		return dao.GetCommitteSMSInitiatedCount(SmsTrackingType);
	}
	
	@Override
	public long InsertCommitteSmsTrackInitiator(String TrackingType) throws Exception {
		long rowAddResult = 0;
		PfmsCommitteSmsTracking Model  = new PfmsCommitteSmsTracking();
		Model.setSmsTrackingType(TrackingType);
	    long dailyCommitteCount = dao.dailyCommitteCount(LocalDate.now().toString());
	    Model.setSmsExpectedCount(dailyCommitteCount);
		Model.setSmsSentCount(0);
		Model.setSmsSentStatus("N");
		Model.setCreatedDate(sdf2.format(new Date()));
		Model.setCreatedTime(new SimpleDateFormat("HH:mm:ss").format(new Date()));
		
	    rowAddResult = dao.InsertCommitteSmsTrackRow(Model);
		
		return rowAddResult;
	}
	
	@Override
	public List<Object[]> GetCommitteEmpsDetailstoSendSms() throws Exception {
		return dao.GetCommitteEmpsDetailstoSendSms();
	}
	
	@Override
	public long InsertDailyCommitteSmsInsights(long committeSmsTrackingId) throws Exception {
		 long TrackingInsightsResult = 0;
		 long count=0;
		    List<Object[]> CommitteEmpsDetailstoSendSms = dao.GetCommitteEmpsDetailstoSendSms();
		    if (CommitteEmpsDetailstoSendSms != null && CommitteEmpsDetailstoSendSms.size() > 0) {

		        for (Object[] rowData : CommitteEmpsDetailstoSendSms) {
		            int empId = Integer.parseInt(rowData[0].toString());
		            String Mobileno=rowData[1].toString();
		            String message="";
		            if(Mobileno != null && !Mobileno.toString().equalsIgnoreCase("0") && Mobileno.toString().trim().length()>0 && Mobileno.toString().trim().length()==10) {
               	    List<Object[]> committedata=dao.getCommittedata(empId);
		            PfmsCommitteSmsTrackingInsights Insights = new PfmsCommitteSmsTrackingInsights();
		            Insights.setCommitteSmsTrackingId(committeSmsTrackingId);
		            Insights.setSmsPurpose("D");
		            Insights.setSmsStatus("S");
		            for(Object[] str:committedata) {
						LocalTime time = LocalTime.parse(str[6].toString());
				        
				        // Format the time as "HH:mm Hrs"
				        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm 'Hrs'");
				        String formattedTime = time.format(formatter);
				        message="Good Morning ";
						if(str[7]!=null && str[7].toString().equalsIgnoreCase("P")) {
							message+=" Today Status: P"+str[1].toString()+" - "+str[2].toString() +" @ " +formattedTime+". ";
						}else if(str[7]!=null && str[7].toString().equalsIgnoreCase("N")){
							message+=" Today Status: NP - "+str[2].toString() +" @ " +formattedTime+". ";
						}else{ 
							message+=" Today Status: GEN - "+str[2].toString() +" @ " +formattedTime+". ";
						}
						
					}
					message+=" - PMS Team.";
					Insights.setMessage(message);
		            Insights.setCreatedDate(sdf1.format(new Date()));
		            Insights.setSmsSentDate(sdf1.format(new Date()));
		            Insights.setEmpId(empId);

		            // Insert the row into the table for this entry
		            long result = dao.InsertCommitteSmsTrackInsights(Insights);
		            TrackingInsightsResult = result;
		            if(TrackingInsightsResult>0) {
		            	count++;
		            }
		            }
		        }
		  }
		    return count;
	}
	
	@Override
	public long updateCommitteSmsSuccessCount(long committeSmsTrackingId, long SuccessCount, String TrackingType)throws Exception {
		long rowUpdateResult = dao.UpdateCommitteSmsTrackRow(committeSmsTrackingId,SuccessCount,TrackingType);
	    return rowUpdateResult;
	}
	
	@Override
	public long UpdateCommitteNoSmsPending(String TrackingType) throws Exception {
		return dao.UpdateCommitteNoSmsPending(TrackingType);
	}
	
	@Override
	public List<Object[]> PmsSmsReportList(String fromDate, String toDate) throws Exception {
		return dao.PmsSmsReportList(fromDate,toDate);
	}
	
	@Override
	public List<Object[]> SmsCommitteReportList(String fromDate, String toDate) throws Exception {
		return dao.SmsCommitteReportList(fromDate,toDate);
	}
	
	@Override
	public long ProjectHoaUpdate(List<ProjectHoa> hoa,String Username,List<IbasLabMaster> LabDetails) throws Exception{
		long count1 =0 ;
		long count = dao.ProjectHoaDelete(LabDetails.get(0).getLabCode());
		System.out.println("HIIHIhziahsihaoisoiahdoisadjoisajodjsaodjoiasjiodsaoidjosioj");
		for(ProjectHoa obj : hoa) {
			obj.setCreatedBy(Username);
			obj.setCreatedDate(sdf1.format(new Date()));
			obj.setLabCode(LabDetails.get(0).getLabCode());
			count1=dao.ProjectHoaUpdate(obj);
		}
		
		return count1;
	}
	
	@Override
	public long ProjectFinanceChangesUpdate(List<FinanceChanges> Monthly, List<FinanceChanges> Weekly, List<FinanceChanges> Today, String UserId,String LabCode) throws Exception {
		List<Object[]> proList=dao.ProjectList(LabCode);
		long result=0;
		for(Object[] obj:proList) {
			try {
		        dao.ProjectHoaChangesDelete(obj[0].toString());
		        ProjectHoaChanges changes = new ProjectHoaChanges();
		        changes.setProjectId(Long.parseLong(obj[0].toString()));
		        changes.setProjectCode(obj[1].toString());
		        changes.setMonthlyChanges(Long.valueOf(Monthly.stream().filter(c-> c.getProjectCode().toString().equalsIgnoreCase(obj[1].toString())).collect(Collectors.toList()).size()));
		        changes.setWeeklyChanges(Long.valueOf(Weekly.stream().filter(c-> c.getProjectCode().toString().equalsIgnoreCase(obj[1].toString())).collect(Collectors.toList()).size()));
		        changes.setTodayChanges(Long.valueOf(Today.stream().filter(c-> c.getProjectCode().toString().equalsIgnoreCase(obj[1].toString())).collect(Collectors.toList()).size()));
		        changes.setCreatedBy(UserId);
		        changes.setCreatedDate(sdf1.format(new Date()));
		        changes.setIsActive(1);
		        
		        result= dao.ProjectHoaChangesInsert(changes);

			}catch (Exception e) {
				e.printStackTrace();
			}		
			}
		return result;
	}
	
	@Override
	public Object getClusterId(String labCode) throws Exception {
		return dao.getClusterId(labCode);
	}
	
	@Override
	public long CCMViewDataUpdate(List<CCMView> CCMViewData,String LabCode,String ClusterId, String UserId,String EmpId) throws Exception
	{
		List<Object[]> proList=dao.ProjectList(LabCode);
		
		long result=0;
		
		if(CCMViewData.size()>0) 
		{
			dao.CCMDataDelete(LabCode);
		
		
			for(CCMView ccmdata:CCMViewData)
			{
				PFMSCCMData pfmsccm = PFMSCCMData.builder()
								
								.ClusterId(Long.parseLong(ClusterId))
								.LabCode(LabCode)
								.ProjectId(ccmdata.getProjectId())
								.ProjectCode(ccmdata.getProjectCode().trim())
								.BudgetHeadId(ccmdata.getBudgetHeadId())
								.BudgetHeadDescription(ccmdata.getBudgetHeadDescription())
								.AllotmentCost(ccmdata.getAllotmentCost())
								.Expenditure(ccmdata.getExpenditure())
								.Balance(ccmdata.getBalance())
								.Q1CashOutGo(ccmdata.getQ1CashOutGo())
								.Q2CashOutGo(ccmdata.getQ2CashOutGo())
								.Q3CashOutGo(ccmdata.getQ3CashOutGo())
								.Q4CashOutGo(ccmdata.getQ4CashOutGo())
								.Required(ccmdata.getRequired())
								.CreatedDate(sdf1.format(new Date()))
								.build();
				result= dao.CCMDataInsert(pfmsccm);
				
			}
		}
		return result;
	}
	
	
	@Override
	public long ProjectHealthUpdate(String LabCode, String UserName) throws Exception {
		List<Object[]> proList=dao.ProjectList(LabCode);
		long result=0;
		if(proList!=null && proList.size()>0) {
		for(Object[] obj:proList) {
			try {
		        dao.ProjectHealthDelete(obj[0].toString());
				Object[] data=dao.ProjectHealthInsertData(obj[0].toString());
				ProjectHealth health=new ProjectHealth();
				health.setLabCode(data[0].toString());
				health.setProjectId(Long.parseLong(data[1].toString()));
				health.setProjectShortName(data[2].toString());
				health.setPMRCHeld(Long.parseLong(data[3].toString()));
				health.setPMRCPending( Long.parseLong(data[4].toString())>=0 ?  Long.parseLong(data[4].toString()) : 0 );
				health.setEBHeld(Long.parseLong(data[5].toString()));
				health.setEBPending(  Long.parseLong(data[6].toString())>=0 ? Long.parseLong(data[6].toString()) : 0 );
				health.setMilPending(Long.parseLong(data[7].toString()));
				health.setMilDelayed(Long.parseLong(data[8].toString()));
				health.setMilCompleted(Long.parseLong(data[9].toString()));
				health.setActionPending(Long.parseLong(data[10].toString()));
				health.setActionForwarded(Long.parseLong(data[11].toString()));
				health.setActionDelayed(Long.parseLong(data[12].toString()));
				health.setActionCompleted(Long.parseLong(data[13].toString()));
				health.setRiskPending(Long.parseLong(data[14].toString()));
				health.setRiskCompleted(Long.parseLong(data[15].toString()));
				health.setProjectType(data[20].toString());
				health.setEndUser(data[21].toString());
				health.setProjectCode(data[22].toString());
				health.setPMRCTotal(Long.parseLong(data[23].toString()));
				health.setEBTotal(Long.parseLong(data[24].toString()));
				
				if(data[16]!=null) {
					health.setExpenditure(Double.parseDouble(data[16].toString()));
					health.setDipl(Double.parseDouble(data[18].toString()));
					health.setOutCommitment(Double.parseDouble(data[17].toString()));
					health.setBalance(Double.parseDouble(data[19].toString()));
				}else {
					health.setExpenditure(Double.parseDouble("0.00"));
					health.setDipl(Double.parseDouble("0.00"));
					health.setOutCommitment(Double.parseDouble("0.00"));
					health.setBalance(Double.parseDouble("0.00"));
				}
				
				health.setCreatedBy(UserName);
				health.setCreatedDate(sdf1.format(new Date()));
				health.setTodayChanges(Long.parseLong(data[25].toString()));
				health.setWeeklyChanges(Long.parseLong(data[26].toString()));
				health.setMonthlyChanges(Long.parseLong(data[27].toString()));
				health.setPDC(data[28].toString());		
				health.setPMRCTotalToBeHeld(Long.parseLong(data[29].toString()));
				health.setEBTotalToBeHeld(Long.parseLong(data[30].toString()));
				health.setSanctionDate(data[31].toString());
				result=dao.ProjectHealthInsert(health);
			}catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("inersrwsrdsafsdfdsfdsfds");
			}
		}
		return result;
	}
	}


