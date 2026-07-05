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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.example.fitnessmobileapp.data.repository.UserDataPrefs
import java.io.File

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

    // Chức năng: hiện bảng xác nhận xóa toàn bộ dữ liệu của tài khoản hiện tại.
    // Chỉ xóa dữ liệu cá nhân, tiến độ, báo cáo, nhắc nhở; không xóa tài khoản đăng nhập.
    private fun showDeleteAllDataDialog() {
        showProfileConfirmDialog(
            title = "Xóa tất cả dữ liệu?",
            message = "Hồ sơ, tiến độ tập luyện, báo cáo và dữ liệu cá nhân sẽ bị xóa.\n\nTài khoản đăng nhập vẫn được giữ lại.",
            confirmText = "XÓA DỮ LIỆU",
            onConfirm = {
                deleteCurrentUserDataKeepAccount()
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

    /// Chức năng: chỉ đặt lại tiến độ tập luyện của tài khoản hiện tại.
    // Không xóa hồ sơ, không xóa tài khoản, không xóa nhắc nhở.
    // Hàm này xóa trực tiếp file XML đang tồn tại để tránh Android tạo thêm file SharedPreferences rỗng.
    private fun resetWorkoutProgressOnly() {
        val context = requireContext()

        deleteExistingWorkoutPreferenceFiles(context)

        Toast.makeText(
            context,
            "Đã đặt lại tiến độ tập luyện",
            Toast.LENGTH_SHORT
        ).show()
    }

    // Chức năng: xóa các file SharedPreferences liên quan đến tiến độ tập luyện đang tồn tại.
    private fun deleteExistingWorkoutPreferenceFiles(context: Context) {
        val safeUsername = UserDataPrefs.getSafeUsername(context)
        val sharedPrefsDir = File(context.applicationInfo.dataDir, "shared_prefs")
        if (!sharedPrefsDir.exists()) return

        // Xóa sạch dữ liệu ăn uống trong RAM trước
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit().clear().commit()

        sharedPrefsDir.listFiles()?.forEach { file ->
            val fileName = file.name
            if (!fileName.endsWith(".xml")) return@forEach
            
            val lowerName = fileName.lowercase()
            // Bảo vệ tuyệt đối file tài khoản và đăng nhập
            if (lowerName.contains("accounts_data") || lowerName.contains("login_data")) return@forEach

            val prefName = fileName.removeSuffix(".xml")

            // Các file tiến độ gắn với tên user (từ ảnh thực tế)
            val isUserWorkoutFile = fileName.startsWith("user_${safeUsername}_") && (
                fileName.contains("plan_progress") || 
                fileName.contains("workout_report") || 
                fileName.contains("weight_report") || 
                fileName.contains("custom_exercise_target")
            )

            // File ăn uống chung
            val isNutritionFile = fileName == "user_prefs.xml"

            if (isUserWorkoutFile || isNutritionFile) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    context.deleteSharedPreferences(prefName)
                } else {
                    file.delete()
                }
            }
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

    // Chức năng: xóa toàn bộ dữ liệu phát sinh của tài khoản hiện tại nhưng giữ lại tài khoản đăng nhập.
    // Hàm này KHÔNG xóa accounts_data.xml, vì accounts_data lưu username/password.
    // Hàm này cũng giữ login_data.xml để app vẫn biết người dùng hiện tại là ai.
    // Sau khi xóa, app chuyển về OnboardingActivity để người dùng thiết lập lại hồ sơ.
    // Lưu ý: hàm này xóa trực tiếp file XML đang tồn tại, không gọi Manager để tránh tạo thêm file rỗng.
    private fun deleteCurrentUserDataKeepAccount() {
        val context = requireContext()
        val currentUsername = UserDataPrefs.getCurrentUsername(context)

        // Chức năng: hủy lịch nhắc nhở để sau khi xóa dữ liệu không còn thông báo cũ.
        cancelAllReminderAlarms()

        // Chức năng: xóa trực tiếp các file XML liên quan trong shared_prefs.
        // Cách này không tạo thêm file rác như khi gọi getSharedPreferences cho file chưa tồn tại.
        deleteExistingPreferenceFilesForCurrentUser(context)

        // Chức năng: giữ lại trạng thái đăng nhập hiện tại.
        // Dùng commit() thay vì apply() để đảm bảo dữ liệu được ghi xuống file ngay lập tức 
        // trước khi app chuyển màn hình, giúp tránh xung đột với các tiến trình xóa file.
        context.getSharedPreferences("login_data", Context.MODE_PRIVATE)
            .edit()
            .putString("current_user", currentUsername)
            .putBoolean("remember_login", true)
            .commit()

        Toast.makeText(
            context,
            "Đã xóa dữ liệu. Vui lòng thiết lập lại hồ sơ.",
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(context, OnboardingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    // Chức năng: xóa triệt để các file SharedPreferences đang tồn tại của tài khoản hiện tại.
    // Chỉ giữ lại đúng 2 file tài khoản, dọn sạch mọi file rác khác theo ảnh thực tế Device File Explorer.
    private fun deleteExistingPreferenceFilesForCurrentUser(context: Context) {
        val sharedPrefsDir = File(context.applicationInfo.dataDir, "shared_prefs")
        if (!sharedPrefsDir.exists()) return

        // Xóa sạch dữ liệu ăn uống trong RAM trước
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE).edit().clear().commit()

        sharedPrefsDir.listFiles()?.forEach { file ->
            val fileName = file.name
            if (!fileName.endsWith(".xml")) return@forEach

            val lowerName = fileName.lowercase()
            
            // Bảo vệ tuyệt đối 2 file tài khoản quan trọng nhất
            if (lowerName.contains("accounts_data") || lowerName.contains("login_data")) {
                return@forEach
            }

            val prefName = fileName.removeSuffix(".xml")

            // Xóa tất cả các file XML còn lại (profile, weight, workout, reminder, shopping...)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                // Ưu tiên dùng API hệ thống để xóa sạch cả cache RAM
                context.deleteSharedPreferences(prefName)
            } else {
                file.delete()
            }
        }
    }

    // Chức năng: lấy tài khoản hiện tại để xóa đúng dữ liệu của user đó.
    private fun getCurrentUsername(): String {
        return UserDataPrefs.getCurrentUsername(requireContext())
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