package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        return userRepository.save(user).getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository
        List<WebSeries> webSeriesList = webSeriesRepository.findAll();
        User user = userRepository.findById(userId).get();
        SubscriptionType userSubscriptionType = user.getSubscription().getSubscriptionType();
        if(userSubscriptionType == SubscriptionType.ELITE){
            return webSeriesList.size();
        }
        Integer proCount = 0;
        Integer basicCount = 0;
        for(WebSeries webSeries : webSeriesList){
            if(user.getAge() >= webSeries.getAgeLimit()){
                if(webSeries.getSubscriptionType() == SubscriptionType.PRO){
                    proCount++;
                }
                if(webSeries.getSubscriptionType() == SubscriptionType.BASIC){
                    basicCount++;
                }
            }
        }
        if(user.getSubscription().getSubscriptionType() == SubscriptionType.PRO){
            return proCount + basicCount;
        }
        return basicCount;
    }


}
