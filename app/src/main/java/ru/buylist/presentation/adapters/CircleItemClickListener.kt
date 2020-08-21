package ru.buylist.presentation.adapters

import ru.buylist.data.entity.wrappers.CircleWrapper

interface CircleItemClickListener {

    fun onCircleClick(circleWrapper: CircleWrapper)
}