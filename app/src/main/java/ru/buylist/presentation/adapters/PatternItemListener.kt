package ru.buylist.presentation.adapters

import ru.buylist.data.entity.Pattern

interface PatternItemListener {

    fun onPatternClicked(pattern: Pattern)

    fun onButtonMoreClick(pattern: Pattern)
}