package com.cococloudy.magnolia.service

import com.cococloudy.magnolia.*
import com.cococloudy.magnolia.security.BCryptPasswordEncoder
import com.cococloudy.magnolia.security.JwtService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class AccountService {

    @Autowired
    private lateinit var accountRepository: AccountRepository

    @Autowired
    private lateinit var encoder: BCryptPasswordEncoder

    @Autowired
    private lateinit var jwtService: JwtService

    fun isAccountIdExist(accountId: String): Boolean {
        return accountRepository.findByAccountId(accountId) != null
    }

    @Transactional
    fun createAccount(accountId: String, password: String, role: Role): AccountDTO {
        val account = Account(
            accountId = accountId,
            password = encoder.encode(password),
            role = role,
        )
        val createdAccount = accountRepository.save(account)

        return createdAccount.toDTO()
    }

    fun loginByIdAndPassword(accountId: String, password: String): AccessAndRefreshTokenDTO {
        val account = accountRepository.findByAccountId(accountId)
            ?: throw NotFoundException("Account", accountId)

        if (!encoder.matches(password, account.password)) {
            throw WrongRequestException("Account id and password doesn't match")
        }

        return AccessAndRefreshTokenDTO(
            jwtService.createAccessToken(account.id!!),
            jwtService.createRefreshToken(account.id)
        )
    }

    fun throwIfPasswordIsWeak(password: String) {
        if (password.length < 8 || password.length > 32) {
            throw WrongRequestException("Password length should be between 8 and 32")
        }

        var hasAlphabet = false
        var hasNumber = false
        var hasAsciiSpecialChar = false

        val asciiSpecialChars = """!"#$%&'()*+,-.\:;<=>?@[\]^_`{|}~"""

        for (char in password) {
            if (char in 'a'..'z' || char in 'A'..'Z') {
                hasAlphabet = true
                continue
            }
            if (char in '0'..'9') {
                hasNumber = true
                continue
            }
            if (char in asciiSpecialChars) {
                hasAsciiSpecialChar = true
                continue
            }
            throw WrongRequestException("Password should consist of ascii chars")
        }

        if (!hasAlphabet) {
            throw WrongRequestException("Password should contain at least one alphabet")
        }
        if (!hasNumber) {
            throw WrongRequestException("Password should contain at least one number")
        }
        if (!hasAsciiSpecialChar) {
            throw WrongRequestException("Password should contain at least one ascii special char")
        }
    }
}