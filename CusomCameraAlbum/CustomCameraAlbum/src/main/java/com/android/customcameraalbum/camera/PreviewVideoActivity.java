package com.android.customcameraalbum.camera;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.android.customcameraalbum.R;
import com.android.customcameraalbum.camera.util.FileUtil;
import com.android.customcameraalbum.settings.CameraSpec;
import com.android.customcameraalbum.settings.GlobalSpec;
import com.android.customcameraalbum.utils.BitmapUtils;
import com.android.customcameraalbum.widget.progressbutton.CircularProgressButton;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.customcameraalbum.common.listener.VideoEditListener;
import com.android.customcameraalbum.common.utils.MediaStoreCompat;
import com.android.customcameraalbum.utils.StatusBarUtils;
import com.android.customcameraalbum.common.utils.ThreadUtils;

import static com.android.customcameraalbum.camera.common.Constants.TYPE_PICTURE;
import static com.android.customcameraalbum.camera.common.Constants.TYPE_VIDEO;
import static com.android.customcameraalbum.constants.Constant.REQUEST_CODE_PREVIEW_VIDEO;

/**
 * 一个预览合成分段录制的视频
 *
 * @author zhongjh
 */
public class PreviewVideoActivity extends AppCompatActivity {

    VideoView mVideoViewPreview;
    ImageView mImgClose;
    CircularProgressButton mBtnConfirm;
    String mPath;
    File mFile;
    int mDuration;

    /**
     * 录像文件配置路径
     */
    private MediaStoreCompat mVideoMediaStoreCompat;
    /**
     * 拍摄配置
     */
    private CameraSpec mCameraSpec;

    /**
     * 打开activity
     *
     * @param fragment 打开者
     * @param path     视频地址
     */
    public static void startActivity(Fragment fragment, String path) {
        Intent intent = new Intent();
        intent.putExtra("path", path);
        intent.setClass(fragment.getContext(), PreviewVideoActivity.class);
        fragment.startActivityForResult(intent, REQUEST_CODE_PREVIEW_VIDEO);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBarUtils.initStatusBar(PreviewVideoActivity.this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_video);
        mPath = getIntent().getStringExtra("path");
        // 初始化设置
        mCameraSpec = CameraSpec.getInstance();
        initView();
        initListener();
        initData();
    }

    @Override
    public void finish() {
        super.finish();
        //关闭窗体动画显示
        this.overridePendingTransition(0, R.anim.activity_close);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSpec.videoEditCoordinator != null) {
            mCameraSpec.videoEditCoordinator.onDestroy();
            mCameraSpec.videoEditCoordinator = null;
        }
    }

    /**
     * 初始化View
     */
    private void initView() {
        mVideoViewPreview = findViewById(R.id.vvPreview);
        mImgClose = findViewById(R.id.imgClose);
        mBtnConfirm = findViewById(R.id.btnConfirm);
        mBtnConfirm.setIndeterminateProgressMode(true);
    }

    private void initListener() {
        mBtnConfirm.setOnClickListener(v -> confirm());
        mImgClose.setOnClickListener(v -> PreviewVideoActivity.this.finish());
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 公共配置
        GlobalSpec mGlobalSpec = GlobalSpec.getInstance();
        mVideoMediaStoreCompat = new MediaStoreCompat(PreviewVideoActivity.this);
        mVideoMediaStoreCompat.setSaveStrategy(mGlobalSpec.videoStrategy == null ? mGlobalSpec.saveStrategy : mGlobalSpec.videoStrategy);

        mFile = new File(mPath);
        playVideo(mFile);
    }

    /**
     * 播放视频,用于录制后，在是否确认的界面中，播放视频
     */
    private void playVideo(File file) {
        mVideoViewPreview.pause();
        // mediaController 是底部控制条
        MediaController mediaController = new MediaController(PreviewVideoActivity.this);
        mediaController.setAnchorView(mVideoViewPreview);
        mediaController.setMediaPlayer(mVideoViewPreview);
        mediaController.setVisibility(View.GONE);
        mVideoViewPreview.setMediaController(mediaController);
        Uri uri = Uri.fromFile(file);
        mVideoViewPreview.setVideoURI(uri);
        // 这段代码需要放在更新视频文件后播放，不然会找不到文件。
        mVideoViewPreview.setVisibility(View.VISIBLE);
        if (!mVideoViewPreview.isPlaying()) {
            mVideoViewPreview.start();
        }
        mVideoViewPreview.setOnPreparedListener(mp -> {
            // 获取时长
            mDuration = mVideoViewPreview.getDuration();
        });
        mVideoViewPreview.setOnCompletionListener(mediaPlayer -> {
            // 循环播放
            if (!mVideoViewPreview.isPlaying()) {
                mVideoViewPreview.start();
            }
        });
    }

    /**
     * 提交
     */
    private void confirm() {
        // 判断是否开启了视频编辑功能
        if (mCameraSpec.videoEditCoordinator != null) {
            // 如果开启了直接压缩
            compress();
        } else {
            // 否则直接转移
            moveVideoFile();
        }
    }

    /**
     * 压缩视频
     */
    private void compress() {
        // 获取文件名称
        String newFileName = mPath.substring(mPath.lastIndexOf(File.separator));
        File newFile = mVideoMediaStoreCompat.createFile(newFileName, 1, false);
        mCameraSpec.videoEditCoordinator.setVideoCompressListener(new VideoEditListener() {
            @Override
            public void onFinish() {
                confirm(newFile);
            }

            @Override
            public void onProgress(int progress, long progressTime) {
                mBtnConfirm.setProgress(progress);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(String message) {

            }
        });
        mCameraSpec.videoEditCoordinator.compress(mPath, newFile.getPath());
    }

    /**
     * 迁移视频文件，缓存文件迁移到配置目录
     */
    private void moveVideoFile() {
        // 执行等待动画
        mBtnConfirm.setProgress(50);
        // 开始迁移文件，将 缓存文件 拷贝到 配置目录
        ThreadUtils.executeByIo(new ThreadUtils.BaseSimpleBaseTask<Void>() {
            @Override
            public Void doInBackground() {
                // 获取文件名称
                String newFileName = mPath.substring(mPath.lastIndexOf(File.separator));
                File newFile = mVideoMediaStoreCompat.createFile(newFileName, 1, false);
                FileUtil.copy(new File(mPath), newFile, null, (ioProgress, file) -> {
                    if (ioProgress >= 1) {
                        ThreadUtils.runOnUiThread(() -> {
                            mBtnConfirm.setProgress(100);
                            confirm(newFile);
                        });
                    }
                });
                return null;
            }

            @Override
            public void onSuccess(Void result) {

            }
        });
    }

    /**
     * 确定该视频
     */
    private void confirm(File newFile) {
        Intent intent = new Intent();
        // 加入视频到android系统库里面
        Uri mediaUri = BitmapUtils.displayToGallery(getApplicationContext(), newFile, TYPE_VIDEO, mDuration, mVideoMediaStoreCompat.getSaveStrategy().directory, mVideoMediaStoreCompat);
        intent.putExtra("path", newFile.getPath());
        intent.putExtra("uri", mediaUri);
        setResult(RESULT_OK, intent);
        PreviewVideoActivity.this.finish();
    }

}
