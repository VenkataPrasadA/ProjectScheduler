package com.ProjectScheduler.Controller;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.ProjectScheduler.DateTimeFormatUtil;
import com.ProjectScheduler.Service.ProjectSchedulerService;
import com.ProjectScheduler.dto.EmailDto;
import com.ProjectScheduler.dto.MailConfigurationDto;
import com.ProjectScheduler.dto.MeetingMailDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ProjectScheduler.Utility.*;
import com.ProjectScheduler.model.CCMView;
import com.ProjectScheduler.model.FinanceChanges;
import com.ProjectScheduler.model.IbasLabMaster;
import com.ProjectScheduler.model.ProjectHoa;

@Controller
public class ProjectSchedulerController {
	private static final Logger logger=LogManager.getLogger(ProjectSchedulerController.class);
	private  SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
	private  SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	ProjectSchedulerService service;
	
    @Autowired
    private Environment env;
    
    @Autowired
   	RestTemplate restTemplate;
    
    @Autowired
	PFMSServeFeignClient PFMSServ;
	
	@Value("${port}")
	private String port;
	
	@Value("${Dronaport}")
	private String Dronaport;
	
	@Value("${PmsTime1}")
	private String PmsTime1;
	
	@Value("${PmsTime2}")
	private String PmsTime2;
	
	@Value("${DmsTime1}")
	private String DmsTime1;
	
	@Value("${DmsTime2}")
	private String DmsTime2;
	
	@Value("${DmsTime3}")
	private String DmsTime3;
	
	@Value("${DmsWeekTime}")
	private String DmsWeekTime;
	
	@Value("${DmsSmsTime1}")
	private String DmsSmsTime1;
	
	@Value("${DmsSmsTime2}")
	private String DmsSmsTime2;
	
	@Value("${PmsSmsTime1}")
	private String PmsSmsTime1;
	
	@Value("${PmsSmsTime2}")
	private String PmsSmsTime2;
	
	@Value("${PmsSmsTime3}")
	private String PmsSmsTime3;
	
	@Value("${DmsSmsFileTime}")
	private String DmsSmsFileTime;
	
	
	@Value("${PmsSmsFileTime1}")
	private String PmsSmsFileTime1;
	
	@Value("${PmsSmsFileTime2}")
	private String PmsSmsFileTime2;
	
	@Value("${ProjectHoaTime}")
	private String ProjectHoaTime;
	
	@Value("${ProjectHealthTime}")
	private String ProjectHealthTime;
	
	@Value("${LocalFilesDrive}")
	private String LocalFilesDrive;
	
	@Value("${InternetFilesDrive}")
	private String InternetFilesDrive;
	
	@Value("${LabCode}")
	private String LabCode;
	
	@Value("${server_uri}")
    private String uri;
	
	private String username;
	
	private String host;
	
	private String password;
	
	private String Dronausername;
	
	private String Dronahost;
	
	private String Dronapassword;
	
	MailConfigurationDto dto1=new MailConfigurationDto(); 
	MailConfigurationDto dto2=new MailConfigurationDto(); 
	
	
	@Scheduled(cron = "${DmsTime1}")
	public void reporttodayCurrentTime() throws Exception {
		System.out.println("Time is for reporttodayCurrentTime - "+LocalDate.now().toString());
		try {
			myDailyPendingScheduledMailTask("today");
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
	
	
	@Scheduled(cron = "${DmsTime2}")
	public void reportCurrentTime() throws Exception {
		System.out.println("Time is for reportCurrentTime - "+LocalDate.now().toString());
		try {
			myDailyPendingScheduledMailTask("tommorrow");
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
	
	
	@Scheduled(cron = "${DmsTime3}")
	public void Summaryreport() throws Exception {
		System.out.println("Time is for Summaryreport - "+LocalDate.now().toString());
	    try {
			mySummaryDistributedMailTask();
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
	
	
	//@Scheduled(cron = "${DmsWeekTime}")
	@Scheduled(cron = "0 49 11 * * *")
	public void Weeklyreport() throws Exception {
		System.out.println("Time is for Weeklyreport - "+LocalDate.now().toString());
		try {
			myWeeklyScheduledMailTask();
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
	
	
	
	@GetMapping("/")
	public String welcome(HttpServletRequest req,HttpServletResponse res,HttpSession ses) {
		System.out.println("Time is "+LocalDate.now().toString());
		try {
			dto1=service.getMailConfigByTypeOfHost("L");
			username= dto1.getUsername().toString();
			password=dto1.getPassword();
			host=dto1.getHost();
			
			dto2=service.getMailConfigByTypeOfHost("D");
			Dronausername=dto2.getUsername().toString();
			Dronapassword=dto2.getPassword();
			Dronahost=dto2.getHost();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "Scheduler/MailScheduler";
	}
	
	
	@Scheduled(cron="${PmsTime1}")
	public void method1() {
		System.out.println("Time is for Method1 - "+LocalDate.now().toString());
		try {
			SentWeeklyActionMail(0);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Scheduled(cron="${PmsTime2}")
	public void method2() {
		System.out.println("Time is for Method2 - "+LocalDate.now().toString());
		try {
			SentWeeklyActionMail(7);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Async
	public void sendMeetingEmails() {
		
		long startTime = System.currentTimeMillis();
		String date = LocalDate.now().plusDays(1).toString();
		try {
			List<MeetingMailDto>meetingMailDtoData=new ArrayList<>();
			List<String>membertypes=Arrays.asList("CC","CS","PS","CI","P","I");
			List<Object[]>todayMeetings=service.getTodaysMeetings(date);
			List<Object[]>empAttendance = new ArrayList<>();
			if(todayMeetings.size()>0) {
				for(Object[]obj:todayMeetings) {
					empAttendance= service.CommitteeAtendance(obj[0].toString()).stream()
							.filter(e ->membertypes.contains(e[3].toString())).collect(Collectors.toList());
					
				for(Object[]obj1:empAttendance) {
					MeetingMailDto m = new MeetingMailDto();
					m.setEmpid(obj1[0].toString());
					m.setEmpname(obj1[6].toString());
					m.setEmail(obj1[8].toString());
					m.setScheduleid(obj[0].toString());
					m.setProjectid(obj[1].toString());
					m.setInitiationId(obj[2].toString());
					m.setCommitteeShortName(obj[3].toString());
					m.setCommitteeName(obj[4].toString());
					m.setMeetingTime(obj[6].toString());
					m.setMeetingVenue(obj[5].toString());
					m.setProjectname(obj[8].toString());
					m.setProjectCode(obj[8].toString());
					m.setDronaEmail(obj1[13].toString());
					meetingMailDtoData.add(m);
				}
				}
				List<MeetingMailDto> meetingMailDtoSubData = new ArrayList<>();
				
				int SentmailCount=0;
				
				while(meetingMailDtoData.size()!=0) {
					String empid=meetingMailDtoData.get(0).getEmpid();
					String message="Sir/Madam<br><p>&emsp;&emsp;This is to inform you that you have "+meetingMailDtoSubData.size()+" meetings scheduled tomorrow.</p><table style=\"align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; max-width: 650px; font-size: 16px; border-collapse:collapse;\" >";
					meetingMailDtoSubData = meetingMailDtoData.stream().filter(e -> e.getEmpid().equalsIgnoreCase(empid)).collect(Collectors.toList());
					for(MeetingMailDto m : meetingMailDtoSubData) {
					  message=message+"<tr><th colspan=\"2\" style=\"text-align: left; font-weight: 700; width: 650px;border: 1px solid black; padding: 5px; padding-left: 15px\">Meeting Details </th></tr>"
									 +"<tr>"
									 + "<td style=\"border: 1px solid black; padding: 5px;text-align: left\"> Project : </td>"
									 + "<td style=\"border: 1px solid black; padding: 5px;text-align: left\"><b style=\"color:#0D47A1\">" + m.getProjectCode()+ "( "+m.getProjectname()+" )"  + "</b></td></tr>"
									 +"<tr>"
									 + "<td style=\"border: 1px solid black; padding: 5px;text-align: left\"> Meeting : </td>"
									 + "<td style=\"border: 1px solid black; padding: 5px;text-align: left\">" + m.getCommitteeShortName()  + "</td></tr>"
									 +"<tr><td style=\"border: 1px solid black; padding: 5px;text-align: left\"> Date :  </td>"
									 +"<td style=\"border: 1px solid black; padding: 5px;text-align: left\">" + LocalDate.now().plusDays(1).toString()+","+LocalDate.parse(LocalDate.now().plusDays(1).toString()).getDayOfWeek()+"</td></tr>"
									 +"<tr>"
									 + "<td style=\"border: 1px solid black; padding: 5px;text-align: left\"> Time : </td>"
									 + "<td style=\"border: 1px solid black; padding: 5px;text-align: left\">" + m.getMeetingTime()  + "</td></tr>"
									 +"<tr>"
									 + "<td style=\"border: 1px solid black; padding: 5px;text-align: left\"> Venue : </td>"
									 + "<td style=\"border: 1px solid black; padding: 5px;text-align: left\">" + m.getMeetingVenue()+ "</td></tr>";
					}
					 message=message
							 +"</table><p style=\"font-weight:bold;font-size:13px;\">[Note:This is an autogenerated e-mail.Reply to this will not be attended please.]</p>"
						     +"<p>Regards</p>"
						     +"<p>PMS Team"+"</p>";
					 
					 String email = meetingMailDtoSubData.get(0).getEmail();
					 String subject = "Tomorrow's Schedule Meetings";
					 
					 SentmailCount=sendMessage(email,subject,message);
					 meetingMailDtoData=meetingMailDtoData.stream().filter( e -> !e.getEmpid().equalsIgnoreCase(empid)).collect(Collectors.toList());
					
				}
				
				
			}
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		System.out.println("Elapsed time: " + elapsedTime + " milliseconds");
	}
	
	@Async
	public void SentWeeklyActionMail(int NumberOfDays) {
		long startTime = System.currentTimeMillis();
	try {
		List<Object[]>weeklyActionList = service.weeklyActionList(NumberOfDays);
		List<Object[]>weeklySubActionList = new ArrayList<>();
		if(weeklyActionList.size()>0) {
			while(weeklyActionList.size()!=0) {
				String empid = weeklyActionList.get(0)[3].toString();
				weeklySubActionList = weeklyActionList.stream().filter(e -> e[3].toString().equalsIgnoreCase(empid))
										.collect(Collectors.toList());
				String message="Sir/Madam ,<br><p>&emsp;&emsp;This is to inform you that you have some actions to be completed .</p><table style=\"align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; font-size: 16px; border-collapse:collapse;\" >";
				message= message+"<tr style=\"font-size:12px;\"><td style=\"border: 1px solid black; padding: 5px;text-align: center;width:5%;\">SN</td><td style=\"border: 1px solid black; padding: 5px;text-align: center; width:30%\">Action No.</td><td style=\"border: 1px solid black; padding: 5px;text-align: center;width:30%;\"> Assignor</td><td style=\"border: 1px solid black; padding: 5px;text-align: center;width:15%\">Progress</td><td style=\"border: 1px solid black; padding: 5px;text-align: center;width:15%\">PDC</td></tr>";
				int count=0;
				for (Object[]obj:weeklySubActionList) {
					message=message+"<tr style=\"font-size:12px;\"><td style=\"border: 1px solid black; padding: 5px;text-align: center;width:5%;\">"+(++count)+"</td><td style=\"border: 1px solid black; padding: 5px;text-align: center;width:30%;font-weight:600\">"+obj[0].toString() +"</td><td style=\"border: 1px solid black; padding: 5px;text-align: center;width:30%;\">"+ obj[1].toString()+"</td><td style=\"border: 1px solid black; padding: 5px;text-align: center;width:15%\">"+obj[4].toString()+"</td><td style=\"border: 1px solid black; padding: 5px;text-align: center;width:15%\">"+obj[5].toString()+",<br>"+LocalDate.parse(obj[5].toString()).getDayOfWeek()  +"</td></tr>";
				}
				message=message
						 +"</table><p style=\"font-weight:bold;font-size:13px;\">[Note:This is an autogenerated e-mail.Reply to this will not be attended please.]</p>"
					     +"<p>Regards</p>"
					     +"<p>PMS Team"+"</p>"
					     ;
				
				String email=weeklySubActionList.get(0)[2].toString();
				String subject="";
				if(NumberOfDays==0) {
				subject = "PMS - Actions PDC Today";
				}
				if(NumberOfDays==7) {
					subject = "PMS - Actions PDC This Week";
				}
				int mailsentCount=sendMessage(email, subject, message);
				
				weeklyActionList = weeklyActionList.stream().filter(e -> !e[3].toString().equalsIgnoreCase(empid)).collect(Collectors.toList());
			}
			
			
		}
		
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	long endTime = System.currentTimeMillis();
	long elapsedTime = endTime - startTime;
	System.out.println("Elapsed time: " + elapsedTime + " milliseconds");
		
	}
	
	
	
	
	
	public int sendMessage(String toEmail, String subject, String msg)  {
		Properties properties = System.getProperties();
	
		properties.setProperty("mail.smtp.host", host);
		properties.put("mail.smtp.starttls.enable", "true");
	
		properties.put("mail.smtp.port", port);
	
		properties.put("mail.smtp.auth", "true");
	
		properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
			
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		int mailSendresult = 0;
		
		try {
			
			MimeMessage message = new MimeMessage(session);
			
			message.setFrom(new InternetAddress(username));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
			message.setSubject(subject);
			message.setText(msg);
			message.setContent(msg, "text/html");// this code is used to make the message in HTML formatting
			
			Transport.send(message);
			System.out.println("Message Sent");
			mailSendresult++;
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
		return mailSendresult;
	}
	
	public int sendMessage1(String toEmail, String subject, String msg)  {
		Properties properties = System.getProperties();
	
		properties.setProperty("mail.smtp.host", Dronahost);
		properties.put("mail.smtp.starttls.enable", "true");
	
		properties.put("mail.smtp.port", Dronaport);
	
		properties.put("mail.smtp.auth", "true");
	
		Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
			
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(Dronausername, Dronapassword);
			}
		});
		int mailSendresult = 0;
		
		try {
			
			MimeMessage message = new MimeMessage(session);
			
			message.setFrom(new InternetAddress(Dronausername));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
			message.setSubject(subject);
			message.setText(msg);
			message.setContent(msg, "text/html");// this code is used to make the message in HTML formatting
			
			Transport.send(message);
			System.out.println("Message Sent");
			mailSendresult++;
		} catch (MessagingException mex) {
			mex.printStackTrace();
		}
		return mailSendresult;
	}
	    
	    @Async
	    public void myDailyPendingScheduledMailTask(String day) {
	      logger.info(new Date() + " Inside CONTROLLER myDailyPendingScheduledMailTask ");
	      try {
	          long MailTrackingId = 0;
	          long MailTrackingInsightsId = 0;
	          
	          // Create an AtomicInteger for thread-safe success count updates
	          
	          AtomicInteger mailSendSuccessCount = new AtomicInteger(0);

	          long DailyPendingMailSendInitiation = service.GetMailInitiatedCount("D");
	          
	          if (DailyPendingMailSendInitiation == 0) {
	          	MailTrackingId = service.InsertMailTrackInitiator("D");
	          }
	          
	          final long effectivelyFinalMailTrackingId = MailTrackingId;

	            //the list of daily pending reply details
	              List<Object[]> PendingReplyEmpsDetailstoSendMail = service.GetDailyPendingReplyEmpData();
	             if (MailTrackingId > 0 && PendingReplyEmpsDetailstoSendMail != null && PendingReplyEmpsDetailstoSendMail.size() > 0) {
	              	   System.out.println("PendingReplyEmpsDetailstoSendMail details " + PendingReplyEmpsDetailstoSendMail.size()+" And MailTrackingId is : "+effectivelyFinalMailTrackingId);
	              	   
	              	   MailTrackingInsightsId = service.InsertDailyPendingInsights(MailTrackingId);
	              	   
	              	   if(MailTrackingInsightsId > 0) {
	                  
	                            // Create a map to store unique EmpId, emails, DakNos, and Sources
	                             Map<Object, EmailDto> empToDataMap = new HashMap<>();
	      
	                            //iterate over the PendingReplyEmpsDetailstoSendMail and constructs a map empToDataMa) to group information by unique EmpId.
	                            //It collects the email addresses, DakNos, and Sources for each EmpId.  
	                  
	                           for (Object[] obj : PendingReplyEmpsDetailstoSendMail) {
	                      
	                  	         Object empId = obj[1];
	                               Object dakNo = obj[4];
	                               Object source = obj[5];
	                               Object dueDate = obj[6];
	                               String email = null;
	                               String DronalEmail=null;
	                               if(obj[3] != null && !obj[3].toString().trim().isEmpty()) {
	                      	            email = obj[3].toString();
	                      	     }
	                               
	                               if(obj[8] != null && !obj[8].toString().trim().isEmpty()) {
	                              	 DronalEmail = obj[8].toString();
	                   	         }

	                               if (empId != null && email != null && !email.isEmpty()) {
	                                   if (!empToDataMap.containsKey(empId)) {
	                          	     System.out.println("%%%%%%%%%%%%%%%email Data" +email);
	                                   empToDataMap.put(empId, new EmailDto(email,DronalEmail));
	                               }

	                               if (dakNo != null && !dakNo.toString().isEmpty()) {
	                                    empToDataMap.get(empId).addDakAndSourceAndDueDate(dakNo.toString(), source.toString(), dueDate.toString());
	                                }
	                              }
	                               
	                          }

	            
	                     // Iterate over the map and sends an email to each unique EmpId
	                       //It creates an HTML table in the email body to display the DakNos and Sources related to each EmpId
	                
	                        // After building the empToDataMap, iterate over it to send emails
	                       for (Map.Entry<Object, EmailDto> mailMapData : empToDataMap.entrySet()) {
	                         Object empId = mailMapData.getKey();
	                         EmailDto emailData = mailMapData.getValue();
	                         String email = emailData.getEmail();
	                         String DronaEmail=emailData.getDronaEmail();
	                          String dakCount;
	                          String word;
	                          int size = emailData.getDakAndSourceAndDueDateList().size();
	                            if(size>1) {
	                      	     dakCount = size+" DAKs";
	                      	     word ="replies";
	                            }else {
	                      	     dakCount = size+" DAK";
	                      	     word ="reply";
	                            }
	          
	                            if (email != null) {
	                                 // Create and format the email content
	                                 String currentDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
	                                 String subject = "DMS - Daily Pending Replies Report - " + currentDate;
	                                 String message = "<p>Dear Sir/Madam,</p>";
	                                        message += "<p></p>";
	                                        message += "<p>This email is to inform you that you have " +dakCount + " with actions due "+day+", awaiting your "+word+" to ensure timely completion.</p>";
	                                        //The timely completion of these tasks is crucial for our project's progress, so please prioritize them and ensure that your responses are provided as soon as possible.
	                                        message += "<p>This is for your information, please take the action.</p>";
	                                        // Generate the HTML table for DakNos and Sources
	                                        message += "<table style=\"align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; max-width: 650px; font-size: 16px; border-collapse: collapse;\">";
	                                        message += "<thead>";
	                                        message += "<tr>";
	                                        message += "<th style=\"text-align: center; width: 500px; border: 1px solid black; padding: 5px; padding-left: 15px\">DAK No</th>";
	                                        message += "<th style=\"text-align: center; width: 200px; border: 1px solid black; padding: 5px; padding-left: 15px\">Source</th>";
	                                        message += "</tr>";
	                                        message += "</thead>";
	                                        message += "<tbody>";
	                                      for (Object[] dakAndSource : emailData.getDakAndSourceAndDueDateList()) {
	                                        message += "<tr>";
	                                        message += "<td style=\"border: 1px solid black; padding: 5px; text-align: left\">" + dakAndSource[0] + "</td>";
	                                        message += "<td style=\"border: 1px solid black; padding: 5px; text-align: center\">" + dakAndSource[1] + "</td>";
	                                        message += "</tr>";
	                                      }
	                                        message += "</tbody>";
	                                        message += "</table>";
	                                        message += "Please <a href=\"" + env.getProperty("Login_link") + "\">Click Here</a> to Go DMS.<br>";
	                                        message += "<p>Important Note: This is an automated message. Kindly avoid responding.</p>";
	                                        message += "<p>Regards,<br>LRDE-DMS Team</p>";
	                                        System.out.println("dakAndSourceList sizeeeeeeeeeeeeee@@@@@@#$$"+size+"for empiddddd :"+empId+"for email  :"+email);
	                             
	                                       // Send the email asynchronously within the loop
	                                       int sendResult1 = sendMessage(email,subject, message);
	                                       int sendResult2 = sendMessage1(DronaEmail, subject, message);
	                                   if (sendResult1 > 0) {
	                                         service.UpdateParticularEmpMailStatus("D", "S", Long.parseLong(empId.toString()), effectivelyFinalMailTrackingId);
	                                         System.out.println("success");
	                                          mailSendSuccessCount.incrementAndGet(); // Increment success count atomically
	                                  } else {
	                                        service.UpdateParticularEmpMailStatus("D", "N", Long.parseLong(empId.toString()), effectivelyFinalMailTrackingId);
	                                        System.out.println("failure");
	                                  }
	                      }
	                  }
	                  service.updateMailSuccessCount(MailTrackingId, mailSendSuccessCount.get(), "D");
	            }   
	             } else {
	                  service.UpdateNoPendingReply("D");
	              }
	          
	      } catch (Exception e) {
	          e.printStackTrace();
	          logger.error(new Date() + " Inside CONTROLLER myDailyPendingScheduledMailTask " + e);
	      }
	  }
	    
	    
	    @Async
	    public void mySummaryDistributedMailTask() {
	      logger.info(new Date() + " Inside CONTROLLER mySummaryDistributedMailTask ");
	      try {
	         long MailTrackingId = 0;
	         long MailTrackingInsightsId = 0;
	         //Create an AtomicInteger for thread-safe success count updates
	         AtomicInteger mailSendSuccessCount = new AtomicInteger(0);

	         long SummaryMailSendInitiation = service.GetMailInitiatedCount("S");
	         if (SummaryMailSendInitiation == 0) {
	         	MailTrackingId = service.InsertMailTrackInitiator("S");
	         }
	         final long effectivelyFinalMailTrackingId = MailTrackingId;
	         
	         //the list of daily distributed details
	         List<Object[]> DailyDistributedSummarytoSendMail = service.GetSummaryDistributedEmpData();
	        if (MailTrackingId > 0 && DailyDistributedSummarytoSendMail != null && DailyDistributedSummarytoSendMail.size() > 0) {
	         		MailTrackingInsightsId = service.InsertSummaryDistributedInsights(MailTrackingId);
	                if(MailTrackingInsightsId > 0) {
	                    // Create a map to store unique EmpId, emails, DakNos, and Sources
	                     Map<Object, EmailDto> empToDataMap = new HashMap<>();
	                    //iterate over the DailyDistributedSummarytoSendMail and constructs a map empToDataMa) to group information by unique EmpId.
	                    //It collects the email addresses, DakNos, and Sources for each EmpId.  
	          
	                   for (Object[] obj : DailyDistributedSummarytoSendMail) {
	              
	          	         Object empId = obj[1];
	                       Object dakNo = obj[4];
	                       Object source = obj[5];
	                       String dueDate = null;
	                       if(obj[6] != null && !obj[6].toString().trim().isEmpty()) {
	                    	   SimpleDateFormat rdf=new SimpleDateFormat("dd-MM-yyyy");
	                    	   dueDate = rdf.format(obj[6]);
	                       	  
	                       }else {
	                    	   dueDate ="NA";
	                       }
	                       String email = null;
	                       String DronaEmail=null;
	                       
	                       if(obj[3] != null && !obj[3].toString().trim().isEmpty()) {
	              	            email = obj[3].toString();
	                       }
	                       
	                       if(obj[7] != null && !obj[7].toString().trim().isEmpty()) {
	                    	   DronaEmail = obj[7].toString();
	                      }

	                       if (empId != null && email != null && !email.isEmpty()) {
	                           if (!empToDataMap.containsKey(empId)) {
	                  	     System.out.println("%%%%%%%%%%%%%%%email Data" +email);
	                           empToDataMap.put(empId, new EmailDto(email,DronaEmail));
	                       }

	                       if (dakNo != null && !dakNo.toString().isEmpty()) {
	                            empToDataMap.get(empId).addDakAndSourceAndDueDate(dakNo.toString(), source.toString(), dueDate.toString());
	                        }
	                      }
	                  }
	   
	    
	             // Iterate over the map and sends an email to each unique EmpId
	               //It creates an HTML table in the email body to display the DakNos and Sources related to each EmpId
	        
	                // After building the empToDataMap, iterate over it to send emails
	               for (Map.Entry<Object, EmailDto> mailMapData : empToDataMap.entrySet()) {
	                 Object empId = mailMapData.getKey();
	                 EmailDto emailData = mailMapData.getValue();
	                 String email = emailData.getEmail();
	                 String DronaEmail=emailData.getDronaEmail();
	                  String dakCount;
	                  
	                  String word;
	                  int size = emailData.getDakAndSourceAndDueDateList().size();
	                    if(size>1) {
	              	     dakCount = size+" DAKs";
	              	     word ="replies";
	                    }else {
	              	     dakCount = size+" DAK";
	              	     word ="reply";
	                    }
	  
	                    if (email != null) {
	                         // Create and format the email content
	                         String currentDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
	                         String subject = "DMS -  Distributed Summary Report - " + currentDate;
	                         String message = "<p>Dear Sir/Madam,</p>";
	                                message += "<p></p>";
	                                message += "<p>This email is to notify you that you have received " +dakCount + " today.This is for your information, please take the action.</p>";
	                                // Generate the HTML table for DakNos and Sources
	                                message += "<table style=\"align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; max-width: 650px; font-size: 16px; border-collapse: collapse;\">";
	                                message += "<thead>";
	                                message += "<tr>";
	                                message += "<th style=\"text-align: center; width: 400px; border: 1px solid black; padding: 5px; padding-left: 15px\">DAK No</th>";
	                                message += "<th style=\"text-align: center; width: 200px; border: 1px solid black; padding: 5px; padding-left: 15px\">Source</th>";
	                                message += "<th style=\"text-align: center; width: 200px; border: 1px solid black; padding: 5px; padding-left: 15px\">Due Date</th>";
	                                message += "</tr>";
	                                message += "</thead>";
	                                message += "<tbody>";
	                             
	                                for (Object[] dakAndSource : emailData.getDakAndSourceAndDueDateList()) {
	                                  message += "<tr>";
	                                  message += "<td style=\"border: 1px solid black; padding: 5px; text-align: left\">"   + dakAndSource[0] + "</td>";
	                                  message += "<td style=\"border: 1px solid black; padding: 5px; text-align: center\">" + dakAndSource[1] + "</td>";
	                                  message += "<td style=\"border: 1px solid black; padding: 5px; text-align: center\">" + dakAndSource[2] + "</td>";
	                                  message += "</tr>";
	                               }
	                                message += "</tbody>";
	                                message += "</table>";
	                                message += "Please <a href=\"" + env.getProperty("Login_link") + "\">Click Here</a> to Go DMS.<br>";
	                                message += "<p>Important Note: This is an automated message. Kindly avoid responding.</p>";
	                                message += "<p>Regards,<br>LRDE-DMS Team</p>";
	                                System.out.println("dakAndSourceList sizeeeeeeeeeeeeee@@@@@@#$$"+size+"for empiddddd :"+empId+"for email  :"+email);
	                               int sendResult1 = sendMessage(email,subject, message);
	                               int sendResult2 = sendMessage1(DronaEmail, subject, message);
	                               if (sendResult1 > 0) {
	                                 // Successfully sent
	                                 service.UpdateParticularEmpMailStatus("S", "S", Long.parseLong(empId.toString()), effectivelyFinalMailTrackingId);
	                                 System.out.println("success");
	                                  mailSendSuccessCount.incrementAndGet(); 
	                              } else {
	                                // Failed to send
	                                service.UpdateParticularEmpMailStatus("S", "N", Long.parseLong(empId.toString()), effectivelyFinalMailTrackingId);
	                                System.out.println("failure");
	                          }
	              }
	          }
	          service.updateMailSuccessCount(MailTrackingId, mailSendSuccessCount.get(), "S");
	      
	    }   
	        } else {
	             service.UpdateNoPendingReply("S");
	         }
	         } catch (Exception e) {
	           e.printStackTrace();
	            logger.error(new Date() + " Inside CONTROLLER mySummaryDistributedMailTask " + e);
	       }
	 
	   }
	    
	    
	    @Async// Runs at every week Monday morning by taking data from current Monday to current Sunday
	   	public void myWeeklyScheduledMailTask() {
	   	     
	       	  logger.info(new Date() + " Inside CONTROLLER myWeeklyScheduledMailTask ");
	             try {
	            	
	            	 
	            	 long MailTrackingId = 0;
	                 long MailTrackingInsightsId = 0;
	                 // Create an AtomicInteger for thread-safe success count updates
	                   AtomicInteger mailSendSuccessCount = new AtomicInteger(0);

	                   long WeeklyMailSendInitiation = service.GetMailInitiatedCount("W");
	                   System.out.println("WeeklyPendingMailSendInitiation RESULTTTT" + WeeklyMailSendInitiation);
	                   
	                   if (WeeklyMailSendInitiation == 0) {
	                   	MailTrackingId = service.InsertMailTrackInitiator("W");
	                   }
	                   
	                   final long effectivelyFinalMailTrackingId = MailTrackingId;

	    
	                       //the list of daily pending reply details
	                       List<Object[]> PendingReplyEmpsDetailstoSendMail = service.GetWeeklyPendingReplyEmpData();
	                      
	                       if (MailTrackingId > 0 && PendingReplyEmpsDetailstoSendMail != null && PendingReplyEmpsDetailstoSendMail.size() > 0) {
	                    		System.out.println("PendingReplyEmpsDetailstoSendMail details " + PendingReplyEmpsDetailstoSendMail.size()+" And MailTrackingId is : "+effectivelyFinalMailTrackingId);
	                          	MailTrackingInsightsId = service.InsertWeeklyPendingInsights(MailTrackingId);
	                          	System.out.println("And MailTrackingInsightsId is : "+MailTrackingInsightsId);
	                            if (MailTrackingInsightsId>0) {
	                       	 // Create a map to store unique EmpId, emails, DakNos, and Sources
	                            Map<Object, EmailDto> empToDataMap = new HashMap<>();
	               
	                         //iterate over the PendingReplyEmpsDetailstoSendMail and constructs a map empToDataMa) to group information by unique EmpId.
	                         //It collects the email addresses, DakNos, and Sources for each EmpId.  
	                            
	                           for (Object[] obj : PendingReplyEmpsDetailstoSendMail) {
	                               
	                           	   Object empId = obj[1];
	                               Object dakNo = obj[4];
	                               Object source = obj[5];
	                               
	                               			String dueDate = null;
				                            if(obj[6] != null && !obj[6].toString().trim().isEmpty()) {
				                            	 SimpleDateFormat rdf=new SimpleDateFormat("dd-MM-yyyy");
				                            	 dueDate = rdf.format(obj[6]);
				                            }else {
				                            	   dueDate ="--";
				                            }
	                               
			                               String email = null;
			                               String DronaEmail=null;
			                               if(obj[3] != null && !obj[3].toString().trim().isEmpty()) {
			                               	  email = obj[3].toString();
			                               }
	                               
			                               if(obj[7] != null && !obj[7].toString().trim().isEmpty()) {
			                            	   DronaEmail = obj[7].toString();
			                               }
	                               
			                               if (empId != null && email != null && !email.isEmpty()) {
			                                   if (!empToDataMap.containsKey(empId)) {
			                                   	System.out.println("%%%%%%%%%%%%%%%email Data" +email);
			                                       empToDataMap.put(empId, new EmailDto(email,DronaEmail));
			                               }

			                               if (dakNo != null && !dakNo.toString().isEmpty()) {
			                                   empToDataMap.get(empId).addDakAndSourceAndDueDate(dakNo.toString(), source.toString(),dueDate.toString());
			                               }
			                          }
	                             }


	                           // Iterate over the map and sends an email to each unique EmpId
	                           //It creates an HTML table in the email body to display the DakNos and Sources related to each EmpId
	                            
	                           // After building the empToDataMap, iterate over it to send emails
	                              for (Map.Entry<Object, EmailDto> mailMapData : empToDataMap.entrySet()) {
	                                  Object empId = mailMapData.getKey();
	                                  EmailDto emailData = mailMapData.getValue();
	                                  String email = emailData.getEmail();
	                                  String DronaEmail=emailData.getDronaEmail();
	                                  String dakCount;
	                                  String word;
	                                  int size = emailData.getDakAndSourceAndDueDateList().size();
	                                  if(size>1) {
	                                  	dakCount = size+" DAKs";
	                                  	word ="replies";
	                                  }else {
	                                  	dakCount = size+" DAK";
	                                  	word ="reply";
	                                  }
	                      
	                                  if (email != null) {
	                                      // Create and format the email content
	                                      String subject = "DMS -  Weekly Pending Replies Report from " + DateTimeFormatUtil.getCurrentWeekMonday()+" to "+ DateTimeFormatUtil.getCurrentWeekSunday();
	                                      String message = "<p>Dear Sir/Madam,</p>";
	                                      message += "<p></p>";
	                                      message += "<p>This email is to inform you that you have " +dakCount + " within due date ("+DateTimeFormatUtil.getCurrentWeekMonday()+" - "+DateTimeFormatUtil.getCurrentWeekSunday()+"), awaiting your "+word+" to ensure timely completion.</p>";
	                                     //The timely completion of these tasks is crucial for our project's progress, so please prioritize them and ensure that your responses are provided as soon as possible.
	                                      message += "<p>This is for your information, please take the action.</p>";

	                                      // Generate the HTML table for DakNos and Sources
	                                      message += "<table style=\"align: left; margin-top: 10px; margin-bottom: 10px; margin-left: 15px; max-width: 650px; font-size: 16px; border-collapse: collapse;\">";
	                                      message += "<thead>";
	                                      message += "<tr>";
	                                      message += "<th style=\"text-align: center; width: 500px; border: 1px solid black; padding: 5px; padding-left: 15px\">DAK No</th>";
	                                      message += "<th style=\"text-align: center; width: 200px; border: 1px solid black; padding: 5px; padding-left: 15px\">Source</th>";
	                                      message += "<th style=\"text-align: center; width: 200px; border: 1px solid black; padding: 5px; padding-left: 15px\">Due Date</th>";
	                                      message += "</tr>";
	                                      message += "</thead>";
	                                      message += "<tbody>";

	                                      for (Object[] dakAndSource : emailData.getDakAndSourceAndDueDateList()) {
	                                          message += "<tr>";
	                                          message += "<td style=\"border: 1px solid black; padding: 5px; text-align: left\">" + dakAndSource[0] + "</td>";
	                                          message += "<td style=\"border: 1px solid black; padding: 5px; text-align: center\">" + dakAndSource[1] + "</td>";
	                                          message += "<td style=\"border: 1px solid black; padding: 5px; text-align: center\">" + dakAndSource[2] + "</td>";
	                                          message += "</tr>";
	                                      }

	                                      message += "</tbody>";
	                                      message += "</table>";
	                                      message += "Please <a href=\"" + env.getProperty("Login_link") + "\">Click Here</a> to Go DMS.<br>";
	                                      message += "<p>Important Note: This is an automated message. Kindly avoid responding.</p>";
	                                      message += "<p>Regards,<br>LRDE-DMS Team</p>";

	                                      // Send the email using 'email' address and 'message' content
	                                      System.out.println("dakAndSourceList sizeeeeeeeeeeeeee@@@@@@#$$"+size+"for empiddddd :"+empId+"for email  :"+email);
	                                         
	                                       // Send the email asynchronously within the loop
	                                      // Send the email asynchronously within the loop
	                                      int sendResult1 = sendMessage(email,subject, message);
	                                      int sendResult2 = sendMessage1(DronaEmail, subject, message);

	                                          if (sendResult1 > 0) {
	                                              // Successfully sent
	                                              service.UpdateParticularEmpMailStatus("W", "S", Long.parseLong(empId.toString()), effectivelyFinalMailTrackingId);
	                                              System.out.println("success");
	                                              mailSendSuccessCount.incrementAndGet(); // Increment success count atomically
	                                          } else {
	                                              // Failed to send
	                                              service.UpdateParticularEmpMailStatus("W", "N", Long.parseLong(empId.toString()), effectivelyFinalMailTrackingId);
	                                              System.out.println("failure");
	                                          }
	                                  }
	                              }
	                              service.updateMailSuccessCount(MailTrackingId, mailSendSuccessCount.get(), "W");
	                       }
	                       } else {
	                              service.UpdateNoPendingReply("W");
	                          }
	                      
	                  } catch (Exception e) {
	                      e.printStackTrace();
	                      logger.error(new Date() + " Inside CONTROLLER myWeeklyScheduledMailTask " + e);
	                  }
	              }
	    
	    
	    @Scheduled(cron = "${DmsSmsTime1}")
	    public void myDailySmsSend() {
	      logger.info(new Date() + " Inside CONTROLLER myDailySmsSend ");
	      try {
	          long SmsTrackingId = 0;
	          long SmsTrackingInsightsId = 0;
	          long DailyPendingSmSendInitiation = service.GetSMSInitiatedCount("D");
	          if (DailyPendingSmSendInitiation == 0) {
	        	  SmsTrackingId = service.InsertSmsTrackInitiator("D");
	          }
	            //the list of daily pending reply details
	              List<Object[]> PendingReplyEmpsDetailstoSendSms = service.GetDailyPendingReplyEmpData();
	             if (SmsTrackingId > 0 && PendingReplyEmpsDetailstoSendSms != null && PendingReplyEmpsDetailstoSendSms.size() > 0) {
	              	 SmsTrackingInsightsId = service.InsertDailySmsPendingInsights(SmsTrackingId);
	              	   if(SmsTrackingInsightsId > 0) {
	              		   System.out.println("SmsTrackingInsightsId:"+SmsTrackingInsightsId);
	                  service.updateSmsSuccessCount(SmsTrackingId, SmsTrackingInsightsId, "D");
	            }   
	           // if No EmpId is found to send daily message
	             } else {
	                  service.UpdateNoSmsPendingReply("D");
	              }
	      } catch (Exception e) {
	          e.printStackTrace();
	          logger.error(new Date() + " Inside CONTROLLER myDailyPendingScheduledSmsTask " + e);
	      }
	  }
	    
	    
	    @Scheduled(cron = "${DmsSmsTime2}")
	    public void DirectorDailySmsSend() {
	      logger.info(new Date() + " Inside CONTROLLER DirectorDailySmsSend ");
	      try {
	    	  long SmsTrackingId = 0;
	          long SmsTrackingInsightsId = 0;
	          SmsTrackingId = service.DirectorInsertSmsTrackInitiator("D");
	          List<Object[]> DirectorPendingReplyEmpsDetailstoSendSms = service.GetDirectorDailyPendingReplyEmpData("LRDE");
	          System.out.println("SmsTrackingId:"+SmsTrackingId);
	          System.out.println("DirectorPendingReplyEmpsDetailstoSendSms:"+DirectorPendingReplyEmpsDetailstoSendSms);
	          if (SmsTrackingId > 0 && DirectorPendingReplyEmpsDetailstoSendSms != null && DirectorPendingReplyEmpsDetailstoSendSms.size() > 0) {
	         	 SmsTrackingInsightsId = service.DirectorInsertDailySmsPendingInsights(SmsTrackingId);
	         	if(SmsTrackingInsightsId > 0) {
	            service.updateSmsSuccessCount(SmsTrackingId, SmsTrackingInsightsId, "D");
	         	}
	         	
	         	} else {
	         		service.UpdateNoSmsPendingReply("D");
	         	}
	          } catch (Exception e) {
	          e.printStackTrace();
	          logger.error(new Date() + " Inside CONTROLLER DirectorDailySmsSend " + e);
	       }
	  }
	    
	    @Scheduled(cron = "${PmsSmsTime1}")
	    public void PmsmyDailySmsSend() {
	      logger.info(new Date() + " Inside CONTROLLER myDailySmsSend ");
	      try {
	          long SmsTrackingId = 0;
	          long SmsTrackingInsightsId = 0;
	          long DailyPendingSmSendInitiation = service.GetPmsSMSInitiatedCount("D");
	          if (DailyPendingSmSendInitiation == 0) {
	        	  SmsTrackingId = service.InsertPmsSmsTrackInitiator("D");
	          }
	            //the list of daily pending reply details
	              List<Object[]> PendingAssigneeEmpsDetailstoSendSms = service.GetDailyPendingAssigneeEmpData();
	             if (SmsTrackingId > 0 && PendingAssigneeEmpsDetailstoSendSms != null && PendingAssigneeEmpsDetailstoSendSms.size() > 0) {
	              	 SmsTrackingInsightsId = service.InsertDailyPmsSmsPendingInsights(SmsTrackingId);
	              	   if(SmsTrackingInsightsId > 0) {
	                  service.updatePmsSmsSuccessCount(SmsTrackingId, SmsTrackingInsightsId, "D");
	            }   
	           // if No EmpId is found to send daily message
	             } else {
	                  service.UpdateNoPmsSmsPendingReply("D");
	              }
	          
	      } catch (Exception e) {
	          e.printStackTrace();
	          logger.error(new Date() + " Inside CONTROLLER myDailyPendingScheduledSmsTask " + e);
	      }
	  }
	    
	    
	    @Scheduled(cron = "${PmsSmsTime2}")
	    public void PmsDirectorDailySmsSend() {
	      logger.info(new Date() + " Inside CONTROLLER DirectorDailySmsSend ");
	      try {
	    	  long SmsTrackingId = 0;
	          long SmsTrackingInsightsId = 0;
	       // Create an AtomicInteger for thread-safe success count updates
	          SmsTrackingId = service.DirectorPmsInsertSmsTrackInitiator("D");
	          List<Object[]> DirectorPendingReplyEmpsDetailstoSendSms = service.GetDirectorDailyPendingAssignEmpData("LRDE");
	          if (SmsTrackingId > 0 && DirectorPendingReplyEmpsDetailstoSendSms != null && DirectorPendingReplyEmpsDetailstoSendSms.size() > 0) {
	         	 SmsTrackingInsightsId = service.DirectorInsertDailyPmsSmsPendingInsights(SmsTrackingId);
	         	   if(SmsTrackingInsightsId > 0) {
	               service.updatePmsSmsSuccessCount(SmsTrackingId, SmsTrackingInsightsId, "D");
	              }
	   
				// if No EmpId is found to send daily message
				  } else {
				       service.UpdateNoSmsPendingReply("D");
				   }

	      } catch (Exception e) {
	          e.printStackTrace();
	          logger.error(new Date() + " Inside CONTROLLER DirectorDailySmsSend " + e);
	       }
	  }
	    
	    @Scheduled(cron = "${PmsSmsTime3}")
	    public void myDailyCommitteSmsSend() {
	      logger.info(new Date() + " Inside CONTROLLER myDailyCommitteSmsSend ");
	      try {
	          long CommitteSmsTrackingId = 0;
	          long CommitteSmsTrackingInsightsId = 0;
	       // Create an AtomicInteger for thread-safe success count updates
	          long DailyCommitteSmSendInitiation = service.GetCommitteSMSInitiatedCount("D");
	          if (DailyCommitteSmSendInitiation == 0) {
	        	  CommitteSmsTrackingId = service.InsertCommitteSmsTrackInitiator("D");
	          }
	            //the list of daily pending reply details
	              List<Object[]> CommitteEmpsDetailstoSendSms = service.GetCommitteEmpsDetailstoSendSms();
	             if (CommitteSmsTrackingId > 0 && CommitteEmpsDetailstoSendSms != null && CommitteEmpsDetailstoSendSms.size() > 0) {
	            	 CommitteSmsTrackingInsightsId = service.InsertDailyCommitteSmsInsights(CommitteSmsTrackingId);
	              	   if(CommitteSmsTrackingInsightsId > 0) {
	                  service.updateCommitteSmsSuccessCount(CommitteSmsTrackingId,CommitteSmsTrackingInsightsId, "D");
	            }   
	           // if No EmpId is found to send daily message
	             } else {
	                  service.UpdateCommitteNoSmsPending("D");
	              }
	          
	      } catch (Exception e) {
	          e.printStackTrace();
	          logger.error(new Date() + " Inside CONTROLLER myDailyCommitteSmsSend " + e);
	      }
	  }
	    
	    
	    @Scheduled(cron="${DmsSmsFileTime}")
		public void DMSSmsReportListExcel() throws Exception{
			logger.info(new Date() + " Inside DMSSmsReportListExcel.htm ");
			try {
				String fromDate = LocalDate.now().toString();
				String toDate  = LocalDate.now().toString();
				List<Object[]> SmsReportList=service.SmsReportList(fromDate,toDate);
					String LocalDrive = LocalFilesDrive;
					
					File Localfolder = new File(LocalDrive);
		            if (!Localfolder.exists()) {
		            	Localfolder.mkdirs(); // Creates all necessary parent folders too
		            }
		            
		            String filename = "DMS_SMS_Report.csv";
		            String fileLocation = LocalDrive + filename;
		            
		            File existingFile = new File(fileLocation);
		            if (existingFile.exists()) {
		                existingFile.delete();
		            }

					String InternetDrive=InternetFilesDrive;
					
					File Internetfolder = new File(InternetDrive);
		            if (!Internetfolder.exists()) {
		            	Internetfolder.mkdirs(); // Creates all necessary parent folders too
		            }
		            
		            String filename1 = "DMS_SMS_Report.csv";
		            String fileLocation1 = InternetDrive + filename1;
		            
		            File existingFile1 = new File(fileLocation1);
		            if (existingFile1.exists()) {
		                existingFile1.delete();
		            }
		            
		            try (BufferedWriter writer1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileLocation), "UTF-8"));
		                 BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileLocation1), "UTF-8"))) {

		                   // Write headers to both files
		                   writer1.write("SN,Mobile No,Message\n");
		                   writer2.write("SN,Mobile No,Message\n");

		                   // Write data to both files
		                   long slno = 1;
		                   for (Object[] obj : SmsReportList) {
		                	   StringBuilder line = new StringBuilder();
		                        line.append(slno++).append(",");
		                        String MobileNo=obj[6] != null ? obj[6].toString():"";
		                        line.append(MobileNo).append(",");
		                        String message = "Good Morning " +
		                                "DAK  DP=" + (obj[2] != null ? obj[2].toString() : "") + "  " +
		                                "DU=" + (obj[3] != null ? obj[3].toString() : "") + "  " +
		                                "DT=" + (obj[4] != null ? obj[4].toString() : "") + " " +
		                                "DD=" + (obj[5] != null ? obj[5].toString() : "") + "" +
		                                "-DMS Team.";
		                     // Replace any commas with spaces to avoid CSV issues
		                        message = message.replaceAll(",", " ");

		                        line.append(message);
		                       writer1.write(line.toString() + "\n");
		                       writer2.write(line.toString() + "\n");
		                   }
		               }

		               System.out.println("CSV files saved to: " + fileLocation + " and " + fileLocation);
		               System.out.println("CSV files saved to: " + fileLocation1 + " and " + fileLocation1);

		           } catch (Exception e) {
		               e.printStackTrace();
		           }
		}
	    
	    
	    @Scheduled(cron="${PmsSmsFileTime1}")
		public void PMSSmsReportListExcel() throws Exception{
			logger.info(new Date() + " Inside PMSSmsReportListExcel.htm ");
			try {
				String fromDate = LocalDate.now().toString();
				String toDate  = LocalDate.now().toString();
				List<Object[]> SmsReportList=service.PmsSmsReportList(fromDate,toDate);
				String LocalDrive = LocalFilesDrive;
				
				File Localfolder = new File(LocalDrive);
	            if (!Localfolder.exists()) {
	            	Localfolder.mkdirs(); // Creates all necessary parent folders too
	            }
	            
	            String filename = "PMS_ACTION_SMS_Report.csv";
	            String fileLocation = LocalDrive + filename;
	            
	            File existingFile = new File(fileLocation);
	            if (existingFile.exists()) {
	                existingFile.delete();
	            }

				String InternetDrive=InternetFilesDrive;
				
				File Internetfolder = new File(InternetDrive);
	            if (!Internetfolder.exists()) {
	            	Internetfolder.mkdirs(); // Creates all necessary parent folders too
	            }
	            
	            String filename1 = "PMS_ACTION_SMS_Report.csv";
	            String fileLocation1 = InternetDrive + filename1;
	            
	            File existingFile1 = new File(fileLocation1);
	            if (existingFile1.exists()) {
	                existingFile1.delete();
	            }
	            
		            
	            try (BufferedWriter writer1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileLocation), "UTF-8"));
		             BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileLocation1), "UTF-8"))) {

		                   // Write headers to both files
		                   writer1.write("SN,Mobile No,Message\n");
		                   writer2.write("SN,Mobile No,Message\n");

		                   // Write data to both files
		                   long slno = 1;
		                   for (Object[] obj : SmsReportList) {
		                	   StringBuilder line = new StringBuilder();
		                        line.append(slno++).append(",");
		                        String MobileNo=obj[11] != null ? obj[11].toString():"";
		                        line.append(MobileNo).append(",");
		                        String message = obj[12] != null ? obj[12].toString():"";
		                     // Replace any commas with spaces to avoid CSV issues
		                        message = message.replaceAll(",", " ");

		                        line.append(message);
		                       writer1.write(line.toString() + "\n");
		                       writer2.write(line.toString() + "\n");
		                   }
		               }

		            System.out.println("CSV file saved to: " + fileLocation +LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
		            System.out.println("CSV file saved to: " + fileLocation1 +LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
			
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	    
	    
	    @Scheduled(cron="${PmsSmsFileTime2}")
		public void PMSSmsCommitteReportListExcel() throws Exception{
			logger.info(new Date() + " Inside PMSSmsCommitteReportListExcel.htm ");
			try {
				String fromDate = LocalDate.now().toString();
				String toDate  = LocalDate.now().toString();
				List<Object[]> SmsCommitteReportList=service.SmsCommitteReportList(fromDate,toDate);
					
				String LocalDrive = LocalFilesDrive;
				
				File Localfolder = new File(LocalDrive);
	            if (!Localfolder.exists()) {
	            	Localfolder.mkdirs(); // Creates all necessary parent folders too
	            }
	            
	            String filename = "PMS_COMMITTE_SMS_Report.csv";
	            String fileLocation = LocalDrive + filename;
	            
	            File existingFile = new File(fileLocation);
	            if (existingFile.exists()) {
	                existingFile.delete();
	            }

				String InternetDrive=InternetFilesDrive;
				
				File Internetfolder = new File(InternetDrive);
	            if (!Internetfolder.exists()) {
	            	Internetfolder.mkdirs(); // Creates all necessary parent folders too
	            }
	            
	            String filename1 = "PMS_COMMITTE_SMS_Report.csv";
	            String fileLocation1 = InternetDrive + filename1;
	            
	            File existingFile1 = new File(fileLocation1);
	            if (existingFile1.exists()) {
	                existingFile1.delete();
	            }
	            
	            try (BufferedWriter writer1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileLocation), "UTF-8"));
			             BufferedWriter writer2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileLocation1), "UTF-8"))) {

			                   // Write headers to both files
			                   writer1.write("SN,Mobile No,Message\n");
			                   writer2.write("SN,Mobile No,Message\n");

			                   // Write data to both files
			                   long slno = 1;
			                   for (Object[] obj : SmsCommitteReportList) {
			                	   StringBuilder line = new StringBuilder();
			                        line.append(slno++).append(",");
			                        String MobileNo=obj[2] != null ? obj[2].toString():"";
			                        line.append(MobileNo).append(",");
			                        String message = obj[3] != null ? obj[3].toString():"";
			                     // Replace any commas with spaces to avoid CSV issues
			                        message = message.replaceAll(",", " ");

			                        line.append(message);
			                       writer1.write(line.toString() + "\n");
			                       writer2.write(line.toString() + "\n");
			                   }
			               }
		            
		            
		            System.out.println("CSV file saved to: " + fileLocation +LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
		            System.out.println("CSV file saved to: " + fileLocation1 +LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
			
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	    
	    @Scheduled(cron = "${ProjectHoaTime}")
	    public void ProjectHoaUpdate() {
	      logger.info(new Date() + " Inside CONTROLLER ProjectHoaUpdate ");
	      
	      try {
	    	
	    	  
	    	final String localUri1=uri+"/pfms_serv/tblprojectdata?labcode="+LabCode;
	      	final String localUri2=uri+"/pfms_serv/pfms-finance-changes?projectCode=A&interval=M";
	      	final String localUri3=uri+"/pfms_serv/pfms-finance-changes?projectCode=A&interval=W";
	      	final String localUri4=uri+"/pfms_serv/pfms-finance-changes?projectCode=A&interval=T";
	      	final String localUri5=uri+"/pfms_serv/labdetails";
//	      	final String CCMDataURI=uri+"/pfms_serv/getCCMViewData";
	      	
	      	String MonthlyData=null;
	      	String WeeklyData=null;
	      	String TodayData=null;
	      	String HoaJsonData=null;
	      	String LabData= null;
	      	List<CCMView> CCMViewData=null;
	      	long count= 0L;
	      	long ibasserveron=0L;
	      	try {
	      		HttpHeaders headers = new HttpHeaders();
	  	 		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	  	 		headers.set("labcode", LabCode);
	      		HttpEntity<String> entity = new HttpEntity<String>(headers);
	  			ResponseEntity<String> response1=restTemplate.exchange(localUri1, HttpMethod.POST, entity, String.class);
	  			HoaJsonData=response1.getBody();

	  			ResponseEntity<String> monthlyresponse=restTemplate.exchange(localUri2, HttpMethod.POST, entity, String.class);
	  			ResponseEntity<String> weeklyresponse=restTemplate.exchange(localUri3, HttpMethod.POST, entity, String.class);
	  			ResponseEntity<String> todayresponse=restTemplate.exchange(localUri4, HttpMethod.POST, entity, String.class);
	  			ResponseEntity<String> labdata=restTemplate.exchange(localUri5, HttpMethod.POST, entity, String.class);
	  			
	  			MonthlyData=monthlyresponse.getBody();
	  			WeeklyData=weeklyresponse.getBody();
	  			TodayData=todayresponse.getBody();
	  			LabData=labdata.getBody();
	  			CCMViewData= PFMSServ.getCCMViewData(LabCode);
	  			
	      	}
	      	catch(HttpClientErrorException  | ResourceAccessException e) 
	      	{
	      		logger.error(new Date() +" Inside ProjectHoaUpdate.htm pfms_serv Not Responding.htm "+ e);
	      		e.printStackTrace();
	      		ibasserveron = 1;
	  		}
	      	catch(Exception e)
	  		{
	      		logger.error(new Date() +" Inside ProjectHoaUpdate.htm "+ e);
	  			e.printStackTrace();
	  		}

	  		ObjectMapper mao = new ObjectMapper().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
	  		List<ProjectHoa> projectDetails1=null;
	  		List<FinanceChanges> FinanceDetailsMonthly=null;
	  		List<FinanceChanges> FinanceDetailsWeekly=null;
	  		List<FinanceChanges> FinanceDetailsToday=null;
	  		List<IbasLabMaster> LabDetails=null;
	  		
	  		if(HoaJsonData!=null) {
	  			try {

	  				projectDetails1 = mao.readValue(HoaJsonData, mao.getTypeFactory().constructCollectionType(List.class, ProjectHoa.class));
	  				LabDetails = mao.readValue(LabData, mao.getTypeFactory().constructCollectionType(List.class, IbasLabMaster.class));
	  				count = service.ProjectHoaUpdate(projectDetails1,"admin",LabDetails);
	  				
	  			} catch (JsonProcessingException e) {
	  				
	  				e.printStackTrace();
	  			}
	  		}
	  		
	  		if(MonthlyData!=null) {
				try {

					FinanceDetailsMonthly = mao.readValue(MonthlyData, mao.getTypeFactory().constructCollectionType(List.class, FinanceChanges.class));
					FinanceDetailsWeekly = mao.readValue(WeeklyData, mao.getTypeFactory().constructCollectionType(List.class, FinanceChanges.class));
					FinanceDetailsToday = mao.readValue(TodayData, mao.getTypeFactory().constructCollectionType(List.class, FinanceChanges.class));
					

					service.ProjectFinanceChangesUpdate(FinanceDetailsMonthly,FinanceDetailsWeekly,FinanceDetailsToday,"admin",LabCode);
					
				} catch (JsonProcessingException e) {

					logger.error(new Date() +" Inside ProjectHoaUpdate.htm "+ e);
					e.printStackTrace();
				}
			}
	  		
	  		if(CCMViewData!=null && CCMViewData.size()>0)
			{
	  			Object getclusterId=service.getClusterId(LabCode);
	  			String ClusterId=getclusterId.toString();
	  			service.CCMViewDataUpdate(CCMViewData, LabCode, ClusterId, "admin", "");
			}
	      } catch (Exception e) {
	          e.printStackTrace();
	          logger.error(new Date() + " Inside CONTROLLER ProjectHoaUpdate " + e);
	      }
	  }   
	    
	    
	    @Scheduled(cron = "${ProjectHealthTime}")
	    public void ProjectHealthUpdate() {
	      logger.info(new Date() + " Inside CONTROLLER ProjectHoaUpdate ");
	      
	      try {
	    	  service.ProjectHealthUpdate(LabCode,"admin");
	      } catch (Exception e) {
	          e.printStackTrace();
	          logger.error(new Date() + " Inside CONTROLLER ProjectHoaUpdate " + e);
	      }
	  }   
}



