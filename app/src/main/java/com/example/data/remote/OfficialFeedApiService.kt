package com.example.data.remote

import java.lang.reflect.Type
import java.util.concurrent.TimeUnit
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Url

interface OfficialFeedApiService {
  @GET
  suspend fun fetchVerifiedFeed(@Url feedUrl: String): OfficialFeedResponseDto

  companion object {
    private val feedJson = Json {
      ignoreUnknownKeys = true
      isLenient = true
      coerceInputValues = true
    }

    fun create(baseUrl: String = "https://localhost/"): OfficialFeedApiService {
      val logging =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
      val client =
        OkHttpClient.Builder()
          .connectTimeout(15, TimeUnit.SECONDS)
          .readTimeout(20, TimeUnit.SECONDS)
          .addInterceptor(logging)
          .build()

      return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(client)
        .addConverterFactory(KotlinxSerializationConverterFactory(feedJson))
        .build()
        .create(OfficialFeedApiService::class.java)
    }
  }
}

private class KotlinxSerializationConverterFactory(
  private val json: Json,
) : Converter.Factory() {
  private val contentType = "application/json; charset=UTF-8".toMediaType()

  override fun responseBodyConverter(
    type: Type,
    annotations: Array<out Annotation>,
    retrofit: Retrofit,
  ): Converter<ResponseBody, *> {
    val serializer: KSerializer<Any> = json.serializersModule.serializer(type)
    return Converter<ResponseBody, Any> { body ->
      body.use { json.decodeFromString(serializer, it.string()) }
    }
  }

  override fun requestBodyConverter(
    type: Type,
    parameterAnnotations: Array<out Annotation>,
    methodAnnotations: Array<out Annotation>,
    retrofit: Retrofit,
  ): Converter<*, RequestBody> {
    val serializer: KSerializer<Any> = json.serializersModule.serializer(type)
    return Converter<Any, RequestBody> { value ->
      json.encodeToString(serializer, value).toRequestBody(contentType)
    }
  }
}
