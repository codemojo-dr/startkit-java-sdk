package io.codemojo.sdk.services;

import io.codemojo.sdk.exceptions.InvalidArgumentsException;
import io.codemojo.sdk.exceptions.SDKInitializationException;
import io.codemojo.sdk.exceptions.SetupIncompleteException;
import io.codemojo.sdk.facades.LoyaltyEvent;
import io.codemojo.sdk.models.Loyalty;
import io.codemojo.sdk.models.LoyaltySummary;
import io.codemojo.sdk.network.ILoyalty;
import io.codemojo.sdk.responses.ResponseLoyalty;
import io.codemojo.sdk.responses.ResponseLoyaltyMaximumRedemption;
import io.codemojo.sdk.responses.ResponseLoyaltySummary;
import retrofit2.Call;

import java.io.IOException;

/**
 * Created by shoaib on 16/06/16.
 */
public class LoyaltyService extends BaseService {

    private final ILoyalty loyaltyService;
    private LoyaltyEvent notification;

    /**
     * @param authenticationService
     * @param notification
     */
    public LoyaltyService(AuthenticationService authenticationService, LoyaltyEvent notification) {
        super(authenticationService, ILoyalty.class);
        this.notification = notification;
        loyaltyService = (ILoyalty) getService();
    }

    /**
     * @param customer_id
     * @param transaction
     * @param transaction_id
     * @param meta
     * @param tag
     * @param platform
     * @param service
     */
    public Loyalty addLoyaltyPoints(String customer_id, float transaction, String transaction_id, String meta, String tag, String platform, String service) {
        if (loyaltyService == null){
            raiseException(new SDKInitializationException());
            return null;
        }
        final Call<ResponseLoyalty> response = loyaltyService.addLoyaltyPoints(customer_id, transaction, transaction_id, meta, tag, platform, service);
        ResponseLoyalty body = null;
        try {
            body = response.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(body != null){
            switch (body.getCode()) {
                case -403:
                    raiseException(new InvalidArgumentsException(body.getMessage()));
                    break;
                case 400:
                    raiseException(new SetupIncompleteException(body.getMessage()));
                    break;
                case 200:
                    if(notification != null && body.getResult().isTierUpgrade()) {
                        notification.newTierUpgrade(body.getResult().getTier());
                    }
                    return body.getResult();
            }
        }
        return null;
    }

    /**
     * @param customer_id
     * @param transaction
     * @param transaction_id
     * @param meta
     * @param tag
     */
    public void addLoyaltyPoints(String customer_id, float transaction, String transaction_id, String meta, String tag) {
        addLoyaltyPoints(customer_id, transaction, transaction_id, meta, tag, null, null);
    }

    /**
     * @param customer_id
     * @param transaction
     * @param transaction_id
     */
    public void addLoyaltyPoints(String customer_id, float transaction, String transaction_id) {
        addLoyaltyPoints(customer_id, transaction, transaction_id, null, null, null, null);
    }

    /**
     * @param customer_id
     * @param transaction
     */
    public void calculateLoyaltyPoints(String customer_id, float transaction) {
        calculateLoyaltyPoints(customer_id, transaction, null, null);
    }

    /**
     * @param customer_id
     * @param transaction
     * @param platform
     * @param service
     */
    public float calculateLoyaltyPoints(String customer_id, float transaction, String platform, String service) {
        if (loyaltyService == null){
            raiseException(new SDKInitializationException());
            return 0;
        }
        final Call<ResponseLoyalty> response = loyaltyService.calculateLoyaltyPoints(customer_id, transaction, platform, service);
        try {
            final ResponseLoyalty body = response.execute().body();
            if(body != null){
                switch (body.getCode()) {
                    case -403:
                        raiseException(new InvalidArgumentsException(body.getMessage()));
                        break;
                    case 400:
                        raiseException(new SetupIncompleteException(body.getMessage()));
                        break;
                    case 200:
                        return body.getResult().getAward();
                }
            }
        } catch (Exception ignored) {
            raiseException(ignored);
        }
        return 0;
    }

    /**
     * @param customer_id
     * @param transaction
     */
    public void maximumRedemption(String customer_id, float transaction) {
        maximumRedemption(customer_id, transaction);
    }


    /**
     * @param customer_id
     * @param transaction
     * @param platform
     * @param service
     */
    public float maximumRedemption(String customer_id, float transaction, String platform, String service) {
        if (loyaltyService == null){
            raiseException(new SDKInitializationException());
            return 0;
        }
        final Call<ResponseLoyaltyMaximumRedemption> response = loyaltyService.maximumRedemption(customer_id, transaction, platform, service);
        try {
            final ResponseLoyaltyMaximumRedemption body = response.execute().body();
            if(body != null){
                switch (body.getCode()) {
                    case -403:
                        raiseException(new InvalidArgumentsException(body.getMessage()));
                        break;
                    case 400:
                        raiseException(new SetupIncompleteException(body.getMessage()));
                        break;
                    case 200:
                        return body.getResult();
                }
            }
        } catch (Exception ignored) {
            raiseException(ignored);
        }
        return 0;
    }

    public LoyaltySummary getSummary() {
        if (loyaltyService == null){
            raiseException(new SDKInitializationException());
            return null;
        }
        final Call<ResponseLoyaltySummary> response = loyaltyService.summary(getCustomerId());
        try {
            final ResponseLoyaltySummary body = response.execute().body();
            if(body != null){
                switch (body.getCode()) {
                    case -403:
                        raiseException(new InvalidArgumentsException(body.getMessage()));
                        break;
                    case 400:
                        raiseException(new SetupIncompleteException(body.getMessage()));
                        break;
                    case 200:
                        return body.getSummary();
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
