package ru.focusstart.kireev.homeworktimer.fragments

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.android.synthetic.main.fragment_timer.*
import ru.focusstart.kireev.homeworktimer.R
import ru.focusstart.kireev.homeworktimer.workers.PushWorker

class TimerFragment : Fragment(R.layout.fragment_timer), View.OnClickListener {
    private var handler: Handler? = null
    private var isRunning = false
    private var count = 0
    private var isExit = false

    companion object {
        fun newInstance() = TimerFragment()
        private const val ARG_BUNDLE_IS_RUNNING = "isRunning"
        private const val ARG_BUNDLE_COUNT = "count"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonStartTimer.setOnClickListener(this)
        buttonPauseTimer.setOnClickListener(this)
        buttonResetTimer.setOnClickListener(this)

        savedInstanceState?.let {
            isRunning = it.getBoolean(ARG_BUNDLE_IS_RUNNING)
            count = it.getInt(ARG_BUNDLE_COUNT)
            changeButton(!isRunning)
        }

        textView.text = "$count"

        initLoop()

        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    startWorker()
                    requireActivity().finish()
                }
            })
    }

//    private fun initLoop() {
//        val task = object : Runnable {
//            override fun run() {
//                if (isRunning) {
//                    count++
//                    textView.text = "$count"
//                }
//                handler?.postDelayed(this, 1000)
//            }
//        }
//        Thread {
//            handler = Handler(Looper.getMainLooper())
//            handler?.post(task)
//        }.start()
//    }

    private fun initLoop() {
        handler = Handler(Looper.getMainLooper())
        Thread {
            while (true) {
                if (isExit) {
                    break
                }
                if (isRunning) {
                    count++
                }
                handler?.post {
                    if (textView != null) {
                        textView.text = "$count"
                    }
                }
                Thread.sleep(1000)
            }
        }.start()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.buttonStartTimer -> startTimer()
            R.id.buttonPauseTimer -> pauseTimer()
            R.id.buttonResetTimer -> resetTimer()
        }
    }

    private fun startTimer() {
        isRunning = true
        changeButton(false)
    }

    private fun pauseTimer() {
        isRunning = false
        changeButton(true)
    }

    private fun resetTimer() {
        isRunning = false
        count = 0
        changeButton(true)
    }

    private fun startWorker() {
        val data = PushWorker.packData(count)

        val workRequest = OneTimeWorkRequestBuilder<PushWorker>()
            .setInputData(data)
            .build()

        WorkManager.getInstance(requireContext()).enqueue(workRequest)
    }

    private fun changeButton(needTurnOn: Boolean) {
        val buttonColor = if (needTurnOn) {
            requireContext().getColor(R.color.purple_500)
        } else {
            Color.GRAY
        }
        buttonStartTimer.isClickable = needTurnOn
        buttonStartTimer.setBackgroundColor(buttonColor)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ARG_BUNDLE_COUNT, count)
        outState.putBoolean(ARG_BUNDLE_IS_RUNNING, isRunning)
    }

    override fun onStop() {
        super.onStop()
        isRunning = false
        handler = null
        isExit = true
    }
}