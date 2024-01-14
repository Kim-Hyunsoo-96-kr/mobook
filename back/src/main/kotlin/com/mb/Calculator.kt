package com.mb

data class Calculator(
    private var number: Int
) {
    fun add(number: Int){
        this.number += number
    }
    fun minus(number: Int){
        this.number -= number
    }
}