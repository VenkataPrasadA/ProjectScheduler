package com.ProjectScheduler.dao;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.ProjectScheduler.model.DakMailTracking;
import com.ProjectScheduler.model.DakMailTrackingInsights;
import com.ProjectScheduler.model.DakSmsTracking;
import com.ProjectScheduler.model.DakSmsTrackingInsights;
import com.ProjectScheduler.model.PfmsSmsTracking;
import com.ProjectScheduler.model.PfmsSmsTrackingInsights;
import com.ProjectScheduler.model.ProjectHoa;
import com.ProjectScheduler.model.ProjectHoaChanges;
import com.ProjectScheduler.model.ProjectHealth;
import com.ProjectScheduler.model.PFMSCCMData;
import com.ProjectScheduler.model.PfmsCommitteSmsTrackingInsights;
import com.ProjectScheduler.model.PfmsCommitteSmsTracking;

@Transactional
@Repository
public class ProjectSchedulerDaoImpl implements ProjectSchedulerDao {
	private static final Logger logger=LogManager.getLogger(ProjectSchedulerDaoImpl.class);

	@PersistenceContext
	EntityManager manager;
	
	private static final String MAILPROPERTIESCONFIGURATION = "SELECT a.MailConfigurationId,a.TypeOfHost,a.Host,a.Port,a.Username,a.Password,a.CreatedBy,a.CreatedDate FROM mail_configuration a WHERE a.TypeOfHost=:typeOfHost LIMIT 1";
	@Override
	public List<Object[]> GetMailPropertiesByTypeOfHost(String typeOfHost)throws Exception{
		Query query = manager.createNativeQuery(MAILPROPERTIESCONFIGURATION);
		query.setParameter("typeOfHost", typeOfHost);
		List<Object[]> GetMailPropertiesByTypeOfHost = query.getResultList();
		return GetMailPropertiesByTypeOfHost;
	}

	private static final String MEETINGS="SELECT cs.scheduleid,cs.projectid,cs.InitiationId,c.CommitteeShortName,c.CommitteeName,cs.MeetingVenue,cs.ScheduleStartTime,pm.projectcode,pm.projectshortname FROM committee_schedule cs,committee c ,project_master pm WHERE  c.CommitteeId=cs.CommitteeId AND pm.projectid=cs.projectid AND  cs.ScheduleDate=:date AND cs.isactive='1'";
	@Override
	public List<Object[]> getTodaysMeetings(String date) throws Exception {
		Query query = manager.createNativeQuery(MEETINGS);
		query.setParameter("date", date);
		return (List<Object[]>)query.getResultList();
	}
	
		@Override
		public List<Object[]> CommitteeAtendance(String committeescheduleid) throws Exception {
			Query query= manager.createNativeQuery("Call Pfms_Committee_Invitation (:committeescheduleid)");
			query.setParameter("committeescheduleid", committeescheduleid);
			return (List<Object[]>)query.getResultList();
		}
		
		
		private static String WEEKLYACTIONLIST="SELECT a.actionno,(SELECT CONCAT(IFNULL(CONCAT(e.title,' '),IFNULL(CONCAT(e.salutation,' '),'')), e.empname) FROM employee e WHERE e.empid=a.assignor)AS 'Assignor',(SELECT email FROM employee e WHERE e.empid=a.assignee)AS 'AssigneeEmail',a.assignee,a.progress,a.enddate FROM action_assign a WHERE a.EndDate BETWEEN CURDATE() AND CURDATE()+(:NumberOfDays) AND a.assigneeLabCode <> '@EXP' ORDER BY a.assignee";
		@Override
		public List<Object[]> weeklyActionList(int NumberOfDays) throws Exception {
			Query query = manager.createNativeQuery(WEEKLYACTIONLIST);
			query.setParameter("NumberOfDays", NumberOfDays);
			
			return (List<Object[]>)query.getResultList();
		}
		
		private static final String MAILINITIATEDCOUNT = "SELECT COUNT(*) FROM dak_mail_track WHERE CreatedDate = CURDATE() AND TrackingType=:trackingType";
		@Override
		public long GetMailInitiatedCount(String TrackingType) throws Exception {
			logger.info(new Date() + "Inside GetMailInitiatedCount");
			try {
			Query query = manager.createNativeQuery(MAILINITIATEDCOUNT);
		    query.setParameter("trackingType", TrackingType);
		    BigInteger countResult = (BigInteger) query.getSingleResult();
	        return countResult.longValue();
		 } catch (Exception e) {
			 e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl GetMailInitiatedCount", e);
				return 0;
		    }
		}
		
		@Override
		public long InsertMailTrackRow(DakMailTracking Model)throws Exception{
			logger.info(new Date() + "Inside DAO InsertDailyMailTrack");
			try {
				manager.persist(Model);
				manager.flush();
				return Model.getMailTrackingId();
            } catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl InsertDailyMailTrack", e);
				return 0;
			}
		}

		private static final String DAILYEXPECTEDPENDINGREPLYCOUNT = "SELECT COUNT(DISTINCT m.EmpId) FROM dak_marking m JOIN Dak a ON m.DakId = a.DakId WHERE a.ReceiptDate IS NOT NULL AND DATE(a.ReceiptDate) >='2023-12-01' AND m.DakId NOT IN (SELECT r.DakId FROM dak_reply r WHERE r.DakId = m.DakId) AND a.DakStatus IN('DD','DA') AND a.ActionId='2' AND m.IsActive=1";
		@Override
		public long GetDailyExpectedPendingReplyCount() throws Exception {
			logger.info(new Date() + "Inside GetDailyPendingReplyCount");
			try {
			 Query query = manager.createNativeQuery(DAILYEXPECTEDPENDINGREPLYCOUNT);
			 BigInteger countResult = (BigInteger) query.getSingleResult();
		        return countResult.longValue();
			 } catch (Exception e) {
				 e.printStackTrace();
					logger.error(new Date() + "Inside DaoImpl GetDailyPendingReplyCount", e);
					return 0;
			    }
		}

		private static final String WEEKLYEXPECTEDPENDINGREPLYCOUNT="SELECT COUNT(DISTINCT m.EmpId) FROM dak_marking m  JOIN Dak a ON m.DakId = a.DakId WHERE m.ActionDueDate IS NOT NULL  AND DATE(m.ActionDueDate) >= CURDATE() - INTERVAL WEEKDAY(CURDATE()) DAY AND DATE(m.ActionDueDate) < CURDATE() + INTERVAL 7 - WEEKDAY(CURDATE()) DAY  AND m.EmpId NOT IN (SELECT r.EmpId FROM dak_reply r WHERE r.DakId = m.DakId)  AND a.DakStatus != 'DI' AND m.IsActive=1";
		@Override
		public long GetWeeklyExpectedPendingReplyCount() throws Exception {
			logger.info(new Date() + "Inside GetWeeklyExpectedPendingReplyCount");
			try {
			 Query query = manager.createNativeQuery(WEEKLYEXPECTEDPENDINGREPLYCOUNT);
			 BigInteger countResult = (BigInteger) query.getSingleResult();
		        return countResult.longValue();
			 } catch (Exception e) {
				 e.printStackTrace();
					logger.error(new Date() + "Inside DaoImpl GetWeeklyExpectedPendingReplyCount", e);
					return 0;
			    }
		}

		private static final String SUMMARYDISTRIBUTEDCOUNT="SELECT COUNT(DISTINCT m.EmpId) FROM dak_marking m INNER JOIN Dak a ON m.DakId = a.DakId AND DATE(a.DistributedDate) = CURDATE() AND  a.DistributedDate != 'DI' AND m.IsActive=1";
		@Override
		public long GetSummaryOfDailyDistributedCount() throws Exception {
			logger.info(new Date() + "Inside GetSummaryOfDailyDistributedCount");
			try {
				 Query query = manager.createNativeQuery(SUMMARYDISTRIBUTEDCOUNT);
				 BigInteger countResult = (BigInteger) query.getSingleResult();
			        return countResult.longValue();
				 } catch (Exception e) {
					    e.printStackTrace();
						logger.error(new Date() + "Inside DaoImpl GetSummaryOfDailyDistributedCount", e);
						return 0;
				    }
		}
		
		private static final String DAILYPENDINGREPLYEMPDATA = "SELECT m.DakId,m.EmpId,empData.EmpName,empData.Email,a.DakNo,sourceData.SourceShortName,a.ActionDueDate,empData.MobileNo,empData.DronaEmail FROM dak_marking m LEFT JOIN employee empData ON empData.EmpId=m.EmpId JOIN Dak a ON m.DakId=a.DakId LEFT JOIN dak_source_details sourceData ON sourceData.SourceDetailId=a.SourceDetailId WHERE a.ReceiptDate IS NOT NULL AND DATE(a.ReceiptDate) >='2023-12-01' AND m.DakId NOT IN (SELECT r.DakId FROM dak_reply r WHERE r.DakId=m.DakId) AND a.DakStatus IN('DD','DA') AND a.ActionId='2' AND m.IsActive=1";
		@Override
		public List<Object[]> GetDailyPendingReplyEmpData() throws Exception{
			logger.info(new Date() + "Inside DAO GetDailyPendingReplyEmpData");
			try {
			Query query = manager.createNativeQuery(DAILYPENDINGREPLYEMPDATA);
			List<Object[]> GetDailyPendingReplyEmpData = query.getResultList();
			return GetDailyPendingReplyEmpData;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl GetDailyPendingReplyEmpData", e);
				return null;
			}
		}
		
		@Override
		public long InsertMailTrackInsights(DakMailTrackingInsights Model)throws Exception{
			logger.info(new Date() + "Inside DAO InsertMailTrackInsights");
			try {
				manager.persist(Model);
				manager.flush();
				return Model.getMailTrackingInsightsId();
            } catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl InsertMailTrackInsights", e);
				return 0;
			}
		}
		
		private static final String UPDATEPARTICULAREMPMAILSTATUS = "UPDATE dak_mail_track_insights SET MailStatus=:mailstatus,MailSentDate= CAST(CURRENT_TIMESTAMP AS DATETIME) WHERE MailPurpose=:mailpurpose AND EmpId=:mailempid AND MailTrackingId=:mailtrackingid ";
		@Override
		public long UpdateParticularEmpMailStatus(String MailPurpose,String MailStatus,long empId,long MailTrackingId)  {
			logger.info(new Date() + "Inside UpdateParticularEmpMailStatus");
			try {
				Query query = manager.createNativeQuery(UPDATEPARTICULAREMPMAILSTATUS);
			    query.setParameter("mailpurpose", MailPurpose);
			    query.setParameter("mailstatus", MailStatus);
			    query.setParameter("mailempid", empId);
			    query.setParameter("mailtrackingid", MailTrackingId);
				return query.executeUpdate();
			 } catch (Exception e) {
				 e.printStackTrace();
					logger.error(new Date() + "Inside DaoImpl UpdateParticularEmpMailStatus", e);
					return 0;
			    }
		}
		
		private static final String UPDATEWEEKLYMAILTRACK = "UPDATE dak_mail_track SET MailSentCount=:successcount,MailSentStatus='S',MailSentDateTime= CAST(CURRENT_TIMESTAMP AS DATETIME) WHERE MailTrackingId=:mailtrackingid AND CreatedDate=CURDATE() AND TrackingType=:trackingtype  ";
		@Override
		public long UpdateDakMailTrackRow(long MailTrackingId,long SuccessCount,String TrackingType)throws Exception{
			logger.info(new Date() + "Inside DAO UpdateDakMailTrackRow");
			try {
				Query query = manager.createNativeQuery(UPDATEWEEKLYMAILTRACK);
				 query.setParameter("mailtrackingid", MailTrackingId);
			    query.setParameter("successcount", SuccessCount);
			    query.setParameter("trackingtype", TrackingType);
				return query.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl UpdateDakMailTrackRow", e);
				return 0;
			}
		}
		
		private static final String UPDATENOPENDINGREPLY = "UPDATE dak_mail_track SET MailSentCount=0 AND MailSentStatus='NA' WHERE CreatedDate=CURDATE() AND TrackingType=:trackingtype ";
		@Override
		public long UpdateNoPendingReply(String TrackingType)throws Exception{
			logger.info(new Date() + "Inside DAO UpdateNoPendingReply");
			try {
				Query query = manager.createNativeQuery(UPDATENOPENDINGREPLY);
			    query.setParameter("trackingtype", TrackingType);
				return query.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl UpdateNoPendingReply", e);
				return 0;
			}
		}
		
		private static final String SUMMARYDISTRIBUTEDEMPDATA = "SELECT m.DakId,m.EmpId,empData.EmpName,empData.Email,a.DakNo,sourceData.SourceShortName,a.ActionDueDate,empData.DronaEmail FROM dak_marking m LEFT  JOIN employee empData ON empData.EmpId=m.EmpId  INNER JOIN dak a ON m.DakId=a.DakId AND DATE(a.DistributedDate) = CURDATE() AND  a.DistributedDate != 'DI' LEFT  JOIN dak_source_details sourceData ON sourceData.SourceDetailId=a.SourceDetailId WHERE m.IsActive=1";
		@Override
		public List<Object[]> GetSummaryDistributedEmpData() throws Exception{
			logger.info(new Date() + "Inside DAO GetSummaryDistributedEmpData");
			try {
			Query query = manager.createNativeQuery(SUMMARYDISTRIBUTEDEMPDATA);
			List<Object[]> GetSummaryEmpData = query.getResultList();
			return GetSummaryEmpData;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl GetSummaryDistributedEmpData", e);
				return null;
			}
		}
		
		
		private static final String WEEKLYPENDINGREPLYEMPDATA = "SELECT  m.DakId,m.EmpId,empData.EmpName,empData.Email,a.DakNo, sourceData.SourceShortName,a.ActionDueDate,empData.DronaEmail  FROM dak_marking m LEFT JOIN employee empData ON empData.EmpId = m.EmpId JOIN Dak a ON m.DakId = a.DakId LEFT JOIN dak_source_details sourceData ON sourceData.SourceDetailId = a.SourceDetailId WHERE  m.ActionDueDate IS NOT NULL AND DATE(m.ActionDueDate) >= CURDATE() - INTERVAL WEEKDAY(CURDATE()) DAY AND DATE(m.ActionDueDate) < CURDATE() + INTERVAL 7 - WEEKDAY(CURDATE()) DAY  AND m.EmpId NOT IN (SELECT r.EmpId FROM dak_reply r WHERE r.DakId = m.DakId)  AND a.DakStatus != 'DI' AND m.IsActive=1 ORDER BY ActionDueDate;";   
		@Override
		public List<Object[]> GetWeeklyPendingReplyEmpData() throws Exception{
			logger.info(new Date() + "Inside DAO GetWeeklyPendingReplyEmpData");
			try {
			Query query = manager.createNativeQuery(WEEKLYPENDINGREPLYEMPDATA);
			List<Object[]> GetWeeklyPendingReplyEmpData = query.getResultList();
			return GetWeeklyPendingReplyEmpData;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl GetWeeklyPendingReplyEmpData", e);
				return null;
			}
		}
		
		private static final String SMSINTIATEDCOUNT="SELECT COUNT(*) FROM dak_sms_track WHERE CreatedDate = CURDATE() AND SmsTrackingType=:smsTrackingType";
		@Override
		public long GetSMSInitiatedCount(String smsTrackingType) throws Exception {
			logger.info(new Date() + "Inside GetSMSInitiatedCount");
			try {
			Query query = manager.createNativeQuery(SMSINTIATEDCOUNT);
		    query.setParameter("smsTrackingType", smsTrackingType);
		    BigInteger countResult = (BigInteger) query.getSingleResult();
	        return countResult.longValue();
		  } catch (Exception e) {
			 e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl GetSMSInitiatedCount", e);
				return 0;
		    }
		}
		
		@Override
		public long InsertSmsTrackRow(DakSmsTracking model) throws Exception {
			logger.info(new Date() + "Inside DAO InsertSmsTrackRow");
			try {
				manager.persist(model);
				manager.flush();
				return model.getSmsTrackingId();
            } catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl InsertSmsTrackRow", e);
				return 0;
			}
		}
		
		private static final String DAKCOUNTS="SELECT(SELECT COUNT(*) FROM dak a,dak_marking b WHERE a.DakStatus IN ('DD','DA') AND a.DakId=b.DakId AND b.EmpId=:empId AND a.ActionId='2' AND a.ReceiptDate >='2023-12-01' AND a.DakId NOT IN (SELECT r.DakId FROM dak_reply r WHERE r.DakId=a.DakId)) AS 'Dak Pending',(SELECT COUNT(*) FROM dak a,dak_marking b WHERE a.PriorityId='3' AND a.DakStatus IN ('DD','DA') AND a.DakId=b.DakId AND b.EmpId=:empId AND a.ActionId='2' AND a.ReceiptDate >='2023-12-01' AND a.DakId NOT IN (SELECT r.DakId FROM dak_reply r WHERE r.DakId=a.DakId))AS 'Urgent Daks',(SELECT COUNT(*) FROM dak a,dak_marking b WHERE a.DakStatus IN ('DD','DA') AND a.DakId=b.DakId AND b.EmpId=:empId AND a.ActionId='2' AND a.ReceiptDate >='2023-12-01' AND a.ActionDueDate IS NOT NULL AND DATE(a.ActionDueDate) = :actionDate AND  a.DakId NOT IN (SELECT r.DakId FROM dak_reply r WHERE r.DakId=a.DakId)) AS 'Today Pending',(SELECT COUNT(*) FROM dak a,dak_marking b WHERE a.DakStatus IN ('DD','DA') AND a.DakId=b.DakId AND b.EmpId=:empId AND a.ActionId='2' AND a.ReceiptDate >='2023-12-01' AND a.ActionDueDate IS NOT NULL AND DATE(a.ActionDueDate) < :actionDate AND  a.DakId NOT IN (SELECT r.DakId FROM dak_reply r WHERE r.DakId=a.DakId)) AS 'Dak Delay'";
		@Override
		public Object[] DakCounts(long empId, String actionDate) throws Exception {
			try {
				Query query = manager.createNativeQuery(DAKCOUNTS);
				query.setParameter("empId", empId);
				query.setParameter("actionDate", actionDate);
				return (Object[])query.getSingleResult();
				
			}
			catch(Exception e) {
				logger.error(new Date()  + "Inside DAO DakCounts " + e);
				return null;
			}
		}
		
		@Override
		public long InsertSmsTrackInsights(DakSmsTrackingInsights insights) throws Exception {
			logger.info(new Date() + "Inside DAO DakSmsTrackingInsights");
			try {
				manager.persist(insights);
				manager.flush();
				return insights.getSmsTrackingInsightsId();
            } catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl DakSmsTrackingInsights", e);
				return 0;
			}
		}
		
		private static final String UPADTEDAKSMSTRACKROW="UPDATE dak_sms_track SET SmsSentCount=:successCount,SmsSentStatus='S',SmsSentDateTime= CAST(CURRENT_TIMESTAMP AS DATETIME) WHERE SmsTrackingId=:smsTrackingId AND CreatedDate=CURDATE() AND SmsTrackingType=:trackingType ";
		@Override
		public long UpdateDakSmsTrackRow(long smsTrackingId, long successCount, String trackingType) throws Exception {
			logger.info(new Date() + "Inside DAO UpdateDakSmsTrackRow");
			try {
				Query query = manager.createNativeQuery(UPADTEDAKSMSTRACKROW);
				 query.setParameter("smsTrackingId", smsTrackingId);
			    query.setParameter("successCount", successCount);
			    query.setParameter("trackingType", trackingType);
				return query.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl UpdateDakSmsTrackRow", e);
				return 0;
			}
		}
		
		private static final String UPDATENOSMSPENDINGREPLY="UPDATE dak_sms_track SET SmsSentCount=0 AND SmsSentStatus='NA' WHERE CreatedDate=CURDATE() AND SmsTrackingType=:trackingType ";
		@Override
		public long UpdateNoSmsPendingReply(String trackingType) throws Exception {
			logger.info(new Date() + "Inside DAO UpdateNoSmsPendingReply");
			try {
				Query query = manager.createNativeQuery(UPDATENOSMSPENDINGREPLY);
			    query.setParameter("trackingType", trackingType);
				return query.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl UpdateNoSmsPendingReply", e);
				return 0;

			}
		}
		
		private static final String DIRECTORDAILYPENDINGREPLYEMPDATA="SELECT  e.empid,e.mobileno FROM employee e,lab_master b WHERE b.labcode=:Lab AND e.empid=b.LabAuthorityId AND e.isactive='1'";
		@Override
		public List<Object[]> GetDirectorDailyPendingReplyEmpData(String Lab) throws Exception {
			logger.info(new Date() + "Inside DAO GetDirectorDailyPendingReplyEmpData");
			try {
			Query query = manager.createNativeQuery(DIRECTORDAILYPENDINGREPLYEMPDATA);
			query.setParameter("Lab", Lab);
			List<Object[]> GetDailyPendingReplyEmpData = query.getResultList();
			return GetDailyPendingReplyEmpData;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl GetDirectorDailyPendingReplyEmpData", e);
				return null;
			}
		}

		private static final String DIRECTORDAKCOUNTS="SELECT(SELECT COUNT(*) FROM dak a WHERE a.DakStatus IN ('DD','DA') AND a.ActionId='2' AND a.ReceiptDate >='2023-12-01' AND a.DakId NOT IN (SELECT r.DakId FROM dak_reply r WHERE r.DakId=a.DakId)) AS 'Dak Pending',(SELECT COUNT(*) FROM dak a WHERE a.PriorityId='3' AND a.DakStatus IN ('DD','DA') AND a.ActionId='2' AND a.ReceiptDate >='2023-12-01' AND a.DakId NOT IN (SELECT r.DakId FROM dak_reply r WHERE r.DakId=a.DaKId))AS 'Urgent Daks',(SELECT COUNT(*) FROM dak a WHERE a.DakStatus IN ('DD','DA') AND a.ActionId='2' AND a.ReceiptDate >='2023-12-01' AND a.ActionDueDate IS NOT NULL AND DATE(a.ActionDueDate) = :ActionDueDate AND a.DakId NOT IN (SELECT r.DakId FROM dak_reply r WHERE r.DakId=a.DakId)) AS 'Today Pending',(SELECT COUNT(*) FROM dak a WHERE a.DakStatus IN ('DD','DA') AND a.ActionId='2' AND a.ReceiptDate >='2023-12-01' AND a.ActionDueDate IS NOT NULL AND DATE(a.ActionDueDate) < :ActionDueDate AND a.DakId NOT IN (SELECT r.DakId FROM dak_reply r WHERE r.DakId=a.DakId)) AS 'Dak Delay'";
		@Override
		public Object[] DirectorDakCounts(String ActionDueDate) throws Exception {
			try {
				Query query = manager.createNativeQuery(DIRECTORDAKCOUNTS);
				query.setParameter("ActionDueDate", ActionDueDate);
				return (Object[])query.getSingleResult();
			}
			catch(Exception e) {
				logger.error(new Date()  + "Inside DAO DirectorDakCounts " + e);
				return null;
			}
		}
		
		private static final String SMSREPORTLIST="SELECT e.EmpName,d.Designation,a.DakPendingCount,a.DakUrgentCount,a.DakTodayPending,a.DakDelayCount,e.MobileNo,a.Message,DATE(a.SmsSentDate) FROM dak_sms_track_insights a,employee e,employee_desig d WHERE a.EmpId=e.EmpId AND e.DesigId=d.DesigId AND DATE(a.SmsSentDate) BETWEEN :fromDate AND :toDate";
		@Override
		public List<Object[]> SmsReportList(String fromDate, String toDate) throws Exception {
			logger.info(new Date() +"Inside the SmsReportList");
			try {
				Query query=manager.createNativeQuery(SMSREPORTLIST);
				query.setParameter("fromDate", fromDate);
				query.setParameter("toDate", toDate);
				List<Object[]> smsreportlist=(List<Object[]>)query.getResultList();
				return smsreportlist;
			} catch (Exception e) {
				logger.error(new Date()+"Inside the SmsReportList");
				e.printStackTrace();
				return null;
			}
			
		}
		
		private static final String PMSSMSINTIATEDCOUNT="SELECT COUNT(*) FROM pfms_sms_track WHERE CreatedDate = CURDATE() AND SmsTrackingType=:smsTrackingType";
		@Override
		public long GetPmsSMSInitiatedCount(String smsTrackingType) throws Exception {
			logger.info(new Date() + "Inside GetPmsSMSInitiatedCount");
			try {
			Query query = manager.createNativeQuery(PMSSMSINTIATEDCOUNT);
		    query.setParameter("smsTrackingType", smsTrackingType);
		    BigInteger countResult = (BigInteger) query.getSingleResult();
	        return countResult.longValue();
		  } catch (Exception e) {
			 e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl GetPmsSMSInitiatedCount", e);
				return 0;
		    }
		}
		
		@Override
		public long InsertPmsSmsTrackRow(PfmsSmsTracking model) throws Exception {
			logger.info(new Date() + "Inside DAO InsertPmsSmsTrackRow");
			try {
				manager.persist(model);
				manager.flush();
				return model.getSmsTrackingId();
	        } catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl InsertPmsSmsTrackRow", e);
				return 0;
			}
		}
		
		private static final String DAILYPENDINGASSIGNEMPDATA="SELECT a.ActionAssignId,a.Assignee,empData.EmpName,empData.Email,empData.MobileNo,b.ActionItem,a.EndDate FROM action_assign a LEFT JOIN employee empData ON empData.EmpId=a.Assignee JOIN action_main b ON a.ActionMainId=b.ActionMainId WHERE a.ActionStatus='A' AND a.IsActive=1";
		@Override
		public List<Object[]> GetDailyPendingAssigneeEmpData() throws Exception {
			logger.info(new Date() + "Inside DAO GetDailyPendingAssigneeEmpData");
			try {
			Query query = manager.createNativeQuery(DAILYPENDINGASSIGNEMPDATA);
			List<Object[]> GetDailyPendingReplyEmpData = query.getResultList();
			return GetDailyPendingReplyEmpData;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl GetDailyPendingAssigneeEmpData", e);
				return null;
			}
		}
		
		private static final String ACTIONASSIGNEDCOUNTS="CALL Pfms_ActionSmsCount(:empId)";
		@Override
		public List<Object[]> ActionAssignedCounts(long empId) throws Exception {
			logger.info(new Date() + "Inside DAO ActionAssignedCounts");
			try {
			Query query = manager.createNativeQuery(ACTIONASSIGNEDCOUNTS);
			query.setParameter("empId", empId);
			List<Object[]> ActionAssignedCounts = query.getResultList();
			return ActionAssignedCounts;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl ActionAssignedCounts", e);
				return null;
			}
		}
		
		@Override
		public long InsertPmsSmsTrackInsights(PfmsSmsTrackingInsights insights) throws Exception {
			logger.info(new Date() + "Inside DAO PfmsSmsTrackingInsights");
			try {
				manager.persist(insights);
				manager.flush();
				return insights.getSmsTrackingInsightsId();
	        } catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl PfmsSmsTrackingInsights", e);
				return 0;
			}
		}
		
		private static final String UPADTEACTIONSMSTRACKROW="UPDATE pfms_sms_track SET SmsSentCount=:successCount,SmsSentStatus='S',SmsSentDateTime= CAST(CURRENT_TIMESTAMP AS DATETIME) WHERE SmsTrackingId=:smsTrackingId AND CreatedDate=CURDATE() AND SmsTrackingType=:trackingType ";
		@Override
		public long UpdateDakActionTrackRow(long smsTrackingId, long successCount, String trackingType) throws Exception {
			logger.info(new Date() + "Inside DAO UpdateDakSmsTrackRow");
			try {
				Query query = manager.createNativeQuery(UPADTEACTIONSMSTRACKROW);
				 query.setParameter("smsTrackingId", smsTrackingId);
			    query.setParameter("successCount", successCount);
			    query.setParameter("trackingType", trackingType);
				return query.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl UpdateDakSmsTrackRow", e);
				return 0;
			}
		}
		
		private static final String UPDATENOPMSSMSPENDINGREPLY="UPDATE pfms_sms_track SET SmsSentCount=0 AND SmsSentStatus='NA' WHERE CreatedDate=CURDATE() AND SmsTrackingType=:trackingType ";
		@Override
		public long UpdateNoPmsSmsPendingReply(String trackingType) throws Exception {
			logger.info(new Date() + "Inside DAO UpdateNoPmsSmsPendingReply");
			try {
				Query query = manager.createNativeQuery(UPDATENOPMSSMSPENDINGREPLY);
			    query.setParameter("trackingType", trackingType);
				return query.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl UpdateNoPmsSmsPendingReply", e);
				return 0;

			}
		}
		
		private static final String DIRECTORDAILYPENDINGASSIGNEMPDATA="SELECT e.empid,e.mobileno FROM employee e,lab_master b WHERE b.labcode=:Lab AND e.empid=b.LabAuthorityId AND e.isactive='1'";
		@Override
		public List<Object[]> GetDirectorDailyPendingAssignEmpData(String lab) throws Exception {
			logger.info(new Date() + "Inside DAO GetDirectorDailyPendingAssignEmpData");
			try {
			Query query = manager.createNativeQuery(DIRECTORDAILYPENDINGASSIGNEMPDATA);
			query.setParameter("Lab", lab);
			List<Object[]> GetDailyPendingReplyEmpData = query.getResultList();
			return GetDailyPendingReplyEmpData;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl GetDirectorDailyPendingAssignEmpData", e);
				return null;
			}
		}
		
		private static final String DIRECTORACTIONASSIGNEDCOUNTS="CALL Pfms_Director_ActionSmsCount()";
		@Override
		public List<Object[]> DirectorActionAssignedCounts() throws Exception {
			logger.info(new Date() + "Inside DAO DirectorActionAssignedCounts");
			try {
			Query query = manager.createNativeQuery(DIRECTORACTIONASSIGNEDCOUNTS);
			List<Object[]> DirectorActionAssignedCounts = query.getResultList();
			return DirectorActionAssignedCounts;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl DirectorActionAssignedCounts", e);
				return null;
			}
		}
		
		private static final String COMMITTESMSINTIATEDCOUNT="SELECT COUNT(*) FROM pfms_sms_committe_track WHERE CreatedDate = CURDATE() AND SmsTrackingType=:smsTrackingType";
		@Override
		public long GetCommitteSMSInitiatedCount(String smsTrackingType) throws Exception {
			logger.info(new Date() + "Inside GetCommitteSMSInitiatedCount");
			try {
			Query query = manager.createNativeQuery(COMMITTESMSINTIATEDCOUNT);
		    query.setParameter("smsTrackingType", smsTrackingType);
		    BigInteger countResult = (BigInteger) query.getSingleResult();
	        return countResult.longValue();
		  } catch (Exception e) {
			 e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl GetCommitteSMSInitiatedCount", e);
				return 0;
		    }
		}
		
		
		private static final String DAILYCOMMITTECOUNT="SELECT COUNT(DISTINCT a.EmpId) FROM committee_schedules_invitation a, committee_schedule b ,committee c WHERE a.CommitteeScheduleId=b.ScheduleId AND b.CommitteeId=c.CommitteeId AND b.ScheduleDate=:ScheduleDate AND b.IsActive=1";
		@Override
		public long dailyCommitteCount(String ScheduleDate) throws Exception {
			logger.info(new Date() + "Inside dailyCommitteCount");
			try {
			 Query query = manager.createNativeQuery(DAILYCOMMITTECOUNT);
			 query.setParameter("ScheduleDate", ScheduleDate);
			 BigInteger countResult = (BigInteger) query.getSingleResult();
		        return countResult.longValue();
			 } catch (Exception e) {
				 e.printStackTrace();
					logger.error(new Date() + "Inside DaoImpl dailyCommitteCount", e);
					return 0;
			    }
		}
		
		@Override
		public long InsertCommitteSmsTrackRow(PfmsCommitteSmsTracking model) throws Exception {
			logger.info(new Date() + "Inside DAO InsertCommitteSmsTrackRow");
			try {
				manager.persist(model);
				manager.flush();
				return model.getCommitteSmsTrackingId();
	        } catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl InsertCommitteSmsTrackRow", e);
				return 0;
			}
		}
		
		private static final String COMMITTEDETAILSTOSENDSMS="SELECT DISTINCT a.EmpId,e.MobileNo FROM committee_schedules_invitation a, committee_schedule b ,committee c ,employee e WHERE a.CommitteeScheduleId=b.ScheduleId AND b.CommitteeId=c.CommitteeId AND b.ScheduleDate=CURDATE() AND a.EmpId=e.EmpId AND b.IsActive=1 ";
		@Override
		public List<Object[]> GetCommitteEmpsDetailstoSendSms() throws Exception {
			logger.info(new Date() + "Inside DAO GetCommitteEmpsDetailstoSendSms");
			try {
			Query query = manager.createNativeQuery(COMMITTEDETAILSTOSENDSMS);
			List<Object[]> GetCommitteEmpsDetailstoSendSms = query.getResultList();
			return GetCommitteEmpsDetailstoSendSms;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl GetCommitteEmpsDetailstoSendSms", e);
				return null;
			}
		}
		
		private static final String GETCOMMITTEDATA="SELECT a.CommitteeScheduleId,b.ProjectId,c.CommitteeShortName,e.EmpName,e.MobileNo,b.ScheduleDate,b.ScheduleStartTime,c.ProjectApplicable FROM committee_schedules_invitation a,committee_schedule b,committee c,employee e WHERE a.CommitteeScheduleId=b.ScheduleId AND b.CommitteeId=c.CommitteeId AND b.ScheduleDate=CURDATE() AND a.EmpId=e.EmpId AND a.EmpId=:empId AND b.IsActive='1'";
		@Override
		public List<Object[]> getCommittedata(long empId) throws Exception {
			try {
				Query query = manager.createNativeQuery(GETCOMMITTEDATA);
				query.setParameter("empId", empId);
				List<Object[]> getCommittedata = (List<Object[]>)query.getResultList();
				return getCommittedata;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		public long InsertCommitteSmsTrackInsights(PfmsCommitteSmsTrackingInsights insights) throws Exception {
			logger.info(new Date() + "Inside DAO DakSmsTrackingInsights");
			try {
				manager.persist(insights);
				manager.flush();
				return insights.getCommitteSmsTrackingInsightsId();
	        } catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl DakSmsTrackingInsights", e);
				return 0;
			}
		}
		
		private static final String UPADTECOMMITTESMSTRACKROW="UPDATE pfms_sms_committe_track SET SmsSentCount=:successCount,SmsSentStatus='S',SmsSentDateTime= CAST(CURRENT_TIMESTAMP AS DATETIME) WHERE CommitteSmsTrackingId=:smsTrackingId AND CreatedDate=CURDATE() AND SmsTrackingType=:trackingType";
		@Override
		public long UpdateCommitteSmsTrackRow(long committeSmsTrackingId, long successCount, String trackingType) throws Exception {
			logger.info(new Date() + "Inside DAO UpdateCommitteSmsTrackRow");
			try {
				Query query = manager.createNativeQuery(UPADTECOMMITTESMSTRACKROW);
				 query.setParameter("smsTrackingId", committeSmsTrackingId);
			    query.setParameter("successCount", successCount);
			    query.setParameter("trackingType", trackingType);
				return query.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl UpdateCommitteSmsTrackRow", e);
				return 0;
			}
		}
		
		private static final String UPDATECOMMITTENOSMSPENDING="UPDATE pfms_sms_committe_track SET SmsSentCount=0 AND SmsSentStatus='NA' WHERE CreatedDate=CURDATE() AND SmsTrackingType=:trackingType ";
		@Override
		public long UpdateCommitteNoSmsPending(String trackingType) throws Exception {
			logger.info(new Date() + "Inside DAO UpdateCommitteNoSmsPending");
			try {
				Query query = manager.createNativeQuery(UPDATECOMMITTENOSMSPENDING);
			    query.setParameter("trackingType", trackingType);
				return query.executeUpdate();
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(new Date() + "Inside DaoImpl UpdateCommitteNoSmsPending", e);
				return 0;

			}
		}
		
		private static final String PMSSMSREPORTLIST="SELECT e.EmpName,d.Designation,a.ActionItemP,a.ActionItemTP,a.ActionItemDP,a.MilestoneActionP,a.MilestoneActionTP,a.MilestoneActionDP,a.MeetingActionP,a.MeetingActionTP,a.MeetingActionDP,e.MobileNo,a.Message,DATE(a.SmsSentDate) FROM pfms_sms_track_insights a,employee e,employee_desig d WHERE a.EmpId=e.EmpId AND e.DesigId=d.DesigId AND DATE(a.SmsSentDate) BETWEEN :fromDate AND :toDate";
		@Override
		public List<Object[]> PmsSmsReportList(String fromDate, String toDate) throws Exception {
			logger.info(new Date() +"Inside the PmsSmsReportList");
			try {
				Query query=manager.createNativeQuery(PMSSMSREPORTLIST);
				query.setParameter("fromDate", fromDate);
				query.setParameter("toDate", toDate);
				List<Object[]> PmsSmsReportList=(List<Object[]>)query.getResultList();
				return PmsSmsReportList;
			} catch (Exception e) {
				logger.error(new Date()+"Inside the PmsSmsReportList");
				e.printStackTrace();
				return null;
			}
		}
		
		private static final String SMSCOMMITTEREPORTLIST="SELECT e.EmpName,d.Designation,e.MobileNo,a.Message,DATE(a.SmsSentDate) FROM pfms_sms_committe_track_insights a,employee e,employee_desig d WHERE a.EmpId=e.EmpId AND e.DesigId=d.DesigId AND DATE(a.SmsSentDate) BETWEEN :fromDate AND :toDate";
		@Override
		public List<Object[]> SmsCommitteReportList(String fromDate, String toDate) throws Exception {
			logger.info(new Date() +"Inside the SmsCommitteReportList dao");
			try {
				Query query=manager.createNativeQuery(SMSCOMMITTEREPORTLIST);
				query.setParameter("fromDate", fromDate);
				query.setParameter("toDate", toDate);
				List<Object[]> SmsCommitteReportList=(List<Object[]>)query.getResultList();
				return SmsCommitteReportList;
			} catch (Exception e) {
				logger.info(new Date() +"Inside the SmsCommitteReportList dao");
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		public long ProjectHoaUpdate(ProjectHoa hoa)throws Exception{
			
			manager.merge(hoa);
			manager.flush();
			
			return hoa.getProjectHoaId();
		}
		
		private static final String PROJECTHOADELETE="DELETE FROM project_hoa WHERE labcode=:labcode";
		@Override
		public int ProjectHoaDelete(String LabCode) throws Exception{
			
			Query query = manager.createNativeQuery(PROJECTHOADELETE);
			query.setParameter("labcode", LabCode);

			return query.executeUpdate();
		}
		
		
		private static final String PROJECTLIST="SELECT a.projectid AS id,a.projectcode,a.projectname,a.projectmainid,a.projecttype AS 'project_director',a.projectdirector,a.sanctiondate,a.pdc FROM project_master a  WHERE  a.isactive=1 AND a.labcode =:LabCode";
		@Override
		public List<Object[]> ProjectList(String LabCode) throws Exception {
			
			Query query=manager.createNativeQuery(PROJECTLIST);
			query.setParameter("LabCode", LabCode);
			return (List<Object[]>) query.getResultList();
		}
		
		private static final String PROJECTHOACHANGESDELETE="DELETE FROM project_hoa_changes where projectid=:projectid";
		@Override
		public int ProjectHoaChangesDelete(String projectId) throws Exception {
			Query query = manager.createNativeQuery(PROJECTHOACHANGESDELETE);
			query.setParameter("projectid", projectId);
			int count =(int)query.executeUpdate();
			
			return count ;
		}
		
		@Override
		public long ProjectHoaChangesInsert(ProjectHoaChanges changes)throws Exception{
			
			manager.merge(changes);
			manager.flush();
			
			return 0L;
		}
		
		
		private static final String GETCLUSTERID="SELECT ClusterId FROM lab_master WHERE LabCode=:labCode";
		@Override
		public Object getClusterId(String labCode) throws Exception {
			try {
				Query query = manager.createNativeQuery(GETCLUSTERID);
				query.setParameter("labCode", labCode);
				Object getClusterId = query.getSingleResult();
				return getClusterId;
			} catch (Exception e) {
				e.printStackTrace();
		
				return null;
			}
		}
		
		private static final String CCMDATADELETE="DELETE FROM pfms_ccm_data where LabCode=:LabCode";
		@Override
		public int CCMDataDelete(String LabCode) throws Exception 
		{
			Query query = manager.createNativeQuery(CCMDATADELETE);
			query.setParameter("LabCode", LabCode);
			int count =(int)query.executeUpdate();
			
			return count ;
		}
		
		@Override
		public long CCMDataInsert(PFMSCCMData ccmdata )throws Exception
		{
			manager.persist(ccmdata);
			manager.flush();
			return ccmdata.getCCMDataId();
		}
		
		private static final String PROJECTHEALTHDELETE="DELETE FROM project_health where projectid=:projectid";
		@Override
		public int ProjectHealthDelete(String projectId) throws Exception {
			Query query = manager.createNativeQuery(PROJECTHEALTHDELETE);
			query.setParameter("projectid", projectId);
			int count =(int)query.executeUpdate();
			
			return count ;
		}
		
		
		private static final String PROJECTHEALTHINSERTDATA="CALL Project_Health_Insert_Data(:projectid)";
		@Override
		public Object[] ProjectHealthInsertData(String projectId) throws Exception {
			
			Query query = manager.createNativeQuery(PROJECTHEALTHINSERTDATA);
			query.setParameter("projectid", projectId);
			Object[] ProjectHealthTotalData= (Object[])query.getSingleResult();
			return ProjectHealthTotalData;
		}
		
		
		@Override
		public long ProjectHealthInsert(ProjectHealth health) throws Exception {
			manager.persist(health);

			manager.flush();

			return health.getProjectHealthId();
		}
}
