package com.example.piaopiao
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

/**
 * ViewModel的基类
 */
open class BaseViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * 多个任务串行发起请求
     */
    fun launchSync(startLaunch: (() -> Unit)? = null, finallyLaunch: (() -> Unit)? = null,
                   launch: suspend CoroutineScope.() -> Unit): Job {
        var isCancelled = false
        return viewModelScope.launch(Dispatchers.IO) {
            try {
                startLaunch?.invoke()
                launch()
            } catch (e: Exception) {
                if (e is CancellationException) {
                    // 取消Job.cancel()后会抛出该异常
                    isCancelled = true
                }
            } finally {
                if (!isCancelled) {
                    finallyLaunch?.invoke()
                }
            }
        }
    }

    /**
     * 多个任务并发
     */
    fun <T> launchAsync(startLaunch: (() -> Unit)? = null, finallyLaunch: (() -> Unit)? = null,
                        launch: suspend CoroutineScope.() -> T): Deferred<T?> {
        var isCancelled = false
        return viewModelScope.async(Dispatchers.IO) {
            try {
                startLaunch?.invoke()
                launch()
            } catch (e: Exception) {
                if (e is CancellationException) {
                    // 取消Job.cancel()后会抛出该异常
                    isCancelled = true
                    null
                } else {
                    null
                }
            } finally {
                if (!isCancelled) {
                    finallyLaunch?.invoke()
                }
            }
        }
    }


}