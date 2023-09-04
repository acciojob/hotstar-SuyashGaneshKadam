package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        int totalAmountPaid = 0;
        SubscriptionType subscriptionType = subscriptionEntryDto.getSubscriptionType();
        if(subscriptionType == SubscriptionType.BASIC){
            totalAmountPaid = 500 + 200 * subscriptionEntryDto.getNoOfScreensRequired();
        }
        else if(subscriptionType == SubscriptionType.PRO){
            totalAmountPaid = 800 + 250 * subscriptionEntryDto.getNoOfScreensRequired();
        }
        else{
            totalAmountPaid = 1000 + 350 * subscriptionEntryDto.getNoOfScreensRequired();
        }
        Subscription subscription = new Subscription(subscriptionType,subscriptionEntryDto.getNoOfScreensRequired(),new Date(),totalAmountPaid);
        return totalAmountPaid;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user = userRepository.findById(userId).get();
        Subscription subscription = user.getSubscription();
        int amountToBePaid = 0;
        if(user.getSubscription().getSubscriptionType() == SubscriptionType.ELITE){
            throw  new Exception("Already the best Subscription");
        }
        else if(user.getSubscription().getSubscriptionType() == SubscriptionType.PRO){
            amountToBePaid = (1000 + 350 * subscription.getNoOfScreensSubscribed()) - subscription.getTotalAmountPaid();
            subscription.setSubscriptionType(SubscriptionType.ELITE);
        }
        else {
            amountToBePaid = (800 + 250 * subscription.getNoOfScreensSubscribed()) - subscription.getTotalAmountPaid();
            subscription.setSubscriptionType(SubscriptionType.PRO);
        }
        subscription.setTotalAmountPaid(subscription.getTotalAmountPaid() + amountToBePaid);

        user.setSubscription(subscription);
        subscriptionRepository.save(subscription);
        return amountToBePaid;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptionList = subscriptionRepository.findAll();
        if(subscriptionList == null || subscriptionList.size() == 0){
            return 0;
        }
        Integer totalRevenue = 0;
        for(Subscription subscription : subscriptionList){
            totalRevenue += subscription.getTotalAmountPaid();
        }
        return totalRevenue;
    }

}
