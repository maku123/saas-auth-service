package com.maku123.saas_auth_service.controller;

import com.maku123.saas_auth_service.service.DataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    private final DataService dataService;

    public ApiController(DataService dataService) {
        this.dataService = dataService;
    }

    /**
     * API endpoint to get team info.
     * @return Raw JSON string of team info
     */
    @GetMapping("/api/dropbox/team-info")
    public String getTeamInfo() {
        return dataService.getTeamInfo();
    }
    
    /**
     * API endpoint to get the user list.
     * @return Raw JSON string of the user list
     */
    @GetMapping("/api/dropbox/users")
    public String getUsersList() {
        return dataService.getUsersList();
    }
    
    /**
     * API endpoint to get sign-in events.
     * @return Raw JSON string of login events
     */
    @GetMapping("/api/dropbox/events")
    public String getSignInEvents() {
        return dataService.getSignInEvents();
    }
}
