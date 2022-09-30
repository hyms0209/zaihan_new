package com.android.customcameraalbum.camera;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.gowtham.library.utils.CompressOption;
import com.gowtham.library.utils.TrimVideo;
import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.controls.Flash;
import com.android.customcameraalbum.R;
import com.android.customcameraalbum.camera.common.Constants;
import com.android.customcameraalbum.camera.entity.BitmapData;
import com.android.customcameraalbum.camera.listener.CaptureListener;
import com.android.customcameraalbum.camera.listener.ClickOrLongListener;
import com.android.customcameraalbum.camera.listener.CloseListener;
import com.android.customcameraalbum.camera.listener.EditListener;
import com.android.customcameraalbum.camera.listener.ErrorListener;
import com.android.customcameraalbum.camera.listener.OperateCameraListener;
import com.android.customcameraalbum.camera.util.FileUtil;
import com.android.customcameraalbum.camera.util.LogUtil;
import com.android.customcameraalbum.camera.widget.PhotoVideoLayoutBase;
import com.android.customcameraalbum.preview.AlbumPreviewActivity;
import com.android.customcameraalbum.preview.BasePreviewActivity;
import com.android.customcameraalbum.settings.CameraSpec;
import com.android.customcameraalbum.settings.GlobalSpec;
import com.android.customcameraalbum.utils.BitmapUtils;
import com.android.customcameraalbum.utils.PackageManagerUtils;
import com.android.customcameraalbum.utils.SelectableUtils;
import com.android.customcameraalbum.widget.BaseOperationLayout;
import com.android.customcameraalbum.widget.ChildClickableFrameLayout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import com.android.customcameraalbum.common.entity.MultiMedia;
import com.android.customcameraalbum.common.enums.MimeType;
import com.android.customcameraalbum.common.enums.MultimediaTypes;
import com.android.customcameraalbum.common.listener.VideoEditListener;
import com.android.customcameraalbum.common.utils.MediaStoreCompat;
import com.android.customcameraalbum.utils.StatusBarUtils;
import com.android.customcameraalbum.common.utils.ThreadUtils;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;

import static com.android.customcameraalbum.album.model.SelectedItemCollection.COLLECTION_IMAGE;
import static com.android.customcameraalbum.album.model.SelectedItemCollection.STATE_COLLECTION_TYPE;
import static com.android.customcameraalbum.album.model.SelectedItemCollection.STATE_SELECTION;
import static com.android.customcameraalbum.camera.common.Constants.BUTTON_STATE_BOTH;
import static com.android.customcameraalbum.camera.common.Constants.BUTTON_STATE_ONLY_CLICK;
import static com.android.customcameraalbum.camera.common.Constants.BUTTON_STATE_ONLY_LONG_CLICK;
import static com.android.customcameraalbum.camera.common.Constants.STATE_VIDEO;
import static com.android.customcameraalbum.camera.common.Constants.TYPE_DEFAULT;
import static com.android.customcameraalbum.camera.common.Constants.TYPE_PICTURE;
import static com.android.customcameraalbum.camera.common.Constants.TYPE_SHORT;
import static com.android.customcameraalbum.camera.common.Constants.TYPE_VIDEO;
import static com.android.customcameraalbum.constants.Constant.EXTRA_MULTIMEDIA_CHOICE;
import static com.android.customcameraalbum.constants.Constant.EXTRA_MULTIMEDIA_TYPES;
import static com.android.customcameraalbum.constants.Constant.EXTRA_RESULT_SELECTION;
import static com.android.customcameraalbum.constants.Constant.EXTRA_RESULT_SELECTION_PATH;
import static com.android.customcameraalbum.constants.Constant.REQUEST_CODE_PREVIEW_CAMRRA;

/**
 * @author zhongjh
 * @date 2018/7/23.
 * 一个全局界面，包含了 右上角的闪光灯、前/后置摄像头的切换、底部按钮功能、对焦框等、显示当前拍照和摄像的界面
 * 该类类似MVP的View，主要包含有关 除了Camera的其他所有ui操作
 * <p>
 * 录制逻辑：
 * 拍摄/录制 文件后，会先缓存到Cache文件夹，当点击完成后，才将相关确认的文件复制到配置的路径下，加入相册库，并且清空Cache文件夹
 */
public class CameraLayout extends RelativeLayout {

    private final String TAG = CameraLayout.class.getSimpleName();
    private final static int PROGRESS_MAX = 100;

    private final Context mContext;
    /**
     * 图片
     */
    private MediaStoreCompat mPictureMediaStoreCompat;
    /**
     * 录像文件配置路径
     */
    private MediaStoreCompat mVideoMediaStoreCompat;
    /**
     * 公共配置
     */
    private GlobalSpec mGlobalSpec;
    /**
     * 拍摄配置
     */
    private CameraSpec mCameraSpec;

    /**
     * 当前活动状态，默认休闲
     */
    public int mState = Constants.STATE_PREVIEW;

    /**
     * 闪关灯状态 默认关闭
     */
    private int mFlashType = Constants.TYPE_FLASH_OFF;

    /**
     * 当前界面的所有控件
     */
    public ViewHolder mViewHolder;

    /**
     * 默认图片
     */
    private Drawable mPlaceholder;
    /**
     * 拍照的图片-集合
     */
    public LinkedHashMap<Integer, BitmapData> mCaptureBitmaps = new LinkedHashMap<>();
    /**
     * 拍照的图片控件-集合
     */
    private final LinkedHashMap<Integer, View> mCaptureViews = new LinkedHashMap<>();
    /**
     * 数据目前的最长索引，上面两个集合都是根据这个索引进行删除增加。这个索引只有递增没有递减
     */
    private int mPosition = -1;
    /**
     * 拷贝文件是否拷贝完
     */
    private int currentCount = 0;
    /**
     * 视频File,用于后面能随时删除
     */
    private File mVideoFile;
    /**
     * 照片File,用于后面能随时删除
     */
    private File mPhotoFile;
    /**
     * 编辑后的照片
     */
    private File mPhotoEditFile;
    /**
     * 是否短时间录制
     */
    private boolean mIsShort;

    /**
     * 是否分段录制
     */
    private boolean mIsSectionRecord;
    /**
     * 处于分段录制模式下的视频的文件列表
     */
    private final ArrayList<String> mVideoPaths = new ArrayList<>();
    /**
     * 处于分段录制模式下的视频的时间列表
     */
    private final ArrayList<Long> mVideoTimes = new ArrayList<>();
    /**
     * 处于分段录制模式下合成的新的视频
     */
    private String mNewSectionVideoPath;
    /**
     * 用于延迟隐藏的事件，如果不用延迟，会有短暂闪屏现象
     */
    private final Handler mCameraViewGoneHandler = new Handler(Looper.getMainLooper());
    /**
     * 用于延迟显示的事件，如果不用延迟，会有短暂闪屏现象
     */
    private final Handler mCameraViewVisibleHandler = new Handler(Looper.getMainLooper());
    private final Runnable mCameraViewGoneRunnable = new Runnable() {
        @Override
        public void run() {
            // 隐藏cameraView
            mViewHolder.cameraView.close();
        }
    };
    private final Runnable mCameraViewVisibleRunnable = new Runnable() {
        @Override
        public void run() {
            mViewHolder.cameraView.open();
        }
    };

    // region 回调监听属性

    private ErrorListener mErrorListener;
    /**
     * 退出当前Activity的按钮监听
     */
    private CloseListener mCloseListener;
    /**
     * 编辑当前图片的监听
     */
    private EditListener mEditListener;
    /**
     * 按钮的监听
     */
    private ClickOrLongListener mClickOrLongListener;
    /**
     * 确认跟返回的监听
     */
    private OperateCameraListener mOperateCameraListener;
    /**
     * 拍摄后操作图片的事件
     */
    private CaptureListener mCaptureListener;
    private CameraFragment fragment;

    // 赋值Camera错误回调

    public void setErrorListener(ErrorListener errorListener) {
        this.mErrorListener = errorListener;
    }

    /**
     * 退出当前Activity的按钮监听
     *
     * @param closeListener 事件
     */
    public void setCloseListener(CloseListener closeListener) {
        this.mCloseListener = closeListener;
    }

    /**
     * 核心按钮事件
     *
     * @param clickOrLongListener 事件
     */
    public void setPhotoVideoListener(ClickOrLongListener clickOrLongListener) {
        this.mClickOrLongListener = clickOrLongListener;
    }

    /**
     * 确认跟返回的监听
     *
     * @param operateCameraListener 事件
     */
    public void setOperateCameraListener(OperateCameraListener operateCameraListener) {
        this.mOperateCameraListener = operateCameraListener;
    }

    /**
     * 拍摄后操作图片的事件
     *
     * @param captureListener 事件
     */
    public void setCaptureListener(CaptureListener captureListener) {
        this.mCaptureListener = captureListener;
    }

    /**
     * 编辑图片的回调
     *
     * @param editListener 事件
     */
    public void setEditListener(EditListener editListener) {
        this.mEditListener = editListener;
    }

    // endregion

    public CameraLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initData();
        initView();
        initListener();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 初始化设置
        mCameraSpec = CameraSpec.getInstance();
        mGlobalSpec = GlobalSpec.getInstance();
        mPictureMediaStoreCompat = new MediaStoreCompat(getContext());
        // 设置图片路径
        if (mGlobalSpec.pictureStrategy != null) {
            // 如果设置了视频的文件夹路径，就使用它的
            mPictureMediaStoreCompat.setSaveStrategy(mGlobalSpec.pictureStrategy);
        } else {
            // 否则使用全局的
            if (mGlobalSpec.saveStrategy == null) {
                throw new RuntimeException("Don't forget to set SaveStrategy.");
            } else {
                mPictureMediaStoreCompat.setSaveStrategy(mGlobalSpec.saveStrategy);
            }
        }
        mVideoMediaStoreCompat = new MediaStoreCompat(getContext());
        mVideoMediaStoreCompat.setSaveStrategy(mGlobalSpec.videoStrategy == null ? mGlobalSpec.saveStrategy : mGlobalSpec.videoStrategy);

        // 默认图片
        TypedArray ta = mContext.getTheme().obtainStyledAttributes(
                new int[]{R.attr.album_thumbnail_placeholder});
        mPlaceholder = ta.getDrawable(0);

        mIsSectionRecord = mCameraSpec.isSectionRecord;

    }

    /**
     * 初始化view
     */
    private void initView() {
        // 自定义View中如果重写了onDraw()即自定义了绘制，那么就应该在构造函数中调用view的setWillNotDraw(false).
        setWillNotDraw(false);
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_camera_main_view_zjh, this);
        mViewHolder = new ViewHolder(view);

        if (mCameraSpec.watermarkResource != -1) {
            LayoutInflater.from(mContext).inflate(mCameraSpec.watermarkResource, mViewHolder.cameraView, true);
        }

        // 回调cameraView可以自定义相关参数
        if (mCameraSpec.onCameraViewListener != null) {
            mCameraSpec.onCameraViewListener.onInitListener(mViewHolder.cameraView);
        }

        // 兼容沉倾状态栏
        int statusBarHeight = StatusBarUtils.getStatusBarHeight(mContext);
        mViewHolder.clMenu.setPadding(0, statusBarHeight, 0, 0);
        ViewGroup.LayoutParams layoutParams = mViewHolder.clMenu.getLayoutParams();
        layoutParams.height = layoutParams.height + statusBarHeight;

        // 如果没启动视频编辑，隐藏分段录制功能
        if (mCameraSpec.videoEditCoordinator == null) {
            mViewHolder.pvLayout.getViewHolder().tvSectionRecord.setVisibility(View.GONE);
        }

        // 初始化cameraView

        setFlashLamp(); // 设置闪光灯模式
        // 设置录制时间
        mViewHolder.pvLayout.setDuration(mCameraSpec.duration * 1000);
        // 最短录制时间
        mViewHolder.pvLayout.setMinDuration(mCameraSpec.minDuration);

        // 判断点击和长按的权限
        if (mCameraSpec.onlySupportImages()) {
            String tipMsg = getResources().getString(R.string.z_multi_library_light_touch_take);
            // 禁用长按功能
            mViewHolder.pvLayout.setButtonFeatures(BUTTON_STATE_ONLY_CLICK);
            mViewHolder.pvLayout.setTip(tipMsg);
        } else if (mCameraSpec.onlySupportVideos()) {
            // 禁用点击功能
            String tipMsg = getResources().getString(R.string.z_multi_library_long_press_camera);
            mViewHolder.pvLayout.setButtonFeatures(BUTTON_STATE_ONLY_LONG_CLICK);
            mViewHolder.pvLayout.setTip(tipMsg);
        } else {
            // 支持所有，不过要判断数量
            if (SelectableUtils.getImageMaxCount() == 0) {
                // 禁用点击功能
                String tipMsg = getResources().getString(R.string.z_multi_library_long_press_camera);
                mViewHolder.pvLayout.setTip(tipMsg);
                mViewHolder.pvLayout.setButtonFeatures(BUTTON_STATE_ONLY_LONG_CLICK);
            } else if (SelectableUtils.getVideoMaxCount() == 0) {
                // 禁用长按功能
                String tipMsg = getResources().getString(R.string.z_multi_library_light_touch_take);
                mViewHolder.pvLayout.setButtonFeatures(BUTTON_STATE_ONLY_CLICK);
                mViewHolder.pvLayout.setTip(tipMsg);
            } else {
                String tipMsg = getResources().getString(R.string.z_multi_library_light_touch_take_long_press_camera);
                mViewHolder.pvLayout.setButtonFeatures(BUTTON_STATE_BOTH);
                mViewHolder.pvLayout.setTip(tipMsg);
            }
        }
    }

    /**
     * 初始化有关事件
     */
    private void initListener() {
        // 切换闪光灯模式
        initImgFlashListener();

        // 切换摄像头前置/后置
        initImgSwitchListener();

        // 主按钮监听
        initPvLayoutPhotoVideoListener();

        // 左右确认和取消
        initPvLayoutOperateListener();

        // 视频编辑后的事件，目前只有分段录制后合并
        initVideoEditListener();

        // 拍照监听
        initCameraViewListener();

        // 关闭事件
        initImgCloseListener();

        // 编辑图片事件
        initPhotoEditListener();

    }

    /**
     * 生命周期onResume
     */
    public void onResume() {
        LogUtil.i("CameraLayout onResume");
        // 重置状态
        resetState(TYPE_DEFAULT);
        mViewHolder.cameraView.open();
    }

    /**
     * 生命周期onPause
     */
    public void onPause() {
        LogUtil.i("CameraLayout onPause");
        mViewHolder.cameraView.close();
    }

    /**
     * 生命周期onDestroy
     *
     * @param isCommit 是否提交了数据,如果不是提交则要删除冗余文件
     */
    protected void onDestroy(boolean isCommit) {
        LogUtil.i("CameraLayout destroy");
        if (!isCommit) {
            if (mPhotoFile != null) {
                // 删除图片
                FileUtil.deleteFile(mPhotoFile);
            }
            if (mVideoFile != null) {
                // 删除视频
                FileUtil.deleteFile(mVideoFile);
            }
            // 删除多个视频
            for (String item : mVideoPaths) {
                FileUtil.deleteFile(item);
            }
            // 删除多个图片
            if (mCaptureBitmaps != null) {
                for (Map.Entry<Integer, BitmapData> entry : mCaptureBitmaps.entrySet()) {
                    FileUtil.deleteFile(Objects.requireNonNull(mCaptureBitmaps.get(entry.getKey())).getPath());
                }
            }
            // 新合成视频删除
            if (mNewSectionVideoPath != null) {
                FileUtil.deleteFile(mNewSectionVideoPath);
            }
        } else {
            // 如果是提交的，删除合成前的视频
            for (String item : mVideoPaths) {
                FileUtil.deleteFile(item);
            }
        }
        mViewHolder.cameraView.destroy();
        if (mCameraSpec.videoEditCoordinator != null) {
            mCameraSpec.videoEditCoordinator.onDestroy();
        }
        mCameraViewGoneHandler.removeCallbacks(mCameraViewGoneRunnable);
        mCameraViewVisibleHandler.removeCallbacks(mCameraViewVisibleRunnable);
    }

    /**
     * 切换闪光灯模式
     */
    private void initImgFlashListener() {
        Log.d("A.lee", "mCameraSpec.useImgFlash" + mCameraSpec.useImgFlash);
        if (!mCameraSpec.useImgFlash) {
            mViewHolder.imgFlash.setVisibility(INVISIBLE);
            return;
        }

        mViewHolder.imgFlash.setOnClickListener(v -> {
            mFlashType++;
            if (mFlashType > Constants.TYPE_FLASH_OFF) {
                mFlashType = Constants.TYPE_FLASH_AUTO;
            }
            // 重新设置当前闪光灯模式
            setFlashLamp();
        });
    }

    /**
     * 切换摄像头前置/后置
     */
    private void initImgSwitchListener() {
        mViewHolder.imgSwitch.setOnClickListener(v -> mViewHolder.cameraView.toggleFacing());
    }

    /**
     * 主按钮监听
     */
    private void initPvLayoutPhotoVideoListener() {
        mViewHolder.pvLayout.setPhotoVideoListener(new ClickOrLongListener() {
            @Override
            public void actionDown() {
                if (mClickOrLongListener != null) {
                    mClickOrLongListener.actionDown();
                }
            }

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick() {
                if (mState == STATE_VIDEO) {
                    return;
                }

                // 开启才能执行别的事件
                if (mViewHolder.cameraView.isOpened()) {
                    // 判断数量
                    if (mViewHolder.llPhoto.getChildCount() < currentMaxSelectable()) {
                        // 拍照  隐藏 闪光灯、右上角的切换摄像头
                        setSwitchVisibility(INVISIBLE);
                        mViewHolder.imgFlash.setVisibility(INVISIBLE);
                        // 设置不能点击，防止多次点击报错
                        mViewHolder.rlMain.setChildClickable(false);
                        mViewHolder.cameraView.takePictureSnapshot();
                        if (mClickOrLongListener != null) {
                            mClickOrLongListener.onClick();
                        }
                    } else {
                        Toast.makeText(mContext, mContext.getResources().getString(
                                R.string.z_multi_library_error_over_count_image,
                                currentMaxSelectable()
                        ), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onLongClickShort(final long time) {
                Log.d(TAG, "onLongClickShort " + time);
                mViewHolder.pvLayout.setTipAlphaAnimation(getResources().getString(R.string.z_multi_library_the_recording_time_is_too_short));  // 提示过短
                setSwitchVisibility(VISIBLE);
                mViewHolder.imgFlash.setVisibility(mCameraSpec.useImgFlash ? VISIBLE : INVISIBLE);
                postDelayed(() -> stopRecord(true), mCameraSpec.minDuration - time);
                if (mClickOrLongListener != null) {
                    mClickOrLongListener.onLongClickShort(time);
                }
            }

            @Override
            public void onLongClick() {
                // 开启才能执行别的事件
                if (mViewHolder.cameraView.isOpened()) {
                    // 用于播放的视频file
                    if (mVideoFile == null) {
                        mVideoFile = mVideoMediaStoreCompat.createFile(1, true);
                    }
                    mViewHolder.cameraView.takeVideoSnapshot(mVideoFile);
                    // 开始录像
                    setSwitchVisibility(INVISIBLE);
                    mViewHolder.imgFlash.setVisibility(INVISIBLE);
                    if (mClickOrLongListener != null) {
                        mClickOrLongListener.onLongClick();
                    }
                }
            }

            @Override
            public void onLongClickEnd(long time) {
                Log.d(TAG, "onLongClickEnd " + time);
                // 录像结束
                stopRecord(false);
                // 判断模式
                if (mIsSectionRecord) {
                    mVideoTimes.add(time);
                    mViewHolder.pvLayout.setData(mVideoTimes);
                    // 如果已经有录像缓存，那么就不执行这个动作了
                    if (mVideoPaths.size() <= 0) {
                        mViewHolder.pvLayout.startShowLeftRightButtonsAnimator();
                        mViewHolder.pvLayout.getViewHolder().tvSectionRecord.setVisibility(View.GONE);
                    }
                }
                if (mClickOrLongListener != null) {
                    mClickOrLongListener.onLongClickEnd(time);
                }
            }

            @Override
            public void onLongClickError() {
                if (mErrorListener != null) {
                    mErrorListener.onAudioPermissionError();
                }
                if (mClickOrLongListener != null) {
                    mClickOrLongListener.onLongClickError();
                }
            }
        });
    }

    /**
     * 左右确认和取消
     */
    private void initPvLayoutOperateListener() {
        mViewHolder.pvLayout.setOperateListener(new BaseOperationLayout.OperateListener() {
            @Override
            public void cancel() {
                pvLayoutCancel();
            }

            @Override
            public void confirm() {
                if (mState == STATE_VIDEO) {
                    File newSectionVideo = mVideoMediaStoreCompat.createFile(1, true);
                    mNewSectionVideoPath = newSectionVideo.getPath();

                    mCameraSpec.videoEditCoordinator.setVideoMergeListener(new VideoEditListener() {
                        @Override
                        public void onFinish() {
                            fragment.confirmVideo(Uri.fromFile(newSectionVideo));
                        }

                        @Override
                        public void onProgress(int progress, long progressTime) {
                        }

                        @Override
                        public void onCancel() {
                            Log.d(TAG, "onCancel");
                        }

                        @Override
                        public void onError(String message) {
                            Log.d(TAG, "onError" + message);
                        }
                    });

                    mCameraSpec.videoEditCoordinator.merge(mNewSectionVideoPath, mVideoPaths,
                            mContext.getCacheDir().getPath() + File.separator + "cam.txt");
//                    fragment.confirmVideo(newSectionVideo);


                } else {
                    ArrayList<BitmapData> bitmapDatas = new ArrayList<BitmapData>();
                    for (Map.Entry<Integer, BitmapData> entry : mCaptureBitmaps.entrySet()) {
                        bitmapDatas.add(mCaptureBitmaps.get(entry.getKey()));
                    }

                    fragment.confirmPicture(bitmapDatas);
                }
            }

            @Override
            public void preview() {
                File newSectionVideo = mVideoMediaStoreCompat.createFile(1, true);
                mNewSectionVideoPath = newSectionVideo.getPath();
                mCameraSpec.videoEditCoordinator.setVideoMergeListener(new VideoEditListener() {
                    @Override
                    public void onFinish() {
                        fragment.goVideoTrim(Uri.fromFile(newSectionVideo));
                    }

                    @Override
                    public void onProgress(int progress, long progressTime) {
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "onCancel");
                    }

                    @Override
                    public void onError(String message) {
                        Log.d(TAG, "onError" + message);
                    }
                });
                mCameraSpec.videoEditCoordinator.merge(mNewSectionVideoPath, mVideoPaths,
                        mContext.getCacheDir().getPath() + File.separator + "cam.txt");

            }

        });
    }


    /**
     * 视频编辑后的事件，目前 有分段录制后合并、压缩视频
     */
    private void initVideoEditListener() {
//        if (mCameraSpec.videoEditCoordinator != null) {
//            mCameraSpec.videoEditCoordinator.setVideoMergeListener(new VideoEditListener() {
//                @Override
//                public void onFinish() {
//                }
//
//                @Override
//                public void onProgress(int progress, long progressTime) {
//                }
//
//                @Override
//                public void onCancel() {
//                    Log.d(TAG, "onCancel");
//                }
//
//                @Override
//                public void onError(String message) {
//                    Log.d(TAG, "onError" + message);
//                }
//            });
//        }
    }

    /**
     * 拍照监听
     */
    private void initCameraViewListener() {
        mViewHolder.cameraView.addCameraListener(new CameraListener() {

            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                result.toBitmap(bitmap -> {
                    // 显示图片
                    showPicture(bitmap);
                    // 恢复点击
                    mViewHolder.rlMain.setChildClickable(true);
                });

                super.onPictureTaken(result);
            }

            @Override
            public void onVideoTaken(@NonNull VideoResult result) {
                super.onVideoTaken(result);

                // 判断是否短时间结束
                if (!mIsShort) {
                    Log.d(TAG, "onVideoTaken " + result.getFile().getPath());
                    // 加入视频列表
                    mVideoPaths.add(result.getFile().getPath());
                    // 显示当前进度
                    mViewHolder.pvLayout.setData(mVideoTimes);
                    // 创建新的file
                    mVideoFile = mVideoMediaStoreCompat.createFile(1, true);
                } else {
                    Log.d(TAG, "onVideoTaken delete " + mVideoFile.getPath());
                    FileUtil.deleteFile(mVideoFile);
                    mIsShort = false;
                }
            }

            @Override
            public void onVideoRecordingStart() {
                Log.d(TAG, "onVideoRecordingStart");
                super.onVideoRecordingStart();
            }

            @Override
            public void onCameraError(@NonNull CameraException exception) {
                super.onCameraError(exception);
                if (!TextUtils.isEmpty(exception.getMessage())) {
                    Log.d(TAG, exception.getMessage() + " " + exception.getReason());
                    mErrorListener.onError();
                }
            }

        });
    }

    /**
     * 关闭事件
     */
    private void initImgCloseListener() {
        mViewHolder.btnCancel.setOnClickListener(v -> {
            if (mCloseListener != null) {
                mCloseListener.onClose();
            }
        });
    }

    /**
     * 编辑图片事件
     */
    private void initPhotoEditListener() {
        mViewHolder.rlEdit.setOnClickListener(view -> {
            Uri uri = (Uri) view.getTag();
            mPhotoEditFile = mPictureMediaStoreCompat.createFile(0, true);
            if (mEditListener != null) {
                mEditListener.onImageEdit(uri, mPhotoEditFile.getAbsolutePath());
            }
        });
    }

    /**
     * 进行删除
     *
     * @param position 索引
     */
    public void removePosition(int position) {
        // 删除文件
        FileUtil.deleteFile(Objects.requireNonNull(mCaptureBitmaps.get(position)).getPath());

        // 删除数据
        mCaptureBitmaps.remove(position);
        mViewHolder.llPhoto.removeView(mCaptureViews.get(position));

        // 回调接口：删除图片后剩下的相关数据
        mCaptureListener.remove(mCaptureBitmaps);

        // 当列表全部删掉的话，就隐藏
        if (mCaptureBitmaps.size() <= 0) {
            // 隐藏横版列表
            mViewHolder.hsvPhoto.setVisibility(View.GONE);

            // 隐藏横版列表的线条空间
            mViewHolder.vLine1.setVisibility(View.GONE);
            mViewHolder.vLine2.setVisibility(View.GONE);

            // 隐藏右侧按钮
            mViewHolder.pvLayout.getViewHolder().btnConfirm.setVisibility(View.GONE);

            // 恢复长按事件，即重新启用录像
//            mViewHolder.pvLayout.getViewHolder().btnClickOrLong.setButtonFeatures(BUTTON_STATE_BOTH);

            // 设置空闲状态
            setState(Constants.STATE_PREVIEW);
        }
    }

    /**
     * 刷新多个图片
     */
    public void refreshMultiPhoto(ArrayList<MultiMedia> multiMediaArrayList) {
        int position = 0;
        for (Map.Entry<Integer, BitmapData> entry : mCaptureBitmaps.entrySet()) {
            ImageView imgPhoto = Objects.requireNonNull(mCaptureViews.get(entry.getKey())).findViewById(R.id.imgPhoto);
            Objects.requireNonNull(mCaptureBitmaps.get(entry.getKey())).setUri(multiMediaArrayList.get(position).getUri());
            Objects.requireNonNull(mCaptureBitmaps.get(entry.getKey())).setPath(multiMediaArrayList.get(position).getPath());
            mGlobalSpec.imageEngine.loadUriImage(getContext(), imgPhoto, Objects.requireNonNull(mCaptureBitmaps.get(entry.getKey())).getUri());
            position++;
        }
    }

    /**
     * 刷新编辑后的图片
     */
    public void refreshEditPhoto() {
        // 删除旧图
        if (mPhotoFile.exists()) {
            boolean wasSuccessful = mPhotoFile.delete();
            if (!wasSuccessful) {
                System.out.println("was not successful.");
            }
        }
        // 用编辑后的图作为新的图片
        mPhotoFile = mPhotoEditFile;
        Uri uri = mPictureMediaStoreCompat.getUri(mPhotoFile.getPath());

        // 重置mCaptureBitmaps
        mCaptureBitmaps.clear();
        BitmapData bitmapData = new BitmapData(mPhotoFile.getPath(), uri);
        mCaptureBitmaps.put(0, bitmapData);

        mViewHolder.imgPhoto.canScroll();
        mGlobalSpec.imageEngine.loadUriImage(getContext(), mViewHolder.imgPhoto, uri);
        mViewHolder.rlEdit.setTag(uri);
    }

    /**
     * 取消核心事件
     */
    private synchronized void pvLayoutCancel() {
        // 判断是不是分段录制并且超过1个视频
        if (mIsSectionRecord && mVideoPaths.size() >= 1) {
            if (mNewSectionVideoPath != null) {
                FileUtil.deleteFile(mNewSectionVideoPath);
            }
            // 删除最后一个视频和视频文件
            FileUtil.deleteFile(mVideoPaths.get(mVideoPaths.size() - 1));
            mVideoPaths.remove(mVideoPaths.size() - 1);
            mVideoTimes.remove(mVideoTimes.size() - 1);

            // 显示当前进度
            mViewHolder.pvLayout.setData(mVideoTimes);
            mViewHolder.pvLayout.invalidateClickOrLongButton();
            if (mVideoPaths.size() == 0) {
                cancelOnReset();
            }
        } else {
            cancelOnReset();
        }
    }

    /**
     * 取消后的重置相关
     */
    private void cancelOnReset() {
        if (mCameraSpec.videoEditCoordinator != null) {
            mViewHolder.pvLayout.getViewHolder().tvSectionRecord.setVisibility(View.VISIBLE);
        }

        // 根据不同状态处理相应的事件,多图不需要取消事件（关闭所有图片就自动恢复了）。
        if (getState() == Constants.STATE_PICTURE) {
            // 针对图片模式进行的重置
            resetState(TYPE_PICTURE);
            mViewHolder.pvLayout.reset();
            // 设置空闲状态
            setState(Constants.STATE_PREVIEW);
        } else if (getState() == Constants.STATE_VIDEO) {
            // 针对arm64-v8a视频模式进行的重置
            resetState(TYPE_VIDEO);
            mViewHolder.pvLayout.reset();
            // 设置空闲状态
            setState(Constants.STATE_PREVIEW);
        }
        if (mOperateCameraListener != null) {
            mOperateCameraListener.cancel();
        }

        mViewHolder.rlEdit.setVisibility(View.GONE);
    }

    /**
     * 针对当前状态重新设置状态
     *
     * @param type 类型
     */
    private void resetState(int type) {
        switch (type) {
            case TYPE_VIDEO:
                // 取消视频删除文件
                FileUtil.deleteFile(mVideoFile);
                break;
            case TYPE_PICTURE:
                // 隐藏图片view
                mViewHolder.cameraView.open();
                mViewHolder.imgPhoto.setVisibility(INVISIBLE);
                mViewHolder.flShow.setVisibility(INVISIBLE);
                if (mPhotoFile != null) {
                    // 删除图片
                    FileUtil.deleteFile(mPhotoFile);
                }
                mViewHolder.pvLayout.getViewHolder().btnClickOrLong.setVisibility(View.VISIBLE);
                break;
            case TYPE_SHORT:
                // 短视屏停止录像并删除文件
                mIsShort = true;
                mViewHolder.cameraView.stopVideo();
                break;
            case TYPE_DEFAULT:
            default:
                break;
        }
        setSwitchVisibility(VISIBLE);
        mViewHolder.imgFlash.setVisibility(mCameraSpec.useImgFlash ? VISIBLE : INVISIBLE);
    }

//    private void confirmState(int type) {
//        switch (type) {
//            case TYPE_VIDEO:
//                // TODO 弃用，已经改用跳转到第二个界面播放视频了
//                break;
//            case TYPE_PICTURE:
//                // 拍照完成
//                if (mOperateCameraListener != null) {
//                    // 移动文件
//                    movePictureFile();
//                }
//                break;
//            case TYPE_SHORT:
//            case TYPE_DEFAULT:
//            default:
//                break;
//        }
//    }
//
//    /**
//     * 迁移图片文件，缓存文件迁移到配置目录
//     */
//    private void movePictureFile() {
////        //  开始迁移文件
////        ThreadUtils.executeByIo(new ThreadUtils.BaseSimpleBaseTask<Void>() {
////            @Override
////            public Void doInBackground() {
////                ArrayList<String> paths = getPaths();
////                ArrayList<String> newPaths = new ArrayList<>();
////                // 总长度
////                int maxCount = paths.size();
////                // 计算每个文件的进度Progress
////                int progress = 100 / maxCount;
////                // 将 缓存文件 拷贝到 配置目录
////                for (String item : paths) {
////                    String newFileName = item.substring(item.lastIndexOf(File.separator));
////                    File newFile = mPictureMediaStoreCompat.createFile(newFileName, 0, false);
////                    Log.d(TAG, "newFile" + newFile.getAbsolutePath());
////                    newPaths.add(newFile.getAbsolutePath());
////                    ThreadUtils.runOnUiThread(() -> {
////                        // 是否拷贝完所有文件
////                        currentCount++;
////                        if (currentCount >= maxCount) {
////                            currentCount = 0;
////                            // 拷贝完毕，进行加入相册库等操作
////                            ArrayList<Uri> uris = getUris(newPaths);
////                            // 加入图片到android系统库里面
////                            for (String path : newPaths) {
////                                BitmapUtils.displayToGallery(getContext(), new File(path), TYPE_PICTURE, -1, mPictureMediaStoreCompat.getSaveStrategy().directory, mPictureMediaStoreCompat);
////                            }
////                            // 执行完成
////                            mOperateCameraListener.captureSuccess(newPaths, uris);
////                        }
////                    });
////                }
////                return null;
////            }
////
////            @Override
////            public void onSuccess(Void result) {
////
////            }
////
////            @Override
////            public void onFail(Throwable t) {
////                super.onFail(t);
////                ThreadUtils.runOnUiThread(() -> Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show());
////            }
////        });
//        ArrayList<String> arrayList = new ArrayList<>();
//        arrayList.add(newFile.getPath());
//        ArrayList<Uri> arrayListUri = new ArrayList<>();
//        arrayListUri.add(Uri.fromFile(newFile));
//        // 获取视频路径
//        Intent result = new Intent();
//        result.putStringArrayListExtra(EXTRA_RESULT_SELECTION_PATH, arrayList);
//        result.putParcelableArrayListExtra(EXTRA_RESULT_SELECTION, arrayListUri);
//        result.putExtra(EXTRA_MULTIMEDIA_TYPES, MultimediaTypes.VIDEO);
//        result.putExtra(EXTRA_MULTIMEDIA_CHOICE, false);
//        mActivity.setResult(RESULT_OK, result);
//        mActivity.finish();
//    }

    /**
     * 显示图片 单个或者多个
     *
     * @param bitmap bitmap
     */
    private void showPicture(Bitmap bitmap) {
        // 初始化数据并且存储进file
        File file = mPictureMediaStoreCompat.saveFileByBitmap(bitmap, false);
        BitmapData bitmapData = new BitmapData(file.getPath(), Uri.fromFile(file));
        // 回收bitmap
        if (bitmap.isRecycled()) {
            // 回收并且置为null
            bitmap.recycle();
        }
        System.gc();// 加速回收机制

        // 判断是否多个图片
        if (SelectableUtils.getImageMaxCount() > 1) {
            addMultiplePicture(bitmapData);
        } else {
            // 如果只有单个图片，就显示相应的提示结果等等
            mCaptureBitmaps.put(0, bitmapData);
            mViewHolder.imgPhoto.canScroll();
            mViewHolder.imgPhoto.setVisibility(VISIBLE);
            mGlobalSpec.imageEngine.loadUriImage(getContext(), mViewHolder.imgPhoto, bitmapData.getUri());
            mViewHolder.cameraView.close();
            mViewHolder.flShow.setVisibility(VISIBLE);
            mViewHolder.pvLayout.startTipAlphaAnimation();
            mViewHolder.pvLayout.startShowLeftRightButtonsAnimator();
            mPhotoFile = file;

            // 设置当前模式是图片模式
            setState(Constants.STATE_PICTURE);

            // 判断是否要编辑
            if (mGlobalSpec.isImageEdit) {
                mViewHolder.rlEdit.setVisibility(View.VISIBLE);
                mViewHolder.rlEdit.setTag(Uri.fromFile(file));
            } else {
                mViewHolder.rlEdit.setVisibility(View.INVISIBLE);
            }

            // 隐藏拍照按钮
            mViewHolder.pvLayout.getViewHolder().btnClickOrLong.setVisibility(View.INVISIBLE);
        }

        // 回调接口：添加图片后剩下的相关数据
        mCaptureListener.add(mCaptureBitmaps);
    }

    /**
     * 如果是多个图片则添加集合并显示
     *
     * @param bitmapData 图片数据源
     */
    private void addMultiplePicture(BitmapData bitmapData) {
        mPosition++;
        // 如果是多个图片，就把当前图片添加到集合并显示出来
        mCaptureBitmaps.put(mPosition, bitmapData);
        // 显示横版列表
        mViewHolder.hsvPhoto.setVisibility(View.VISIBLE);

        // 显示横版列表的线条空间
        mViewHolder.vLine1.setVisibility(View.VISIBLE);
        mViewHolder.vLine2.setVisibility(View.VISIBLE);

        // 添加view
        ViewHolderImageView viewHolderImageView = new ViewHolderImageView(View.inflate(getContext(), R.layout.item_horizontal_image_zjh, null));
        mGlobalSpec.imageEngine.loadUriImage(getContext(), viewHolderImageView.imgPhoto, bitmapData.getUri());
        // 删除事件
        viewHolderImageView.imgCancel.setTag(R.id.tagid, mPosition);
        viewHolderImageView.imgCancel.setOnClickListener(v -> removePosition(Integer.parseInt(v.getTag(R.id.tagid).toString())));

        // 打开显示大图
        viewHolderImageView.imgPhoto.setTag(R.id.tagid, String.valueOf(mPosition));
        viewHolderImageView.imgPhoto.setOnClickListener(v -> {
            ArrayList<MultiMedia> items = new ArrayList<>();
            for (Map.Entry<Integer, BitmapData> entry : mCaptureBitmaps.entrySet()) {
                MultiMedia item = new MultiMedia();
                item.setUri(entry.getValue().getUri());
                item.setPath(entry.getValue().getPath());
                item.setType(MultimediaTypes.PICTURE);
                item.setMimeType(MimeType.JPEG.toString());
                item.setPosition(entry.getKey());
                items.add(item);
            }
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(STATE_SELECTION, items);
            bundle.putInt(STATE_COLLECTION_TYPE, COLLECTION_IMAGE);

            Intent intent = new Intent(mContext, AlbumPreviewActivity.class);

            // 获取目前点击的这个item
            MultiMedia item = new MultiMedia();
            item.setUri(Objects.requireNonNull(mCaptureBitmaps.get(Integer.parseInt(String.valueOf(v.getTag(R.id.tagid))))).getUri());
            item.setPath(Objects.requireNonNull(mCaptureBitmaps.get(Integer.parseInt(String.valueOf(v.getTag(R.id.tagid))))).getPath());
            item.setType(MultimediaTypes.PICTURE);
            item.setMimeType(MimeType.JPEG.toString());
            item.setPosition(Integer.parseInt(String.valueOf(v.getTag(R.id.tagid))));
            intent.putExtra(AlbumPreviewActivity.EXTRA_ITEM, item);

            intent.putExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE, bundle);
            intent.putExtra(BasePreviewActivity.EXTRA_RESULT_ORIGINAL_ENABLE, false);
            intent.putExtra(BasePreviewActivity.EXTRA_IS_ALLOW_REPEAT, true);
            intent.putExtra(BasePreviewActivity.IS_SELECTED_LISTENER, false);
            intent.putExtra(BasePreviewActivity.IS_SELECTED_CHECK, false);
            intent.putExtra(BasePreviewActivity.IS_ALBUM_URI, false);
            fragment.startActivityForResult(intent, REQUEST_CODE_PREVIEW_CAMRRA);
            if (mGlobalSpec.isCutscenes) {
                if (fragment.getActivity() != null) {
                    fragment.getActivity().overridePendingTransition(R.anim.activity_open, 0);
                }
            }
        });

        mCaptureViews.put(mPosition, viewHolderImageView.rootView);
        mViewHolder.llPhoto.addView(viewHolderImageView.rootView);
        mViewHolder.pvLayout.startTipAlphaAnimation();
        mViewHolder.pvLayout.startOperaeBtnAnimatorMulti();

        // 重置按钮，因为每次点击，都会自动关闭
        mViewHolder.pvLayout.getViewHolder().btnClickOrLong.resetState();
        // 显示右上角
        setSwitchVisibility(View.VISIBLE);

        // hide flash icon
        mViewHolder.imgFlash.setVisibility(mCameraSpec.useImgFlash ? VISIBLE : INVISIBLE);

        // 设置当前模式是图片休闲并存模式
        setState(Constants.STATE_PICTURE_PREVIEW);

        // 禁用长按事件，即禁止录像
        mViewHolder.pvLayout.getViewHolder().btnClickOrLong.setButtonFeatures(BUTTON_STATE_ONLY_CLICK);
    }

    /**
     * 获取当前view的状态
     *
     * @return 状态
     */
    private int getState() {
        return mState;
    }

    /**
     * 设置当前view的状态
     *
     * @param state 状态
     */
    private void setState(int state) {
        this.mState = state;
    }

    /**
     * 设置闪关灯
     */
    private void setFlashLamp() {
        if (!mCameraSpec.useImgFlash) {
            mViewHolder.imgFlash.setVisibility(INVISIBLE);
            return;
        }

        switch (mFlashType) {
            case Constants.TYPE_FLASH_AUTO:
                mViewHolder.imgFlash.setImageResource(mCameraSpec.imageFlashAuto);
                mViewHolder.cameraView.setFlash(Flash.AUTO);
                break;
            case Constants.TYPE_FLASH_ON:
                mViewHolder.imgFlash.setImageResource(mCameraSpec.imageFlashOn);
                mViewHolder.cameraView.setFlash(Flash.TORCH);
                break;
            case Constants.TYPE_FLASH_OFF:
                mViewHolder.imgFlash.setImageResource(mCameraSpec.imageFlashOff);
                mViewHolder.cameraView.setFlash(Flash.OFF);
                break;
            default:
                break;
        }
    }

    /**
     * 返回最多选择的图片数量
     *
     * @return 数量
     */
    private int currentMaxSelectable() {
        // 返回最大选择数量
        return SelectableUtils.getImageMaxCount();
    }

    /**
     * 返回当前所有图片的路径 paths
     */
    private ArrayList<String> getPaths() {
        ArrayList<String> paths = new ArrayList<>();
        for (BitmapData value : mCaptureBitmaps.values()) {
            paths.add(value.getPath());
        }
        return paths;
    }

    /**
     * 返回当前所有图片的路径 uris
     */
    private ArrayList<Uri> getUris(ArrayList<String> paths) {
        ArrayList<Uri> uris = new ArrayList<>();
        for (int i = 0; i < paths.size(); i++) {
            uris.add(mPictureMediaStoreCompat.getUri(paths.get(i)));
        }
        return uris;
    }

    /**
     * 调用停止录像
     *
     * @param isShort 是否因为视频过短而停止
     */
    private void stopRecord(boolean isShort) {
        Log.d(TAG, "stopRecord " + isShort);
        mIsShort = isShort;
        mViewHolder.cameraView.stopVideo();
        if (isShort) {
            // 如果视频过短就是录制不成功
            resetState(TYPE_SHORT);
            mViewHolder.pvLayout.reset();
        } else {
            // 设置成视频播放状态
            setState(Constants.STATE_VIDEO);
        }
    }

    /**
     * 设置闪光灯是否显示，如果不支持，是一直不会显示
     */
    private void setSwitchVisibility(int viewVisibility) {
        if (!PackageManagerUtils.isSupportCameraLedFlash(mContext.getPackageManager())) {
            mViewHolder.imgSwitch.setVisibility(View.GONE);
        } else {
            mViewHolder.imgSwitch.setVisibility(viewVisibility);
        }
    }

    /**
     * 设置fragment
     */
    public void setFragment(CameraFragment fragment) {
        this.fragment = fragment;
    }

    public static class ViewHolder {

        View rootView;
        ChildClickableFrameLayout rlMain;
        ImageViewTouch imgPhoto;
        FrameLayout flShow;
        public ImageView imgFlash;
        public ImageView imgSwitch;
        public PhotoVideoLayoutBase pvLayout;
        public HorizontalScrollView hsvPhoto;
        LinearLayout llPhoto;
        View vLine1;
        View vLine2;
        View vLine3;
        ImageButton btnCancel;
        CameraView cameraView;
        ConstraintLayout clMenu;
        RelativeLayout rlEdit;

        ViewHolder(View rootView) {
            this.rootView = rootView;
            this.rlMain = rootView.findViewById(R.id.rlMain);
            this.imgPhoto = rootView.findViewById(R.id.imgPhoto);
            this.flShow = rootView.findViewById(R.id.flShow);
            this.imgFlash = rootView.findViewById(R.id.imgFlash);
            this.imgSwitch = rootView.findViewById(R.id.imgSwitch);
            this.pvLayout = rootView.findViewById(R.id.pvLayout);
            this.hsvPhoto = rootView.findViewById(R.id.hsvPhoto);
            this.llPhoto = rootView.findViewById(R.id.llPhoto);
            this.vLine1 = rootView.findViewById(R.id.vLine1);
            this.vLine2 = rootView.findViewById(R.id.vLine2);
            this.vLine3 = rootView.findViewById(R.id.vLine3);
            this.btnCancel = rootView.findViewById(R.id.btnCancel);
            this.cameraView = rootView.findViewById(R.id.cameraView);
            this.clMenu = rootView.findViewById(R.id.clMenu);
            this.rlEdit = rootView.findViewById(R.id.rlEdit);
        }

    }

    public static class ViewHolderImageView {
        public View rootView;
        public ImageView imgPhoto;
        public ImageView imgCancel;

        public ViewHolderImageView(View rootView) {
            this.rootView = rootView;
            this.imgPhoto = rootView.findViewById(R.id.imgPhoto);
            this.imgCancel = rootView.findViewById(R.id.imgCancel);
        }

    }
}
