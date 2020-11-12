package com.artifex.mupdflib.ui;

import android.app.Application;

import com.lx.framework.base.BaseViewModel;
import com.lx.framework.binding.command.BindingCommand;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

public class AttachPreviewViewModel extends BaseViewModel {
    public ObservableField<Boolean> tvPageNumber = new ObservableField<>(false);
    public ObservableField<String> pageNumber = new ObservableField<>();
    public ObservableField<Boolean> pbLoading = new ObservableField<>(false);
    public ObservableField<Boolean> pinchImageView = new ObservableField<>(false);
    public ObservableField<Boolean> rlPdfContainer = new ObservableField<>(false);
    public ObservableField<String> attachFileUrl = new ObservableField<>();
    public ObservableField<String> title = new ObservableField<>();

    public AttachPreviewViewModel(@NonNull Application application) {
        super(application);
    }

    public BindingCommand<Void> backCommand = new BindingCommand<Void>(this::finish);
}
