package com.epeisong.base.view.viewinject;

import java.lang.reflect.Field;

import android.app.Activity;
import android.view.View;

public class ViewInjecter {

    public static void inject(Activity sourceActivity) {
        inject(sourceActivity, sourceActivity.getWindow().getDecorView());
    }

    public static void inject(Object injectedSource, View sourceView) {
        Field[] fields = injectedSource.getClass().getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                ViewInject viewInject = field.getAnnotation(ViewInject.class);
                if (viewInject != null) {
                    int viewId = viewInject.id();
                    try {
                        field.setAccessible(true);
                        /* 当已经被赋值时，不在重复赋值，用于include，inflate情景下的viewinject组合 */
                        if (field.get(injectedSource) == null) {
                            field.set(injectedSource, sourceView.findViewById(viewId));
                        } else {
                            continue;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
