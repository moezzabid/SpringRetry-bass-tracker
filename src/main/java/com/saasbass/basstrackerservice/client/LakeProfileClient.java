package com.saasbass.basstrackerservice.client;

import com.saasbass.basstrackerservice.BassTrackerServiceApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

public class LakeProfileClient {
    private RestTemplate restTemplate;
    private String lakeProfileServiceBaseUrl;
    private RetryTemplate retryTemplate ;
    private Logger log = LoggerFactory.getLogger(BassTrackerServiceApplication.class);
    private long lastMillis=System.currentTimeMillis();
    private Boolean isFirstRequest=true;

    public LakeProfileClient(HttpComponentsClientHttpRequestFactory clientFactory, RetryTemplate retryTemplate, String lakeProfileServiceBaseUrl) {
        this.restTemplate = new RestTemplate(clientFactory);
        this.retryTemplate=retryTemplate;
        this.lakeProfileServiceBaseUrl = lakeProfileServiceBaseUrl;
    }

    public LakeProfile getLakeProfile(Long id) {
        String url = lakeProfileServiceBaseUrl + "/lake-profile/" + id;
        return retryTemplate.execute(context ->{
            long currentTime=System.currentTimeMillis();
            log.info("---Get lake profile attempts ---"+(!isFirstRequest ?"bacoff duration:"+(currentTime-lastMillis)+" milliseconde":""));
            lastMillis=currentTime;
            isFirstRequest=false;
            return  restTemplate.getForObject(url,LakeProfile.class);
        });
    }

    public void createLakeProfile(LakeProfile lakeProfile) {
        String url = lakeProfileServiceBaseUrl + "/lake-profile";
         retryTemplate.execute(context ->{
            log.info("---- Create lake profile attempts----");
            return  restTemplate.postForObject(url,lakeProfile,LakeProfile.class);
        });
    }
}
