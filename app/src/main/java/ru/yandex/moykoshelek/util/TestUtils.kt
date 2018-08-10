package ru.yandex.moykoshelek.util

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object TestUtils {
    fun <T> getValueFromLiveData(liveData: LiveData<T>): T {
        val data = arrayOfNulls<Any>(1)
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(o: T?) {
                data[0] = o
                latch.countDown()
                liveData.removeObserver(this)
            }
        }
        liveData.observeForever(observer)
        latch.await(2, TimeUnit.SECONDS)

        @Suppress("UNCHECKED_CAST")
        return data[0] as T
    }
}