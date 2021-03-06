package com.cococloudy.magnolia.controller

import com.cococloudy.magnolia.KeywordAndCountDTO
import com.cococloudy.magnolia.extractAccountId
import com.cococloudy.magnolia.service.PlaceSearchHistoryService
import org.springframework.http.ResponseEntity
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(produces = ["application/json"])
class PlaceSearchHistoryController(
    private val placeSearchHistoryService: PlaceSearchHistoryService,
) {

    @GetMapping("/api/v1/histories/placeSearch/me")
    fun getMyPlaceSearchHistories(
        @RequestParam(required = false) uniqueKeyword: Boolean?,
        request: SecurityContextHolderAwareRequestWrapper
    ): ResponseEntity<List<Any>> {
        val accountId = request.extractAccountId()

        val myPlaceSearchHistories =
            placeSearchHistoryService.getPlaceSearchHistories(accountId, uniqueKeyword ?: false)

        return ResponseEntity.ok(myPlaceSearchHistories)
    }

    @GetMapping("/api/v1/histories/placeSearch/top10")
    fun getTop10PlaceSearchHistories(
        request: SecurityContextHolderAwareRequestWrapper
    ): ResponseEntity<List<KeywordAndCountDTO>> {
        val frequentPlaceSearchKeywords = placeSearchHistoryService.getFrequentPlaceSearchKeywords(10)

        return ResponseEntity.ok(frequentPlaceSearchKeywords)
    }
}