package ru.focusstart.kireev.homeworktimer.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import ru.focusstart.kireev.homeworktimer.R

class PushWorker(private val appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    override fun doWork(): Result {
        createNotification()
        return Result.success()
    }

    private fun createNotification() {
        Thread.sleep(10000)
        val data = inputData.getInt(ARG_WORKER_COUNT, 0)
        val channelId = "PushWorker"
        val builder = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(appContext.getString(R.string.notification_title))
            .setContentText(data.toString())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, "Timer", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(1, builder.build())
    }

    companion object {
        private const val ARG_WORKER_COUNT = "count"

        fun packData(count: Int): Data {
            return Data.Builder()
                .putInt(ARG_WORKER_COUNT, count)
                .build()
        }
    }
}