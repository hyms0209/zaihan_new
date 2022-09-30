package kr.android.zaihan.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import kr.android.zaihan.databinding.ViewAlertDialogBinding

class AlertDialog :DialogFragment(){

    private lateinit var binding: ViewAlertDialogBinding
    private var listener:OnDialogClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ViewAlertDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)
        binding.btnConfirm.setOnClickListener {
            listener?.onConfirmClickListener()
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