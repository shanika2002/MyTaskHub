package com.example.MyTaskHub

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.MyTaskHub.databinding.ActivityReminderFragmentBinding
import java.util.*

class ReminderFragment : Fragment() {

    private lateinit var binding: ActivityReminderFragmentBinding
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActivityReminderFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize AlarmManager
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Set Reminder Button
        binding.btnSetReminder.setOnClickListener {
            setReminder()
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setReminder() {
        val title = binding.etReminderTitle.text.toString()
        val calendar = Calendar.getInstance()

        calendar.set(
            binding.datePicker.year,
            binding.datePicker.month,
            binding.datePicker.dayOfMonth,
            binding.timePicker.hour,
            binding.timePicker.minute,
            0
        )

        // Check for exact alarm permission (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                return
            }
        }

        // Create intent for ReminderReceiver
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("reminderTitle", title)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Set the alarm
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Toast.makeText(context, "Reminder Set Successfully!", Toast.LENGTH_SHORT).show()
    }


    }

