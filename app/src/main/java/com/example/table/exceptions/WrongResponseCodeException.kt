package com.example.table.exceptions

class WrongResponseCodeException(override val message: String? = null, val code: Int): Exception(message) {
}