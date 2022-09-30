package kr.android.zaihan.ui.dialog

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kr.android.zaihan.databinding.ViewPermissionDialogBinding
import kr.android.zaihan.databinding.ViewPermissionItemBinding

class PermissionDialog :Fragment(){

    private lateinit var binding: ViewPermissionDialogBinding
    private var listener:OnDialogClickListener? = null
    private var ALL_PERMISSSION_REQUEST = 10000

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ViewPermissionDialogBinding.inflate(inflater, container, false)
        PermissionItem.values().forEach {
            var bindItem = ViewPermissionItemBinding.inflate(inflater,null, false)
            bindItem.imgIcon.setImageResource(it.getImageRecource())
            bindItem.tvTitle.text = it.getPermissionTitle()
            bindItem.tvDesc.text = it.getPermissionDesc()
            binding.llContent.addView(bindItem.root)
        }
        PermissionItem.values().forEach {
            var bindItem = ViewPermissionItemBinding.inflate(inflater,null, false)
            bindItem.imgIcon.setImageResource(it.getImageRecource())
            bindItem.tvTitle.text = it.getPermissionTitle()
            bindItem.tvDesc.text = it.getPermissionDesc()
            binding.llContent.addView(bindItem.root)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnConfirm.setOnClickListener {
            requestPermissions()
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

    /***
     * 전화번호 취득용 전화번호 권한 요청
     */
    fun requestPermissions() {
        var permissionList = arrayListOf<String>()
        PermissionItem.values().forEach { permission ->
            if ( permission.getPermission().count() > 0 ) {
                permission.getPermission().forEach {
                    if ( ContextCompat.checkSelfPermission(requireActivity(), it ) != PackageManager.PERMISSION_GRANTED ) {
                        permissionList.add(it)
                    }
                }
            }
        }

        if ( permissionList.count() > 0  ) {
            requestPermissions(permissionList.toTypedArray(), ALL_PERMISSSION_REQUEST)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        listener?.onConfirmClickListener()
    }
}