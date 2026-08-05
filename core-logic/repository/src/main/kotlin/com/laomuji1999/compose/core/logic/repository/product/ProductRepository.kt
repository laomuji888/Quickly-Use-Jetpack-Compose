package com.laomuji1999.compose.core.logic.repository.product

import com.laomuji1999.compose.core.logic.model.response.ProductResponse
import com.laomuji1999.compose.core.logic.model.resultSuccessOrFailure
import com.laomuji1999.compose.core.logic.network.http.HttpService
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    httpService: HttpService,
) {
    val client = httpService.client
    suspend fun getProducts(page: Int): Result<ProductResponse> {
        return resultSuccessOrFailure {
            val response =
                client.get("https://dummyjson.com/products?select=title,price,description") {
                    contentType(ContentType.Application.Json)
                    parameter("limit", 10)
                    parameter("skip", page * 10)
                }
            response.body()
        }
    }
}