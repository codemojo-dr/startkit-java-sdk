package io.codemojo.sdk.network;


import io.codemojo.sdk.models.GenericResponse;
import io.codemojo.sdk.responses.ResponseLoyalty;
import io.codemojo.sdk.responses.ResponseLoyaltyMaximumRedemption;
import io.codemojo.sdk.responses.ResponseLoyaltySummary;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * Created by shoaib on 22/10/14.
 */
public interface ILoyalty {

    @PUT("/v1/services/loyalty")
    @FormUrlEncoded
    Call<ResponseLoyalty> addLoyaltyPoints(@Field("customer_id") String customer_id, @Field("transaction") float transaction,
                                           @Field("transaction_id") String transaction_id, @Field("meta") String meta,
                                           @Field("tag") String tag, @Field("platform") String platform,
                                           @Field("service") String service);

    @GET("/v1/services/loyalty")
    Call<ResponseLoyalty> calculateLoyaltyPoints(@Query("customer_id") String customer_id, @Query("transaction") float transaction_value,
                                                 @Query("platform") String platform, @Query("service") String service);

    @DELETE("/v1/services/loyalty")
    Call<GenericResponse> redeemLoyaltyPoints(@Query("customer_id") String customer_id, @Query("transaction") float transaction_value,
                                              @Query("value") float redemption_value, @Query("transaction_id") String transaction_id, @Query("platform") String platform,
                                              @Query("service") String service, @Query("meta") String meta, @Query("tag") String tag);

    @GET("/v1/services/loyalty/calculate-redemption")
    Call<ResponseLoyaltyMaximumRedemption> maximumRedemption(@Query("customer_id") String customer_id, @Query("transaction") float transaction_value,
                                                             @Query("platform") String platform, @Query("service") String service);

    @GET("/v1/services/loyalty/summary/{customer_id}")
    Call<ResponseLoyaltySummary> summary(@Path("customer_id") String customer_id);

    @DELETE("/v1/services/loyalty/transaction/{transaction_id}")
    Call<GenericResponse> cancel(@Path("transaction_id") String customer_id);

    @PUT("/v1/services/loyalty/transaction/refund/{transaction_id}")
    Call<GenericResponse> refund(@Path("transaction_id") String customer_id);

}
