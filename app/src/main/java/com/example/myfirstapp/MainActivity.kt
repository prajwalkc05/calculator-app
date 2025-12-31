package com.example.myfirstapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvDisplay: TextView

    private var firstNumber = 0.0
    private var currentOperator = ""
    private var isNewNumber = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDisplay = findViewById(R.id.tvDisplay)

        val digitIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
            R.id.btnDot
        )
        
        digitIds.forEach { id ->
            findViewById<Button>(id).setOnClickListener { view ->
                onDigitClick((view as Button).text.toString())
            }
        }

        val opIds = listOf(
            R.id.btnDiv, R.id.btnMul, R.id.btnSub, R.id.btnAdd
        )
        
        opIds.forEach { id ->
            findViewById<Button>(id).setOnClickListener { view ->
                onOperatorClick((view as Button).text.toString())
            }
        }

        findViewById<Button>(R.id.btnEqual).setOnClickListener { onEqualClick() }
        findViewById<Button>(R.id.btnClear).setOnClickListener { onClearClick() }
        findViewById<Button>(R.id.btnBackspace).setOnClickListener { onBackspaceClick() }
        findViewById<Button>(R.id.btnPercent).setOnClickListener { onPercentClick() }
    }

    private fun onDigitClick(digit: String) {
        if (isNewNumber) {
            if (digit == ".") {
                tvDisplay.text = "0."
            } else {
                tvDisplay.text = digit
            }
            isNewNumber = false
        } else {
            if (digit == "." && tvDisplay.text.contains(".")) {
                return
            }
            tvDisplay.append(digit)
        }
    }

    private fun onOperatorClick(op: String) {
        // If user presses operator again without typing a number, just update the operator
        if (currentOperator.isNotEmpty() && isNewNumber) {
            currentOperator = op
            tvDisplay.text = "${formatResult(firstNumber)} $op"
            return
        }

        if (currentOperator.isNotEmpty()) {
            val currentValue = tvDisplay.text.toString().toDoubleOrNull()
            if (currentValue != null) {
                val result = calculate(firstNumber, currentValue, currentOperator)
                firstNumber = result
            }
        } else {
            firstNumber = tvDisplay.text.toString().toDoubleOrNull() ?: 0.0
        }

        currentOperator = op
        tvDisplay.text = "${formatResult(firstNumber)} $op"
        isNewNumber = true
    }

    private fun onEqualClick() {
        if (currentOperator.isEmpty()) return

        // If display is "5 +", doubleOrNull is null, so use firstNumber as the second operand
        val text = tvDisplay.text.toString()
        val secondNumber = text.toDoubleOrNull() ?: firstNumber

        val result = calculate(firstNumber, secondNumber, currentOperator)

        tvDisplay.text = formatResult(result)
        currentOperator = ""
        isNewNumber = true
    }

    private fun onClearClick() {
        tvDisplay.text = "0"
        firstNumber = 0.0
        currentOperator = ""
        isNewNumber = true
    }

    private fun onBackspaceClick() {
        // Handle backspace when an operator is displayed (e.g. "5 +")
        if (isNewNumber) {
            if (currentOperator.isNotEmpty()) {
                currentOperator = ""
                tvDisplay.text = formatResult(firstNumber)
                isNewNumber = false // Allow editing firstNumber again
            } else {
                tvDisplay.text = "0"
            }
            return
        }

        val currentText = tvDisplay.text.toString()
        if (currentText.length > 1) {
            tvDisplay.text = currentText.dropLast(1)
        } else {
            tvDisplay.text = "0"
            isNewNumber = true
        }
    }

    private fun onPercentClick() {
        val value = tvDisplay.text.toString().toDoubleOrNull() ?: 0.0
        val result = value / 100
        tvDisplay.text = formatResult(result)
        isNewNumber = true
    }

    private fun calculate(n1: Double, n2: Double, op: String): Double {
        return when (op) {
            "+" -> n1 + n2
            "-" -> n1 - n2
            "×" -> n1 * n2
            "÷" -> if (n2 != 0.0) n1 / n2 else 0.0
            else -> n2
        }
    }

    private fun formatResult(result: Double): String {
        if (result.isNaN() || result.isInfinite()) return "Error"
        
        if (result == result.toLong().toDouble()) {
            return result.toLong().toString()
        }
        return result.toString()
    }
}