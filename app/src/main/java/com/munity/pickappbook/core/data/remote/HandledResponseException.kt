package com.munity.pickappbook.core.data.remote

import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse

class HandledResponseException(response: HttpResponse, cachedResponseText: String) :
    ResponseException(response, cachedResponseText)