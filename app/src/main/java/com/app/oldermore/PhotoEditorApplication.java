package com.app.oldermore;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.app.oldermore.utils.GLog;
import com.app.oldermore.widget.BaseToolObject;
import com.app.oldermore.widget.ToolStructureBuilder;

import java.util.List;

public class PhotoEditorApplication extends Application {

    public PhotoEditorApplication() {
        super();

    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppConfigs.getInstance().editorFeatures = buildEditorTools();
        GLog.setLogMode(GLog.LogMode.VERBOSE);
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        AppConfigs.getInstance().deviceWidth = metrics.widthPixels;
        AppConfigs.getInstance().deviceHeight = metrics.heightPixels;
    }


    private List<BaseToolObject> buildEditorTools() {
        ToolStructureBuilder tb = new ToolStructureBuilder(this);
        return tb.build();
    }
}
