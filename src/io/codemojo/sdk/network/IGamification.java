package io.codemojo.sdk.network;


import io.codemojo.sdk.responses.ResponseGamification;
import io.codemojo.sdk.responses.ResponseGamificationAchievement;
import io.codemojo.sdk.responses.ResponseGamificationSummary;
import io.codemojo.sdk.responses.ResponseWalletTransaction;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created by shoaib on 22/10/14.
 */
public interface IGamification {

    @PUT("/v1/services/gamification")
    @FormUrlEncoded
    Call<ResponseGamification> addAction(@Field("customer_id") String customer_id, @Field("action_id") String action_id);

    @PUT("/v1/services/gamification/achievements")
    @FormUrlEncoded
    Call<ResponseGamificationAchievement> addAchievement(@Field("customer_id") String customer_id, @Field("action_id") String action_id, @Field("id") String category_id);

    @GET("/v1/services/gamification/summary/{customer_id}")
    Call<ResponseGamificationSummary> getSummary(@Path("customer_id") String customer_id);

    @GET("/v1/services/wallet/transactions/{user}/{count}")
    Call<ResponseWalletTransaction> getTransaction(@Path("user") String customer_id, @Path("count") int count,
                                                   @Query("transaction_type") int transaction_type, @Query("page") int page);

}
