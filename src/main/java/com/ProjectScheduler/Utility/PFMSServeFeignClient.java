package com.ProjectScheduler.Utility;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.ProjectScheduler.model.CCMView;

@FeignClient(name = "PFMSServeFeignClient", url = "${server_uri}"+"/pfms_serv")
public interface PFMSServeFeignClient {
	
	
	@PostMapping("/getCCMViewData")
    public List<CCMView> getCCMViewData(@RequestHeader(name = "labcode")String LabCode);
    
	
}