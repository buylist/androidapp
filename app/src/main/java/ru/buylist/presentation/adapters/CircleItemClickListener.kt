package ru.buylist.presentation.adapters

import ru.buylist.data.entity.CircleWrapper

interface CircleItemClickListener {

    fun onCircleClick(circleWrapper: CircleWrapper)
}