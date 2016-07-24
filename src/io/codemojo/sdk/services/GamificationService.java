package io.codemojo.sdk.services;

import io.codemojo.sdk.exceptions.InvalidArgumentsException;
import io.codemojo.sdk.exceptions.ResourceNotFoundException;
import io.codemojo.sdk.exceptions.SDKInitializationException;
import io.codemojo.sdk.exceptions.SetupIncompleteException;
import io.codemojo.sdk.facades.GamificationEarnedEvent;
import io.codemojo.sdk.facades.IPagination;
import io.codemojo.sdk.models.*;
import io.codemojo.sdk.network.IGamification;
import io.codemojo.sdk.responses.ResponseGamification;
import io.codemojo.sdk.responses.ResponseGamificationAchievement;
import io.codemojo.sdk.responses.ResponseGamificationSummary;
import io.codemojo.sdk.responses.ResponseWalletTransaction;
import io.codemojo.sdk.utils.APICodes;
import retrofit2.Call;

import java.io.IOException;
import java.util.Map;

/**
 * Created by shoaib on 16/06/16.
 */
public class GamificationService extends BaseService implements IPagination<WalletTransaction> {

    private final IGamification gamificationService;
    private GamificationEarnedEvent notification;
    private int transactionPage = 0;
    private int count;

    /**
     * @param authenticationService
     * @param notification
     */
    public GamificationService(AuthenticationService authenticationService, GamificationEarnedEvent notification) {
        super(authenticationService, IGamification.class);
        this.notification = notification;
        gamificationService = (IGamification) getService();
    }

    /**
     * @param action_id
     */
    public GamificationStatus captureAction(String action_id) throws Exception {
        if (gamificationService == null){
            raiseException(new SDKInitializationException());
            return null;
        }
        final Call<ResponseGamification> response = gamificationService.addAction(getCustomerId(), action_id);
        try {
            final ResponseGamification body = response.execute().body();
            if(body != null){
                switch (body.getCode()) {
                    case APICodes.INVALID_MISSING_FIELDS:
                        raiseException(new InvalidArgumentsException(body.getMessage()));
                        break;
                    case APICodes.SERVICE_NOT_SETUP:
                        raiseException(new SetupIncompleteException(body.getMessage()));
                        break;
                    case APICodes.RESOURCE_NOT_FOUND:
                        raiseException(new ResourceNotFoundException(body.getMessage()));
                        break;
                    case APICodes.RESPONSE_FAILURE:
                        raiseException(new Exception(body.getMessage()));
                        break;
                    case APICodes.RESPONSE_SUCCESS:
                        if(notification != null && body.getGamificationStatus().isBadgeUpgrade()) {
                            notification.newBadgeUnlocked(body.getGamificationStatus().getCurrentPoints(), body.getGamificationStatus().getBadge());
                        }
                        return body.getGamificationStatus();
                }
            }
        } catch (Exception ignored) {
            raiseException(ignored);
        }
        return null;
    }

    /**
     * @param action_id
     */
    public void captureAchievementsAction(String action_id) throws Exception {
        captureAchievementsAction(action_id, null);
    }

    /**
     * @param action_id
     * @param category_id
     */
    public Map<String, GamificationAchievement> captureAchievementsAction(String action_id, String category_id) throws Exception {
        if (gamificationService == null){
            raiseException(new SDKInitializationException());
            return null;
        }
        final Call<ResponseGamificationAchievement> response = gamificationService.addAchievement(getCustomerId(), action_id, category_id);
        try {
            final ResponseGamificationAchievement body = response.execute().body();
            if(body != null){
                switch (body.getCode()) {
                    case APICodes.INVALID_MISSING_FIELDS:
                        raiseException(new InvalidArgumentsException(body.getMessage()));
                        break;
                    case APICodes.SERVICE_NOT_SETUP:
                        raiseException(new SetupIncompleteException(body.getMessage()));
                        break;
                    case APICodes.RESOURCE_NOT_FOUND:
                        raiseException(new ResourceNotFoundException(body.getMessage()));
                        break;
                    case APICodes.RESPONSE_FAILURE:
                        raiseException(new Exception(body.getMessage()));
                        break;
                    case APICodes.RESPONSE_SUCCESS:
                        if(notification != null) {
                            for (String achievement : body.getAchievements().keySet()) {
                                if (body.getAchievements().get(achievement).isNewBagdeEarned()) {
                                    notification.newAchievementUnlocked(body.getAchievements().get(achievement).getTotal(), achievement, body.getAchievements().get(achievement));
                                }
                            }
                        }
                        notification.updatedAchievemenstAvailable(body.getAchievements());
                        return body.getAchievements();
                }
            }
        } catch (IOException e) {
            raiseException(e);
        }
        return null;
    }

    /**
     * @return
     */
    public GamificationSummary getUserSummary() throws Exception {
        if (gamificationService == null){
            raiseException(new SDKInitializationException());
            return null;
        }
        final Call<ResponseGamificationSummary> response = gamificationService.getSummary(getCustomerId());

        try {
            final ResponseGamificationSummary body = response.execute().body();
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
            raiseException(ignored);
        }
        return null;
    }

    public PaginatedTransaction<WalletTransaction> getTransactions(int count){
        this.count = count;
        return next();
    }

    private PaginatedTransaction<WalletTransaction> getTransactions(int count, int page) throws Exception {
        if (gamificationService == null){
            raiseException(new SDKInitializationException());
            return null;
        }
        this.count = count;
        final Call<ResponseWalletTransaction> response = gamificationService.getTransaction(getCustomerId(), count, 3, page);
        try {
            final ResponseWalletTransaction code  = response.execute().body();
            if(code != null){
                switch (code.getCode()){
                    case APICodes.INVALID_MISSING_FIELDS:
                        raiseException(new InvalidArgumentsException(code.getMessage()));
                        break;
                    case APICodes.SERVICE_NOT_SETUP:
                        raiseException(new SetupIncompleteException(code.getMessage()));
                        break;
                    case APICodes.RESPONSE_FAILURE:
                        raiseException(new Exception(code.getMessage()));
                        break;
                    case APICodes.RESPONSE_SUCCESS:
                        PaginatedTransaction<WalletTransaction> paginatedTransaction = code.getTransactions();
                        paginatedTransaction.setPaginationHandler(this);
                        return paginatedTransaction;
                }
            }
        } catch (IOException ignored) {
        }
        return null;
    }

    public void reset(){
        transactionPage = 1;
    }


    @Override
    public PaginatedTransaction<WalletTransaction> next() {
        transactionPage++;
        try {
            return getTransactions(count, transactionPage);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public PaginatedTransaction<WalletTransaction> prev() {
        transactionPage--;
        if(transactionPage <= 0){
            transactionPage = 1;
        }
        try {
            return getTransactions(count, transactionPage);
        } catch (Exception e) {
            return null;
        }
    }
}
