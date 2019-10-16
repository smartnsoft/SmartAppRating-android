package com.smartnsoft.smartapprating.ws;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import androidx.annotation.AnyThread;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import android.text.TextUtils;

import com.smartnsoft.smartapprating.bo.Configuration;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * @author Adrien Vitti
 * @since 2018.01.29
 */

public final class SmartAppRatingServices
{

  private ConfigurationAPI configurationAPI;

  private static volatile SmartAppRatingServices instance;

  private SmartAppRatingServices(@NonNull final String baseURL, @Nullable final File cachefileDirectory,
      @IntRange(from = 1024 * 1024) final int cacheSize)
  {
    if (TextUtils.isEmpty(baseURL))
    {
      throw new IllegalStateException("Base URL for the SmartAppRatingServices cannot be empty");
    }

    final OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES);

    if (cachefileDirectory != null)
    {
      okHttpBuilder.cache(new Cache(cachefileDirectory, cacheSize));
    }

    final OkHttpClient client = okHttpBuilder.build();

    configurationAPI = new Builder()
        .baseUrl(baseURL)
        .addConverterFactory(MoshiConverterFactory.create())
        .client(client)
        .build()
        .create(ConfigurationAPI.class);
  }

  public static SmartAppRatingServices get(final String baseURL, @Nullable final File cachefileDirectory,
      final int cacheSize)
  {
    if (instance == null)
    {
      synchronized (SmartAppRatingServices.class)
      {
        instance = new SmartAppRatingServices(baseURL, cachefileDirectory, cacheSize);
      }
    }
    return instance;
  }

  @WorkerThread
  @Nullable
  public Configuration getConfiguration(final String configurationFilePath)
      throws IOException
  {
    final Call<Configuration> configurationCall = configurationAPI.getConfiguration(configurationFilePath);
    final Response<Configuration> configurationResponse = configurationCall.execute();
    if (configurationResponse.isSuccessful())
    {
      return configurationResponse.body();
    }
    return null;
  }

  @AnyThread
  public void getConfiguration(final String configurationFilePath, Callback<Configuration> callback)
  {

    final Call<Configuration> configurationCall = configurationAPI.getConfiguration(configurationFilePath);
    configurationCall.enqueue(callback);
  }

}
