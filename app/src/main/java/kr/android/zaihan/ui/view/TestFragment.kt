package kr.android.zaihan.ui.view

import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.customcameraalbum.R.*
import com.android.customcameraalbum.album.model.SelectedItemCollection
import com.android.customcameraalbum.common.entity.SaveStrategy
import com.android.customcameraalbum.common.enums.MimeType
import com.android.customcameraalbum.common.enums.MultimediaTypes
import com.android.customcameraalbum.listener.OnMainListener
import com.android.customcameraalbum.settings.AlbumSetting
import com.android.customcameraalbum.settings.CameraSetting
import com.android.customcameraalbum.settings.GlobalSetting
import com.android.customcameraalbum.settings.MultiMediaSetting
import com.android.customcameraalbum.videoedit.VideoEditManager
import kr.android.zaihan.Glide4Engine
import kr.android.zaihan.R
import kr.android.zaihan.databinding.ViewTestBinding
import java.io.File


class TestFragment : Fragment(){

    lateinit var binding:ViewTestBinding



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ViewTestBinding.inflate(inflater, container, false)
        bindControll()

        return binding.root
    }

    fun bindControll() {
        binding.btnCamera.setOnClickListener {

        }

        binding.btnAlbum.setOnClickListener {

        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == REQUEST_CODE_CHOOSE) {
//            val path: List<Uri> = MultiMediaSetting.obtainResult(data)
//            if (MultiMediaSetting.obtainMultimediaType(data) == MultimediaTypes.VIDEO) {
//                // 비디오 파일일 경우, 썸네일 보여줌
//                val pathList: List<String> = MultiMediaSetting.obtainPathResult(data)
//
//                Log.d("A.lee", "files" + pathList.toString())
//
//
////                Glide.with(this)
////                    .load(path.get(0))
////                    .apply(RequestOptions.centerCropTransform())
////                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
////                    .apply(RequestOptions.placeholderOf(R.drawable.no_image))
////                    .into(binding.img1)
////                Glide4Engine().loadThumbnail()
//
//
//                var size = Size(80, 80)
//                var cancelSignal = CancellationSignal()
//                try {
//                    var bitmap = ThumbnailUtils.createVideoThumbnail(pathList.get(0), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND)
//                    var result = ThumbnailUtils.extractThumbnail(bitmap, 160, 160);
//                    binding.img1.setImageBitmap(result)
//                } catch (e:Exception) {
//
//                }
//
////                val file = File(pathList.get(0))
////                val absolutePath = file.absolutePath
////                val fileName = file.name
////                val source = BitmapFactory.decodeFile(file.absolutePath)
////                binding.img1.setImageBitmap(ThumbnailUtils.extractThumbnail(source, 80, 80))
//            } else {
//
//            }
//
//
//        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun convertDPtoPX(dp: Int): Int {
        val density = resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }

    private fun getRealPathFromURI(contentUri: Uri): String? {
        var column_index = 0
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = requireActivity().contentResolver.query(contentUri, proj, null, null, null)
        cursor?.let {
            if (it.moveToFirst()) {
                column_index = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            }
            return it.getString(column_index)
        }

        return ""
    }
    fun getWebVideoThumbnail(uri : Uri) : Bitmap? {
        val retriever = MediaMetadataRetriever()

        try {
            retriever.setDataSource(uri.toString(), HashMap<String,String>())
            return retriever.getFrameAtTime((1 * 1000000).toLong(), MediaMetadataRetriever.OPTION_CLOSEST)
        } catch (e : IllegalArgumentException){
            e.printStackTrace()
        } catch (e : RuntimeException){
            e.printStackTrace()
        } finally {
            try {
                retriever.release()
            } catch (e : RuntimeException){
                e.printStackTrace()
            }
        }
        return null
    }

    class VideoThumbnailUtil {
        //영상의 1초 시간의 사진을 가져옴
        val thumbnailTime = 1

        fun getWebVideoThumbnail(uri : Uri) : Bitmap? {
            val retriever = MediaMetadataRetriever()

            try {
                retriever.setDataSource(uri.toString(), HashMap<String,String>())
                return retriever.getFrameAtTime((1 * 1000000).toLong(), MediaMetadataRetriever.OPTION_CLOSEST)
            } catch (e : IllegalArgumentException){
                e.printStackTrace()
            } catch (e : RuntimeException){
                e.printStackTrace()
            } finally {
                try {
                    retriever.release()
                } catch (e : RuntimeException){
                    e.printStackTrace()
                }
            }
            return null
        }
    }
}