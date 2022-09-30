package kr.android.zaihan.ui.dialog

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kr.android.zaihan.databinding.ViewDialogBinding
import kr.android.zaihan.network.vo.EmergencyNoticeData

class EmergencyDialog :DialogFragment(){

    private lateinit var binding: ViewDialogBinding
    private var listener:OnDialogClickListener? = null

    var id              = ""
    var title           = ""
    var content         = ""
    var createdDate     = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ViewDialogBinding.inflate(inflater, container, false)
        binding.tvBody.text = content
        binding.tvTitlesub.text = title
        binding.tvDate.text = createdDate
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)
        binding.tvBody.movementMethod = ScrollingMovementMethod()
        binding.btnConfirm.setOnClickListener {
            this.listener?.onConfirmClickListener()
        }
    }

    fun setData(data:EmergencyNoticeData) {
        content = data.description
        title = data.title
        createdDate = data.createdAt
    }

    fun setOnClickListener(listener : OnDialogClickListener) {
        this.listener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.listener = null
    }
}