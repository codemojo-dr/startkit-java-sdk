package io.codemojo.sdk;

import io.codemojo.sdk.exceptions.AuthenticationException;
import io.codemojo.sdk.facades.CodemojoException;
import io.codemojo.sdk.facades.GamificationEarnedEvent;
import io.codemojo.sdk.facades.LoyaltyEvent;
import io.codemojo.sdk.services.*;

/**
 * Created by shoaib on 24/06/16.
 */
public class Codemojo {

    private static AuthenticationService authenticationService;
    private LoyaltyService loyaltyService;
    private WalletService walletService;
    private GamificationService gamificationService;
    private ReferralService referralService;

    private GamificationEarnedEvent gamificationEarnedEvent;
    private LoyaltyEvent loyaltyEvent;

    private CodemojoException exception;

    /**
     * @param client_token Secret Token obtained from Codemojo
     * @param logged_in_user_id Encoded user id
     */
    public Codemojo(String client_token, String logged_in_user_id) {
        this(client_token, logged_in_user_id, false);
    }

    /**
     * @param client_token
     * @param hashed_user_id
     * @param testing
     */
    public Codemojo(String client_token, String hashed_user_id, boolean testing) {
        try {
            authenticationService = new AuthenticationService(client_token, hashed_user_id, testing ? 0 : 1);
        } catch (AuthenticationException e) {
        }
    }

    public static AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    /**
     * @param loyaltyEvent
     */
    public void setLoyaltyEventListener(LoyaltyEvent loyaltyEvent) {
        this.loyaltyEvent = loyaltyEvent;
    }

    /**
     * @param gamificationEarnedEvent
     */
    public void setGamificationEarnedEventListener(GamificationEarnedEvent gamificationEarnedEvent) {
        this.gamificationEarnedEvent = gamificationEarnedEvent;
    }

    /**
     * @return
     */
    public LoyaltyService getLoyaltyService() {
        if(loyaltyService == null){
            loyaltyService = new LoyaltyService(getAuthenticationService(), loyaltyEvent);
            loyaltyService.setErrorHandler(this.exception);
        }
        return loyaltyService;
    }

    /**
     * @return
     */
    public WalletService getWalletService() {
        if(walletService == null){
            walletService = new WalletService(getAuthenticationService());
            walletService.setErrorHandler(this.exception);
        }
        return walletService;
    }

    /**
     * @return
     */
    public GamificationService getGamificationService() {
        if(gamificationService == null){
            gamificationService = new GamificationService(getAuthenticationService(), gamificationEarnedEvent);
            gamificationService.setErrorHandler(this.exception);
        }
        return gamificationService;
    }

    /**
     * @return
     */
    public ReferralService getReferralService() {
        if(referralService == null) {
            referralService = new ReferralService(authenticationService);
            referralService.setErrorHandler(this.exception);
        }
        return referralService;
    }

    /**
     * @param exception
     */
    public void setExceptionHandler(CodemojoException exception) {
        this.exception = exception;
    }
}
