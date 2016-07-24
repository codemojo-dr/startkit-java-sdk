package io.codemojo.sdk.services;

import io.codemojo.sdk.exceptions.InvalidArgumentsException;
import io.codemojo.sdk.exceptions.ResourceNotFoundException;
import io.codemojo.sdk.exceptions.SDKInitializationException;
import io.codemojo.sdk.exceptions.SetupIncompleteException;
import io.codemojo.sdk.models.GenericResponse;
import io.codemojo.sdk.models.ReferralCode;
import io.codemojo.sdk.network.IReferral;
import io.codemojo.sdk.responses.ResponseReferralCode;
import io.codemojo.sdk.utils.APICodes;
import retrofit2.Call;

import java.io.IOException;

/**
 * Created by shoaib on 16/06/16.
 */
public class ReferralService extends BaseService {

    private final IReferral referralService;

    /**
     * @param authenticationService
     */
    public ReferralService(AuthenticationService authenticationService) {
        super(authenticationService, IReferral.class);
        referralService = (IReferral) getService();
    }

    public ReferralCode getReferralCode() throws Exception {
        if (referralService == null){
            raiseException(new SDKInitializationException());
            return null;
        }
        final Call<ResponseReferralCode> response = referralService.getReferralCode(getCustomerId());
        try {
            final ResponseReferralCode code = response.execute().body();
            if(code != null){
                switch (code.getCode()){
                    case APICodes.RESPONSE_SUCCESS:
                        return code.getReferralCode();
                    case APICodes.INVALID_MISSING_FIELDS:
                        raiseException(new InvalidArgumentsException(code.getMessage()));
                        break;
                    case APICodes.SERVICE_NOT_SETUP:
                        raiseException(new SetupIncompleteException(code.getMessage()));
                        break;
                    case APICodes.RESOURCE_NOT_FOUND:
                        raiseException(new ResourceNotFoundException(code.getMessage()));
                        break;
                    case APICodes.RESPONSE_FAILURE:
                        raiseException(new Exception(code.getMessage()));
                        break;
                }
            }
        } catch (Exception ignored) {
            raiseException(ignored);
        }

        return null;
    }

    /**
     * @param referral_code
     */
    public boolean applyReferralCode(String referral_code) throws Exception {
        if (referralService == null){
            raiseException(new SDKInitializationException());
            return false;
        }
        final Call<GenericResponse> response = referralService.applyReferralCode(getCustomerId(), referral_code);

        try {
            final GenericResponse body = response.execute().body();
            if(body != null){
                switch (body.getCode()){
                    case APICodes.INVALID_MISSING_FIELDS:
                        raiseException(new InvalidArgumentsException(body.getMessage()));
                        break;
                    case APICodes.SERVICE_NOT_SETUP:
                        raiseException(new SetupIncompleteException(body.getMessage()));
                        break;
                    case APICodes.RESOURCE_NOT_FOUND:
                        raiseException(new ResourceNotFoundException(body.getMessage()));
                        break;
                    case APICodes.RESPONSE_SUCCESS:
                        return true;
                    case APICodes.RESPONSE_FAILURE:
                        raiseException(new Exception(body.getMessage()));
                        break;
                }
            }
        } catch (IOException ignored) {
            raiseException(ignored);
        }

        return false;
    }


    public boolean markActionAsComplete() throws Exception {
        if (referralService == null){
            raiseException(new SDKInitializationException());
            return false;
        }
        final Call<GenericResponse> response = referralService.markActionComplete(getCustomerId());
        try {
            GenericResponse body = response.execute().body();
            if(body != null){
                switch (body.getCode()){
                    case APICodes.INVALID_MISSING_FIELDS:
                        raiseException(new InvalidArgumentsException(body.getMessage()));
                        break;
                    case APICodes.SERVICE_NOT_SETUP:
                        raiseException(new SetupIncompleteException(body.getMessage()));
                        break;
                    case APICodes.RESOURCE_NOT_FOUND:
                        raiseException(new ResourceNotFoundException(body.getMessage()));
                        break;
                    case APICodes.RESPONSE_SUCCESS:
                        return true;
                    case APICodes.RESPONSE_FAILURE:
                        raiseException(new Exception(body.getMessage()));
                        break;
                }
            }
        } catch (IOException ignored) {
            raiseException(ignored);
        }

        return false;
    }

}
