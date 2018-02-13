package com.liux.tool;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.util.SparseArray;

/**
 * 2018/2/13
 * By Liux
 * lx0758@qq.com
 */

public class ActivityStarter {
    private static final String TAG = "ActivityStarter";

    public static void startActivityForResult(Activity activity, Intent intent, Callback callback) {
        synchronized (activity) {
//            if (activity instanceof android.support.v4.app.FragmentActivity) {
//                innerSupportFragmentForResult((android.support.v4.app.FragmentActivity) activity, intent, callback);
//            } else {
//                innerFragmentForResult(activity, intent, callback);
//            }
            innerFragmentForResult(activity, intent, callback);
        }
    }

    public static void startActivityForResult(Fragment fragment, Intent intent, Callback callback) {
        startActivityForResult(fragment.getActivity(), intent, callback);
    }

//    private static void innerSupportFragmentForResult(android.support.v4.app.FragmentActivity activity, Intent intent, Callback callback) {
//        InnerSupportFragment fragment = (InnerSupportFragment) activity.getSupportFragmentManager().findFragmentByTag(TAG);
//        if (fragment == null) {
//            fragment = new InnerSupportFragment();
//            activity.getSupportFragmentManager().beginTransaction().add(fragment, TAG).commit();
//        }
//        fragment.startActivityForResult(intent, callback);
//    }

    private static void innerFragmentForResult(Activity activity, Intent intent, Callback callback) {
        InnerFragment fragment = (InnerFragment) activity.getFragmentManager().findFragmentByTag(TAG);
        if (fragment == null) {
            fragment = new InnerFragment();
            activity.getFragmentManager().beginTransaction().add(fragment, TAG).commit();
        }
        fragment.startActivityForResult(intent, callback);
    }

//    public static class InnerSupportFragment extends android.support.v4.app.Fragment {
//        private int requestCode = 0;
//        private SparseArray<Callback> callbackSparseArray = new SparseArray<>();
//
//        public synchronized void startActivityForResult(Intent intent, Callback callback) {
//            int realRequestCode = requestCode++;
//            callbackSparseArray.put(realRequestCode, callback);
//            super.startActivityForResult(intent, realRequestCode);
//        }
//
//        @Override
//        public void onActivityResult(int requestCode, int resultCode, Intent data) {
//            Callback callback = callbackSparseArray.get(requestCode);
//            callbackSparseArray.remove(requestCode);
//            if (callback != null) {
//                callback.onActivityResult(resultCode, data);
//            }
//        }
//    }

    public static class InnerFragment extends android.app.Fragment {
        private int requestCode = 0;
        private SparseArray<Callback> callbackSparseArray = new SparseArray<>();

        public synchronized void startActivityForResult(Intent intent, Callback callback) {
            int realRequestCode = requestCode++;
            callbackSparseArray.put(realRequestCode, callback);
            super.startActivityForResult(intent, realRequestCode);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            Callback callback = callbackSparseArray.get(requestCode);
            callbackSparseArray.remove(requestCode);
            if (callback != null) {
                callback.onActivityResult(resultCode, data);
            }
        }
    }

    public interface Callback {

        void onActivityResult(int resultCode, Intent data);
    }
}
