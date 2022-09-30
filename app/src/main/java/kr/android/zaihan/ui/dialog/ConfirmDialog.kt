package kr.android.zaihan.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kr.android.zaihan.databinding.ViewConfirmDialogBinding

interface OnDialogClickListener{
    fun onConfirmClickListener()
    fun onCancelClickListener()
}

class ConfirmDialog :DialogFragment(){

    private lateinit var binding: ViewConfirmDialogBinding
    private var listener:OnDialogClickListener? = null

    open var title:String? = ""
    open var message:String? = ""
    open var cancelTitle:String? = ""
    open var confirmTitle:String? = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ViewConfirmDialogBinding.inflate(inflater, container, false)

        binding.tvTitle.text = title
        binding.tvMessage.text = message
        binding.btnCancel.text = cancelTitle
        binding.btnConfirm.text = confirmTitle

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)
        binding.btnConfirm.setOnClickListener {
            listener?.onConfirmClickListener()
        }

        binding.btnCancel.setOnClickListener {
            listener?.onCancelClickListener()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this.listener = null
    }

    fun setOnClickListener(listener : OnDialogClickListener) {
        this.listener = listener
    }
}