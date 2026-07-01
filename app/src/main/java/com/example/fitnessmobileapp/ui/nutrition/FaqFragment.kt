package com.example.fitnessmobileapp.ui.nutrition

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.fitnessmobileapp.R
import com.google.android.material.card.MaterialCardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class FaqFragment : Fragment(R.layout.fragment_faq) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val header = view.findViewById<View>(R.id.header)

        ViewCompat.setOnApplyWindowInsetsListener(header) { v, insets ->

            val statusBarHeight =
                insets.getInsets(WindowInsetsCompat.Type.statusBars()).top

            v.setPadding(
                v.paddingLeft,
                statusBarHeight,
                v.paddingRight,
                v.paddingBottom
            )

            insets
        }

        val btnBack = view.findViewById<View>(R.id.header).findViewById<ImageView>(R.id.btnBack)

        val btnApp = view.findViewById<Button>(R.id.btnApp)
        val btnWorkout = view.findViewById<Button>(R.id.btnWorkout)
        val btnPayment = view.findViewById<Button>(R.id.btnPayment)

        val layoutQuestions =
            view.findViewById<LinearLayout>(R.id.layoutQuestions)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        fun selectButton(selected: Button, vararg others: Button) {

            selected.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor("#20C76F"))
            selected.setTextColor(Color.WHITE)

            others.forEach {

                it.backgroundTintList =
                    ColorStateList.valueOf(Color.WHITE)

                it.setTextColor(Color.GRAY)

            }
        }

        fun addQuestion(question: String, answer: String) {

            val card = MaterialCardView(requireContext())

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.bottomMargin = 20

            card.layoutParams = params
            card.radius = 20f
            card.cardElevation = 6f
            card.setCardBackgroundColor(Color.WHITE)

            val container = LinearLayout(requireContext())
            container.orientation = LinearLayout.VERTICAL

            val title = TextView(requireContext())
            title.text = "▶  $question"
            title.textSize = 18f
            title.setTextColor(Color.BLACK)
            title.setPadding(40, 40, 40, 40)

            val content = TextView(requireContext())
            content.text = answer
            content.textSize = 15f
            content.setTextColor(Color.DKGRAY)
            content.visibility = View.GONE
            content.setPadding(40, 0, 40, 40)

            title.setOnClickListener {

                if (content.visibility == View.VISIBLE) {

                    content.visibility = View.GONE
                    title.text = "▶  $question"

                } else {

                    content.visibility = View.VISIBLE
                    title.text = "▼  $question"

                }

            }

            container.addView(title)
            container.addView(content)

            card.addView(container)

            layoutQuestions.addView(card)

        }

        fun loadAppFaq() {

            layoutQuestions.removeAllViews()

            addQuestion(
                "Tôi nên tập thể dục với tần suất như thế nào?",
                "Bạn có thể lặp lại các bài tập hằng ngày theo nhu cầu. Người mới nên tập 2–3 lần mỗi tuần. Người đã có kinh nghiệm có thể tập mỗi ngày. Nếu bị chấn thương hoặc có bệnh lý, hãy tham khảo ý kiến bác sĩ trước khi tập."
            )

            addQuestion(
                "Tôi là người mới bắt đầu. Tôi cần bắt đầu từ đâu?",
                "Hãy bắt đầu với cường độ nhẹ để cơ thể thích nghi. Sau đó tăng dần mức độ tập luyện và duy trì thành thói quen."
            )

            addQuestion(
                "Chế độ ăn uống có cần thiết không?",
                "Có. Chế độ ăn hợp lý sẽ giúp tối đa hóa hiệu quả tập luyện và giảm cân."
            )

            addQuestion(
                "Có nên tuân thủ nghiêm ngặt kế hoạch bữa ăn?",
                "Bạn có thể thay đổi món ăn hoặc khẩu phần theo nhu cầu miễn vẫn đảm bảo dinh dưỡng."
            )

            addQuestion(
                "Có nên khởi động và hạ nhiệt?",
                "Có. Khởi động giúp giảm chấn thương, hạ nhiệt giúp cơ thể phục hồi tốt hơn."
            )
        }

        fun loadWorkoutFaq() {

            layoutQuestions.removeAllViews()

            addQuestion(
                "Nó quá khó với tôi.",
                "Bạn có thể giảm số lượng bài tập hoặc thay đổi kế hoạch tập phù hợp với thể lực của mình."
            )

            addQuestion(
                "Tôi cảm thấy không khỏe.",
                "Nếu cảm thấy đau hoặc khó chịu trong lúc tập, hãy dừng ngay và tham khảo ý kiến bác sĩ."
            )

            addQuestion(
                "Tôi đã hoàn thành 30 ngày tập luyện. Tiếp theo là gì?",
                "Bạn có thể lặp lại chương trình 30 ngày hoặc chọn một chương trình khác phù hợp hơn."
            )

            addQuestion(
                "Không thấy có kết quả?",
                "Giảm cân là một quá trình lâu dài. Hãy kiên trì tập luyện và kết hợp chế độ ăn uống hợp lý."
            )
        }

        fun loadPaymentFaq() {

            layoutQuestions.removeAllViews()

            addQuestion(
                "Cách tiếp tục sử dụng phiên bản Cao Cấp trên thiết bị mới?",
                "Đăng nhập cùng tài khoản Google Play đã mua gói Premium và mở lại ứng dụng."
            )

            addQuestion(
                "Làm thế nào để hủy đăng ký?",
                "Google Play → Đăng ký → Chọn ứng dụng → Hủy đăng ký."
            )

            addQuestion(
                "Tôi đã bị tính phí nhưng không có thông báo!",
                "Nếu không hủy đăng ký, Google Play sẽ tự động gia hạn trước khi gói hiện tại kết thúc."
            )

            addQuestion(
                "Tôi có thể được hoàn tiền không?",
                "Việc hoàn tiền do Google Play quản lý. Hãy liên hệ bộ phận hỗ trợ Google Play."
            )

            addQuestion(
                "Đã gỡ cài đặt ứng dụng nhưng vẫn bị tính phí?",
                "Gỡ ứng dụng không đồng nghĩa với hủy đăng ký. Bạn cần hủy trong Google Play."
            )
        }

        btnApp.setOnClickListener {
            selectButton(btnApp, btnWorkout, btnPayment)
            loadAppFaq()
        }

        btnWorkout.setOnClickListener {
            selectButton(btnWorkout, btnApp, btnPayment)
            loadWorkoutFaq()
        }

        btnPayment.setOnClickListener {
            selectButton(btnPayment, btnApp, btnWorkout)
            loadPaymentFaq()
        }

        selectButton(btnApp, btnWorkout, btnPayment)
        loadAppFaq()
    }
}