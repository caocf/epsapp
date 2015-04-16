package com.bdmap.epsloc;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Eps地理服务全局对象
 * @author poet
 *
 */
public class EpsLocationHolder {

    private static EpsLocation sEpsLocation;

    private static List<WeakReference<EpsLocationHolderObserver>> sReferences = new ArrayList<WeakReference<EpsLocationHolderObserver>>();

    public static EpsLocation getEpsLocation() {
        return sEpsLocation;
    }

    public static void setEpsLocation(EpsLocation sEpsLocation) {
        EpsLocationHolder.sEpsLocation = null;
        EpsLocationHolder.sEpsLocation = sEpsLocation;
        if (sReferences.size() > 0) {
            Iterator<WeakReference<EpsLocationHolderObserver>> it = sReferences.iterator();
            while (it.hasNext()) {
                WeakReference<EpsLocationHolderObserver> next = it.next();
                if (next.get() == null) {
                    it.remove();
                } else {
                    next.get().onEpsLocationHolderChange(sEpsLocation);
                }
            }
        }
    }

    public static void addEpsLocationHolderObserver(EpsLocationHolderObserver ob) {
        if (ob != null) {
            sReferences.add(new WeakReference<EpsLocationHolderObserver>(ob));
        }
    }

    public static void removeEpsLocationHolderObserver(EpsLocationHolderObserver ob) {
        Iterator<WeakReference<EpsLocationHolderObserver>> it = sReferences.iterator();
        while (it.hasNext()) {
            WeakReference<EpsLocationHolderObserver> next = it.next();
            if (next.get() == null || next.get() == ob) {
                it.remove();
                if (next.get() == ob) {
                    break;
                }
            }
        }
    }

    public interface EpsLocationHolderObserver {
        void onEpsLocationHolderChange(EpsLocation epsLocation);
    }
}
