package com.artifex.mupdflib.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import com.artifex.mupdflib.FilePicker;
import com.artifex.mupdflib.MuPDFCore;
import com.artifex.mupdflib.MuPDFPageAdapter;
import com.artifex.mupdflib.MuPDFReaderView;
import com.lx.framework.base.BaseActivity;
import com.lx.framework.http.DownLoadManager;
import com.lx.framework.http.download.ProgressCallBack;
import com.lx.framework.permission.IPermission;
import com.lx.framework.utils.FileUtils;
import com.lx.framework.utils.KLog;
import com.lx.framework.utils.RegexUtils;
import com.lx.framework.utils.ToastUtils;
import com.lx.framework.utils.Utils;

import java.io.File;

import cn.leapinfo.mupdf.BR;
import cn.leapinfo.mupdf.R;
import cn.leapinfo.mupdf.databinding.FragmentAttachPreviewBinding;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2017/9/27.
 */

public class AttachPreviewActivity extends BaseActivity<FragmentAttachPreviewBinding, AttachPreviewViewModel> implements FilePicker.FilePickerSupport{
    private int attachFileType;
    private String attachFileUrl;
    private String attachFileName;

    private MuPDFCore muPDFCore;
    private MuPDFReaderView muPDFReaderView;
    public static final String ATTACH_FILE_URL = "ATTACH_FILE_URL";
    public static final String ATTACH_FILE_NAME = "ATTACH_FILE_NAME";
    public static final String ATTACH_FILE_PATH = "ATTACH_FILE_PATH";
    public static final int IMAGE_FILE = 900;
    public static final int PDF_FILE = 800;
    private String destFileDir;

    public static void startActivity(Context context,String attachFileUrl) {
        startActivity(context,attachFileUrl,"");
    }

    public static void startActivity(Context context,String attachFileUrl, String attachFileName) {
        Bundle bundle = new Bundle();
        bundle.putString(ATTACH_FILE_URL, attachFileUrl);
        if (!TextUtils.isEmpty(attachFileName))bundle.putString(ATTACH_FILE_NAME, attachFileName);
        Intent intent = new Intent(context,AttachPreviewActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public int initVariableId() {
        return BR.viewModel;
    }

    @Override
    public void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            attachFileUrl = bundle.getString(ATTACH_FILE_URL);
            attachFileName = bundle.getString(ATTACH_FILE_NAME);
        }
        attachFileType = isPdfFile(attachFileUrl) ? PDF_FILE : IMAGE_FILE;

        viewModel.title.set(TextUtils.isEmpty(attachFileName) ? "文书附件预览" : attachFileName);

        if (attachFileType == IMAGE_FILE) {
            viewModel.pinchImageView.set(true);
            viewModel.rlPdfContainer.set(false);

            viewModel.attachFileUrl.set(attachFileUrl);

        } else if (attachFileType == PDF_FILE) {
            viewModel.pinchImageView.set(false);
            viewModel.rlPdfContainer.set(true);
        }
        destFileDir = Utils.getContext().getCacheDir().getPath()+File.separator+"document";

        requestPermission(new IPermission() {
            @Override
            public void onGranted() {
                if (!RegexUtils.isURL(attachFileUrl)) {
                    updatePdfFile(attachFileUrl);
                } else {
                    downLoadFile();
                }
            }

            @Override
            public void onDenied(boolean denied) {
                ToastUtils.showLong("请授权读写存储卡的权限,才能完成附件预览");
            }
        },Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void downLoadFile() {
        String destFileName = System.currentTimeMillis() + ".document";
        DownLoadManager.getInstance().load(attachFileUrl, new ProgressCallBack<ResponseBody>(destFileDir, destFileName) {
            @Override
            public void onStart() {
                super.onStart();
                viewModel.pbLoading.set(true);
            }

            @Override
            public void onCompleted() {
                viewModel.pbLoading.set(false);
            }

            @Override
            public void onSuccess(ResponseBody responseBody) {
//                ToastUtils.showShort("文件下载完成！");
                viewModel.pbLoading.set(false);
                updatePdfFile(destFileDir+File.separator+destFileName);
            }

            @Override
            public void progress(final long progress, final long total) {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                ToastUtils.showShort("文件下载失败！");
                viewModel.pbLoading.set(false);
            }
        });
    }

    public void updatePdfFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            muPDFCore = openPdfFile(filePath);
            if (muPDFCore == null) {
                ToastUtils.showShort("打开PDF文件失败");
                finish();
                return;
            }
            int totalPageCount = muPDFCore.countPages();
            if (totalPageCount == 0) {
                ToastUtils.showShort("PDF文件格式错误");
                finish();
                return;
            }
            muPDFCore.setDisplayPages(1); //每页展示一张pdf

            muPDFReaderView = new MuPDFReaderView(this) {
                @Override
                protected void onMoveToChild(int i) {
                    super.onMoveToChild(i);
                    viewModel.pageNumber.set(String.format(" %s / %s ", i + 1, totalPageCount));
                }

                @Override
                protected void onChildSetup(int i, View v) {
                    super.onChildSetup(i, v);
                    viewModel.pbLoading.set(false);
                    viewModel.tvPageNumber.set(true);
                }

            };
            muPDFReaderView.setAdapter(new MuPDFPageAdapter(this, this, muPDFCore));
            muPDFReaderView.setKeepScreenOn(true);
            muPDFReaderView.setScrollingDirectionHorizontal(true);

            binding.rlPdfContainer.addView(muPDFReaderView, new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
            viewModel.pageNumber.set(String.format(" %s / %s ", 1, totalPageCount));
        } else {
            KLog.e("获取文件失败");
        }
    }

    public boolean isPdfFile(String fileUrl) {
        if (!TextUtils.isEmpty(fileUrl)) {
            return fileUrl.endsWith(".pdf") || fileUrl.endsWith(".PDF");
        }
        return false;
    }

    private MuPDFCore openPdfFile(String path) {
        try {
            muPDFCore = new MuPDFCore(this, path);
        } catch (Exception e) {
            KLog.e(e.getMessage());
            return null;
        }
        return muPDFCore;
    }

    @Override
    public void performPickFor(FilePicker picker) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FileUtils.deleteAllInDir(destFileDir);
    }

    @Override
    public int initContentView(Bundle savedInstanceState) {
        return R.layout.fragment_attach_preview;
    }
}
