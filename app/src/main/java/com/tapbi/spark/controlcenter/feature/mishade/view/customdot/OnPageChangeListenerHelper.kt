package com.tapbi.spark.controlcenter.feature.mishade.view.customdot

abstract class OnPageChangeListenerHelper {
    private var lastLeftPosition: Int = -1
    private var lastRightPosition: Int = -1

    internal abstract val pageCount: Int

    fun onPageScrolled(position: Int, positionOffset: Float) {
        var offset = (position + positionOffset)
//        Timber.e("hachung offset: $offset  /position: $position  /positionOffset: $positionOffset")
        val lastPageIndex = (pageCount - 1).toFloat()
//        Timber.e("hachung lastPageIndex: $lastPageIndex")
        if (offset == lastPageIndex) {
            offset = lastPageIndex - .0001f
        }
        val leftPosition = offset.toInt()
        val rightPosition = leftPosition + 1
//        Timber.e("hachung leftPosition: $leftPosition  /rightPosition: $rightPosition")
        if (rightPosition > lastPageIndex || leftPosition == -1) {
            return
        }

        onPageScrolled(leftPosition, rightPosition, offset % 1)

        if (lastLeftPosition != -1) {
            if (leftPosition > lastLeftPosition) {
                (lastLeftPosition until leftPosition).forEach {
                    resetPosition(it)
                }
            }

            if (rightPosition < lastRightPosition) {
                resetPosition(lastRightPosition)
                ((rightPosition + 1)..lastRightPosition).forEach {
                    resetPosition(it)
                }
            }
        }

        lastLeftPosition = leftPosition
        lastRightPosition = rightPosition
    }

    internal abstract fun onPageScrolled(
        selectedPosition: Int, nextPosition: Int,
        positionOffset: Float
    )

    internal abstract fun resetPosition(position: Int)
}
