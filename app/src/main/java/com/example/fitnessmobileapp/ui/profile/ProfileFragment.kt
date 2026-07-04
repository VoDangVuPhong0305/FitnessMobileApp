package com.example.fitnessmobileapp.ui.profile

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fitnessmobileapp.LoginActivity
import com.example.fitnessmobileapp.OnboardingActivity
import com.example.fitnessmobileapp.R
import com.example.fitnessmobileapp.data.repository.CustomExerciseTargetManager
import com.example.fitnessmobileapp.data.repository.PlanProgressManager
import com.example.fitnessmobileapp.data.repository.WorkoutReportManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.example.fitnessmobileapp.data.repository.UserDataPrefs

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    // Chức năng: xử lý các nút bấm trong màn Của tôi.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.btnHelp).setOnClickListener {
            startActivity(Intent(requireContext(), FAQActivity::class.java))
        }

        view.findViewById<View>(R.id.itemMyProfile).setOnClickListener {
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }

        view.findViewById<View>(R.id.itemReminder).setOnClickListener {
            startActivity(Intent(requireContext(), ReminderActivity::class.java))
        }

        view.findViewById<View>(R.id.itemResetProgress).setOnClickListener {
            showResetProgressDialog()
        }

        view.findViewById<View>(R.id.itemDeleteAllData).setOnClickListener {
            showDeleteAllDataDialog()
        }

        view.findViewById<View>(R.id.itemFAQ).setOnClickListener {
            startActivity(Intent(requireContext(), FAQActivity::class.java))
        }

        view.findViewById<View>(R.id.btnLogout).setOnClickListener {
            requireContext()
                .getSharedPreferences("login_data", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("remember_login", false)
                .remove("current_user")
                .apply()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    // Chức năng: hiện bảng xác nhận đặt lại tiến độ giống app mẫu.
    private fun showResetProgressDialog() {
        showProfileConfirmDialog(
            title = "Bắt đầu lại tiến trình?",
            message = "Bạn sẽ bắt đầu lại kế hoạch của mình\ntừ Ngày 1 và hồ sơ sẽ không bị xóa.",
            confirmText = "BẮT ĐẦU LẠI",
            onConfirm = {
                resetWorkoutProgressOnly()
            }
        )
    }

    // Chức năng: hiện bảng xác nhận xóa toàn bộ dữ liệu giống app mẫu.
    private fun showDeleteAllDataDialog() {
        showProfileConfirmDialog(
            title = "Bạn có chắc muốn đặt\nlại ứng dụng và xóa tất\ncả dữ liệu?",
            message = "",
            confirmText = "XÓA",
            onConfirm = {
                deleteAllUserDataButKeepLogin()
            }
        )
    }

    // Chức năng: tạo BottomSheet xác nhận dùng chung cho Đặt lại tiến độ và Xóa dữ liệu.
    private fun showProfileConfirmDialog(
        title: String,
        message: String,
        confirmText: String,
        onConfirm: () -> Unit
    ) {
        val dialog = BottomSheetDialog(requireContext())

        val root = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(28), dp(26), dp(28), dp(34))
            background = GradientDrawable().apply {
                setColor(Color.WHITE)
                cornerRadii = floatArrayOf(
                    dp(24).toFloat(), dp(24).toFloat(),
                    dp(24).toFloat(), dp(24).toFloat(),
                    0f, 0f,
                    0f, 0f
                )
            }
        }

        val closeRow = LinearLayout(requireContext()).apply {
            gravity = Gravity.END
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(54)
            )
        }

        val btnClose = TextView(requireContext()).apply {
            text = "×"
            textSize = 42f
            gravity = Gravity.CENTER
            includeFontPadding = false
            setTextColor(Color.parseColor("#A8A8A8"))
            typeface = Typeface.DEFAULT_BOLD

            layoutParams = LinearLayout.LayoutParams(
                dp(54),
                dp(54)
            )

            setOnClickListener {
                dialog.dismiss()
            }
        }

        closeRow.addView(btnClose)

        val txtTitle = TextView(requireContext()).apply {
            text = title
            textSize = 28f
            gravity = Gravity.CENTER
            includeFontPadding = false
            setTextColor(Color.BLACK)
            typeface = Typeface.DEFAULT_BOLD
            setLineSpacing(dp(2).toFloat(), 1.05f)

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(28)
            }
        }

        val txtMessage = TextView(requireContext()).apply {
            text = message
            textSize = 20f
            gravity = Gravity.CENTER
            includeFontPadding = false
            setTextColor(Color.parseColor("#9CA3AF"))
            typeface = Typeface.DEFAULT_BOLD
            setLineSpacing(dp(4).toFloat(), 1.05f)
            visibility = if (message.isBlank()) View.GONE else View.VISIBLE

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = dp(26)
            }
        }

        val btnConfirm = TextView(requireContext()).apply {
            text = confirmText
            textSize = 22f
            gravity = Gravity.CENTER
            includeFontPadding = false
            setTextColor(Color.WHITE)
            typeface = Typeface.DEFAULT_BOLD
            background = roundedBackground("#EF6376", 40f)

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(72)
            ).apply {
                topMargin = if (message.isBlank()) dp(76) else dp(70)
            }

            setOnClickListener {
                onConfirm()
                dialog.dismiss()
            }
        }

        val btnCancel = TextView(requireContext()).apply {
            text = "HỦY"
            textSize = 22f
            gravity = Gravity.CENTER
            includeFontPadding = false
            setTextColor(Color.parseColor("#555555"))
            typeface = Typeface.DEFAULT_BOLD
            background = roundedBackground("#F5F5F5", 40f)

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(72)
            ).apply {
                topMargin = dp(24)
            }

            setOnClickListener {
                dialog.dismiss()
            }
        }

        root.addView(closeRow)
        root.addView(txtTitle)
        root.addView(txtMessage)
        root.addView(btnConfirm)
        root.addView(btnCancel)

        dialog.setContentView(root)

        dialog.setOnShowListener {
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            )?.background = null
        }

        dialog.show()
    }

    // Chức năng: chỉ reset dữ liệu tập luyện.
    // Không xóa hồ sơ, không xóa tài khoản, không xóa nhắc nhở.
    private fun resetWorkoutProgressOnly() {
        val context = requireContext()

        // Chức năng: reset ngày đã hoàn thành của tất cả kế hoạch.
        PlanProgressManager.resetAllProgress(context)

        // Chức năng: xóa báo cáo kcal, phút tập, số bài, lịch sử workout.
        WorkoutReportManager.clearReportData(context)

        // Chức năng: xóa số lần / thời gian bài tập đã chỉnh custom.
        CustomExerciseTargetManager.clearAllTargets(context)

        // Chức năng: xóa dữ liệu tiến độ cũ nếu app từng lưu ở progress_data.
        context.getSharedPreferences("progress_data", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()

        // Chức năng: xóa thêm một số dữ liệu tập luyện cũ nếu từng được lưu riêng.
        clearWorkoutRelatedPreferences(context)

        Toast.makeText(
            context,
            "Đã đặt lại tiến độ tập luyện",
            Toast.LENGTH_SHORT
        ).show()
    }

    // Chức năng: xóa toàn bộ dữ liệu cá nhân nhưng giữ tài khoản đăng nhập.
    // Sau khi xóa xong chuyển về Onboarding để setup lại từ đầu.
    private fun deleteAllUserDataButKeepLogin() {
        val context = requireContext()
        val username = getCurrentUsername()

        // Chức năng: hủy lịch nhắc nhở để sau khi xóa dữ liệu không còn thông báo cũ.
        cancelAllReminderAlarms()

        // Chức năng: xóa dữ liệu tập luyện.
        PlanProgressManager.resetAllProgress(context)
        WorkoutReportManager.clearReportData(context)
        CustomExerciseTargetManager.clearAllTargets(context)
        clearWorkoutRelatedPreferences(context)

        // Chức năng: xóa hồ sơ chung.
        context.getSharedPreferences("profile_data", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()

        // Chức năng: xóa hồ sơ theo tài khoản.
        context.getSharedPreferences("user_${username}_profile", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()

        // Chức năng: xóa báo cáo cân nặng theo tài khoản.
        context.getSharedPreferences("user_${username}_weight_report", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()

        // Chức năng: xóa nhắc nhở.
        context.getSharedPreferences("reminder_data", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()

        // Chức năng: xóa dữ liệu âm thanh nếu còn dùng.
        context.getSharedPreferences("sound_data", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()

        // Chức năng: xóa dữ liệu tiến độ cũ.
        context.getSharedPreferences("progress_data", Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()

        // Chức năng: giữ tài khoản đăng nhập, không bắt đăng nhập lại.
        context.getSharedPreferences("login_data", Context.MODE_PRIVATE)
            .edit()
            .putString("current_user", username)
            .putBoolean("remember_login", true)
            .apply()

        Toast.makeText(
            context,
            "Đã xóa dữ liệu. Vui lòng thiết lập lại.",
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(context, OnboardingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    // Chức năng: xóa thêm các SharedPreferences liên quan trực tiếp đến bài tập của tài khoản hiện tại.
// Không xóa dữ liệu của tài khoản khác.
    private fun clearWorkoutRelatedPreferences(context: Context) {
        val username = getCurrentUsername()
            .trim()
            .lowercase()
            .replace("@", "_at_")
            .replace(".", "_")
            .replace(" ", "_")

        val workoutPrefs = listOf<String>(
            "user_${username}_plan_progress_pref",
            "user_${username}_workout_report_pref",
            "user_${username}_progress_data",
            "user_${username}_custom_exercise_target",
            "user_${username}_custom_exercise_targets",
            "user_${username}_custom_exercise_target_data",
            "user_${username}_exercise_target_data",
            "user_${username}_workout_session_data",
            "user_${username}_completed_exercise_data",
            "user_${username}_completed_exercises",
            "user_${username}_exercise_progress_data",

            // Chức năng: xóa thêm dữ liệu cũ dạng lưu chung toàn app nếu trước đó từng dùng.
            "plan_progress_pref",
            "workout_report_pref",
            "progress_data",
            "custom_exercise_target",
            "custom_exercise_targets",
            "custom_exercise_target_data",
            "exercise_target_data",
            "workout_session_data",
            "completed_exercise_data",
            "completed_exercises",
            "exercise_progress_data"
        )

        workoutPrefs.forEach { prefName ->
            context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply()
        }
    }

    // Chức năng: hủy các lịch nhắc nhở cũ để sau khi xóa dữ liệu không còn thông báo cũ bật lên.
    private fun cancelAllReminderAlarms() {
        val context = requireContext()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        for (requestCode in 2000..2200) {
            val intent = Intent(context, ReminderReceiver::class.java)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )

            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
            }
        }

        val legacyIntent = Intent(context, ReminderReceiver::class.java)

        val legacyPendingIntent = PendingIntent.getBroadcast(
            context,
            200,
            legacyIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (legacyPendingIntent != null) {
            alarmManager.cancel(legacyPendingIntent)
            legacyPendingIntent.cancel()
        }
    }

    // Chức năng: lấy tài khoản hiện tại để xóa đúng dữ liệu của user đó.
    private fun getCurrentUsername(): String {
        val loginPrefs = requireContext().getSharedPreferences(
            "login_data",
            Context.MODE_PRIVATE
        )

        return loginPrefs.getString("current_user", "guest") ?: "guest"
    }

    // Chức năng: tạo nền bo góc.
    private fun roundedBackground(
        color: String,
        radius: Float
    ): GradientDrawable {
        return GradientDrawable().apply {
            setColor(Color.parseColor(color))
            cornerRadius = dp(radius.toInt()).toFloat()
        }
    }

    // Chức năng: đổi dp sang pixel.
    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}