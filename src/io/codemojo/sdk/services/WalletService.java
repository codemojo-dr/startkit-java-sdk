package io.codemojo.sdk.services;

import io.codemojo.sdk.exceptions.InvalidArgumentsException;
import io.codemojo.sdk.exceptions.SDKInitializationException;
import io.codemojo.sdk.exceptions.SetupIncompleteException;
import io.codemojo.sdk.facades.IPagination;
import io.codemojo.sdk.models.PaginatedTransaction;
import io.codemojo.sdk.models.WalletBalance;
import io.codemojo.sdk.models.WalletTransaction;
import io.codemojo.sdk.network.IWallet;
import io.codemojo.sdk.responses.ResponseWalletBalance;
import io.codemojo.sdk.responses.ResponseWalletTransaction;
import io.codemojo.sdk.utils.APICodes;
import retrofit2.Call;

import java.io.IOException;

/**
 * Created by shoaib on 16/06/16.
 */
public class WalletService extends BaseService implements IPagination<WalletTransaction> {

    private final IWallet walletService;
    private int transactionPage = 0;
    private int count;

    /**
     * @param authenticationService
     */
    public WalletService(AuthenticationService authenticationService) {
        super(authenticationService, IWallet.class);
        walletService = (IWallet) getService();
    }

    /**
     * @return WalletBalance
     */
    public WalletBalance getWalletBalance() throws Exception {
        if (walletService == null){
            raiseException(new SDKInitializationException());
            return null;
        }
        final Call<ResponseWalletBalance> response = walletService.getBalance(getCustomerId());
        try {
            final ResponseWalletBalance code  = response.execute().body();
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
                        return code.getBalance();
                }
            }
        } catch (IOException ignored) {
            raiseException(ignored);
        }
        return null;
    }

    /**
     * @param count
     * @return PaginatedTransaction<WalletTransaction>
     */
    public PaginatedTransaction<WalletTransaction> getTransactions(int count){
        this.count = count;
        return next();
    }

    /**
     * @param count
     * @param page
     * @return PaginatedTransaction<WalletTransaction>
     */
    private PaginatedTransaction<WalletTransaction> getTransactions(int count, int page) throws Exception {
        if (walletService == null){
            raiseException(new SDKInitializationException());
            return null;
        }
        this.count = count;
        final Call<ResponseWalletTransaction> response = walletService.getTransaction(getCustomerId(), count, -1, page);
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

    /**
     * Resets the page to 1
     */
    public void reset(){
        transactionPage = 1;
    }

    /**
     * @return PaginatedTransaction<WalletTransaction>
     */
    @Override
    public PaginatedTransaction<WalletTransaction> next() {
        transactionPage++;
        try {
            return getTransactions(count, transactionPage);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @return PaginatedTransaction<WalletTransaction>
     */
    @Override
    public PaginatedTransaction<WalletTransaction> prev() {
        transactionPage--;
        if(transactionPage <= 0){
            transactionPage = 1;
        }
        return getTransactions(transactionPage);

    }
}
