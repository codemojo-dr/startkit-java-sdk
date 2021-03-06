package io.codemojo.sdk.services;

import io.codemojo.sdk.exceptions.InvalidArgumentsException;
import io.codemojo.sdk.exceptions.ResourceNotFoundException;
import io.codemojo.sdk.exceptions.SDKInitializationException;
import io.codemojo.sdk.exceptions.SetupIncompleteException;
import io.codemojo.sdk.facades.LoyaltyEvent;
import io.codemojo.sdk.models.GenericResponse;
import io.codemojo.sdk.models.Loyalty;
import io.codemojo.sdk.models.LoyaltySummary;
import io.codemojo.sdk.network.ILoyalty;
import io.codemojo.sdk.responses.ResponseLoyalty;
import io.codemojo.sdk.responses.ResponseLoyaltyMaximumRedemption;
import io.codemojo.sdk.responses.ResponseLoyaltySummary;
import io.codemojo.sdk.utils.APICodes;
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
     * @param transaction
     * @param transaction_id
     * @param meta
     * @param tag
     * @param platform
     * @param service
     */
    public Loyalty addLoyaltyPoints(float transaction, String transaction_id, String meta, String tag, String platform, String service) throws Exception {
        if (loyaltyService == null){
            raiseException(new SDKInitializationException());
            return null;
        }
        final Call<ResponseLoyalty> response = loyaltyService.addLoyaltyPoints(getCustomerId(), transaction, transaction_id, meta, tag, platform, service);
        ResponseLoyalty body = null;
        try {
            body = response.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(body != null){
            switch (body.getCode()) {
                case APICodes.INVALID_MISSING_FIELDS:
                    raiseException(new InvalidArgumentsException(body.getMessage()));
                    break;
                case APICodes.SERVICE_NOT_SETUP:
                    raiseException(new SetupIncompleteException(body.getMessage()));
                    break;
                case APICodes.RESPONSE_FAILURE:
                    raiseException(new Exception(body.getMessage()));
                    break;
                case APICodes.RESPONSE_SUCCESS:
                    if(notification != null && body.getResult().isTierUpgrade()) {
                        notification.newTierUpgrade(body.getResult().getTier());
                    }
                    return body.getResult();
            }
        }
        return null;
    }

    /**
     * @param transaction
     * @param transaction_id
     * @param meta
     * @param tag
     */
    public Loyalty addLoyaltyPoints(float transaction, String transaction_id, String meta, String tag) throws Exception {
        return addLoyaltyPoints(transaction, transaction_id, meta, tag, null, null);
    }

    /**
     * @param transaction
     * @param transaction_id
     */
    public Loyalty addLoyaltyPoints(float transaction, String transaction_id) throws Exception {
        return addLoyaltyPoints(transaction, transaction_id, null, null, null, null);
    }

    /**
     * @param transaction
     */
    public float calculateLoyaltyPoints(float transaction) throws Exception {
        return calculateLoyaltyPoints(transaction, null, null);
    }

    /**
     * @param transaction
     * @param platform
     * @param service
     */
    public float calculateLoyaltyPoints(float transaction, String platform, String service) throws Exception {
        if (loyaltyService == null){
            raiseException(new SDKInitializationException());
            return 0;
        }
        final Call<ResponseLoyalty> response = loyaltyService.calculateLoyaltyPoints(getCustomerId(), transaction, platform, service);
        try {
            final ResponseLoyalty body = response.execute().body();
            if(body != null){
                switch (body.getCode()) {
                    case APICodes.INVALID_MISSING_FIELDS:
                        raiseException(new InvalidArgumentsException(body.getMessage()));
                        break;
                    case APICodes.SERVICE_NOT_SETUP:
                        raiseException(new SetupIncompleteException(body.getMessage()));
                        break;
                    case APICodes.RESPONSE_FAILURE:
                        raiseException(new Exception(body.getMessage()));
                        break;
                    case APICodes.RESPONSE_SUCCESS:
                        return body.getResult().getAward();
                }
            }
        } catch (IOException ignored) {
            raiseException(ignored);
        }
        return 0;
    }

    /**
     * @param transaction
     */
    public float maximumRedemption(float transaction) throws Exception {
        return maximumRedemption(transaction, null, null);
    }


    /**
     * @param transaction
     * @param platform
     * @param service
     */
    public float maximumRedemption(float transaction, String platform, String service) throws Exception {
        if (loyaltyService == null){
            raiseException(new SDKInitializationException());
            return 0;
        }
        final Call<ResponseLoyaltyMaximumRedemption> response = loyaltyService.maximumRedemption(getCustomerId(), transaction, platform, service);
        try {
            final ResponseLoyaltyMaximumRedemption body = response.execute().body();
            if(body != null){
                switch (body.getCode()) {
                    case APICodes.INVALID_MISSING_FIELDS:
                        raiseException(new InvalidArgumentsException(body.getMessage()));
                        break;
                    case APICodes.SERVICE_NOT_SETUP:
                        raiseException(new SetupIncompleteException(body.getMessage()));
                        break;
                    case APICodes.RESPONSE_FAILURE:
                        raiseException(new Exception(body.getMessage()));
                        break;
                    case APICodes.RESPONSE_SUCCESS:
                        return body.getResult();
                }
            }
        } catch (IOException ignored) {
            raiseException(ignored);
        }
        return 0;
    }

    /**
     * @return
     */
    public LoyaltySummary getSummary() throws Exception {
        if (loyaltyService == null){
            raiseException(new SDKInitializationException());
            return null;
        }
        final Call<ResponseLoyaltySummary> response = loyaltyService.summary(getCustomerId());
        try {
            final ResponseLoyaltySummary body = response.execute().body();
            if(body != null){
                switch (body.getCode()) {
                    case APICodes.INVALID_MISSING_FIELDS:
                        raiseException(new InvalidArgumentsException(body.getMessage()));
                        break;
                    case APICodes.SERVICE_NOT_SETUP:
                        raiseException(new SetupIncompleteException(body.getMessage()));
                        break;
                    case APICodes.RESPONSE_FAILURE:
                        raiseException(new Exception(body.getMessage()));
                        break;
                    case APICodes.RESPONSE_SUCCESS:
                        return body.getSummary();
                }
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    /**
     * @param transaction_id
     * @param redemption_value
     * @param transaction_value
     * @param meta
     * @param tag
     * @return
     */
    public boolean redeemPoints(String transaction_id, float redemption_value,
                                        float transaction_value, String meta, String tag) throws Exception {
        return redeemPoints(transaction_id, redemption_value, transaction_value, null, null, meta, tag);
    }

    /**
     * @param transaction_id
     * @param redemption_value
     * @param transaction_value
     * @param platform
     * @param service_id
     * @param meta
     * @param tag
     * @return
     */
    public boolean redeemPoints(String transaction_id, float redemption_value,
                                        float transaction_value, String platform, String service_id, String meta, String tag) throws Exception {
        if (loyaltyService == null){
            raiseException(new SDKInitializationException());
            return false;
        }
        final Call<GenericResponse> response = loyaltyService.redeemLoyaltyPoints(getCustomerId(), transaction_value, redemption_value,
                transaction_id, platform, service_id, meta, tag);
        try {
            final GenericResponse body = response.execute().body();
            if(body != null){
                switch (body.getCode()) {
                    case APICodes.WALLET_BALANCE_EXHAUSTED:
                        raiseException(new Exception("Insufficient balance"));
                        break;
                    case APICodes.INVALID_MISSING_FIELDS:
                        raiseException(new InvalidArgumentsException(body.getMessage()));
                        break;
                    case APICodes.SERVICE_NOT_SETUP:
                        raiseException(new SetupIncompleteException(body.getMessage()));
                        break;
                    case APICodes.OVERFLOW:
                    case APICodes.RESPONSE_FAILURE:
                        raiseException(new Exception(body.getMessage()));
                        break;
                    case APICodes.RESPONSE_SUCCESS:
                        return true;
                }
            }
        } catch (IOException ignored) {
        }
        return false;
    }

    /**
     * @param transaction_id
     * @return
     */
    public boolean cancelTransaction(String transaction_id) throws Exception {
        if (loyaltyService == null){
            raiseException(new SDKInitializationException());
            return false;
        }
        final Call<GenericResponse> response = loyaltyService.cancel(transaction_id);
        try {
            final GenericResponse body = response.execute().body();
            if(body != null){
                switch (body.getCode()) {
                    case APICodes.RESOURCE_NOT_FOUND:
                        raiseException(new ResourceNotFoundException(body.getMessage()));
                        break;
                    case APICodes.SERVICE_NOT_SETUP:
                        raiseException(new SetupIncompleteException(body.getMessage()));
                        break;
                    case APICodes.RESPONSE_FAILURE:
                        raiseException(new Exception(body.getMessage()));
                        break;
                    case APICodes.RESPONSE_SUCCESS:
                        return true;
                }
            }
        } catch (IOException ignored) {
        }
        return false;
    }

    /**
     * @param transaction_id
     * @return
     */
    public boolean refund(String transaction_id) throws Exception {
        if (loyaltyService == null){
            raiseException(new SDKInitializationException());
            return false;
        }
        final Call<GenericResponse> response = loyaltyService.refund(transaction_id);
        try {
            final GenericResponse body = response.execute().body();
            if(body != null){
                switch (body.getCode()) {
                    case APICodes.RESOURCE_NOT_FOUND:
                        raiseException(new ResourceNotFoundException(body.getMessage()));
                        break;
                    case APICodes.SERVICE_NOT_SETUP:
                        raiseException(new SetupIncompleteException(body.getMessage()));
                        break;
                    case APICodes.RESPONSE_FAILURE:
                        raiseException(new Exception(body.getMessage()));
                        break;
                    case APICodes.RESPONSE_SUCCESS:
                        return true;
                }
            }
        } catch (Exception ignored) {
        }
        return false;
    }
}
