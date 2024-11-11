package com.example.MyTaskHub

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.MyTaskHub.databinding.ActivityTimerFragmentBinding

class TimerFragment : Fragment() {

    private lateinit var binding: ActivityTimerFragmentBinding

    private var isRunning = false
    private var startTime = 0L
    private var timeInMillis = 0L

    private val handler = Handler(Looper.getMainLooper())
    private val runnable: Runnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                val currentTime = System.currentTimeMillis()
                val elapsedTime = currentTime - startTime + timeInMillis
                val seconds = (elapsedTime / 1000) % 60
                val minutes = (elapsedTime / (1000 * 60)) % 60
                val hours = (elapsedTime / (1000 * 60 * 60)) % 24
                binding.timerText.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)

                handler.postDelayed(this, 1000) // update every second
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityTimerFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Start button functionality
        binding.btnStart.setOnClickListener {
            if (!isRunning) {
                startTimer()
            }
        }

        // Pause button functionality
        binding.btnPause.setOnClickListener {
            if (isRunning) {
                pauseTimer()
            }
        }

        // Stop button functionality
        binding.btnStop.setOnClickListener {
            resetTimer()
        }
    }

    private fun startTimer() {
        startTime = System.currentTimeMillis()
        handler.post(runnable)
        isRunning = true
        showToast("Timer started")
    }

    private fun pauseTimer() {
        timeInMillis += System.currentTimeMillis() - startTime
        handler.removeCallbacks(runnable)
        isRunning = false
        showToast("Timer paused")
    }

    private fun resetTimer() {
        handler.removeCallbacks(runnable)
        binding.timerText.text = "00:00:00"
        timeInMillis = 0L
        isRunning = false
        showToast("Timer reset")
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
