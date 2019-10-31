package com.smartnsoft.smartapprating.ws;

import com.smartnsoft.smartapprating.bo.Configuration;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 *
 * @author Adrien Vitti
 * @since 2018.01.29
 */

interface ConfigurationAPI
{

  @GET("/{configurationFileName}")
  Call<Configuration> getConfiguration(@Path(value="configurationFileName", encoded = true) final String configurationFilePath);
}