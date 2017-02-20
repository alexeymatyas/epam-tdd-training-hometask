package com.epam.jamp2.rest;

import com.epam.jamp2.model.FixerioResponse;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Alexey on 05.12.2016.
 */
@Service
public interface FixerioServiceProxy {
    @GET("latest")
    Call<FixerioResponse> getRates(@Query("base") String baseCurrencyCode);
}
