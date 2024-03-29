/*
 * Copyright 2016 Michael Bely
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.nurik.bottomsheet.menu;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 3 MAR 2018
 * Time: 14:03 MSK
 *
 * @author Michael Bel
 */

@SuppressWarnings("all")
public class BottomSheetMenu implements Menu {

    private Context mContext;

    private boolean mIsQwerty;

    private ArrayList<BottomSheetMenuItem> mItems;

    public BottomSheetMenu(Context context) {
        mContext = context;
        mItems = new ArrayList<>();
    }

    public Context getContext() {
        return mContext;
    }

    public MenuItem add(CharSequence title) {
        return add(0, 0, 0, title);
    }

    public MenuItem add(int titleRes) {
        return add(0, 0, 0, titleRes);
    }

    public MenuItem add(int groupId, int itemId, int order, int titleRes) {
        return add(groupId, itemId, order, mContext.getResources().getString(titleRes));
    }

    public MenuItem add(int groupId, int itemId, int order, CharSequence title) {
        BottomSheetMenuItem item = new BottomSheetMenuItem(getContext(), groupId, itemId, 0, order, title);
        mItems.add(item);
        return item;
    }

    public int addIntentOptions(int groupId, int itemId, int order, ComponentName caller, Intent[] specifics, Intent intent, int flags, MenuItem[] outSpecificItems) {
        PackageManager pm = mContext.getPackageManager();
        final List<ResolveInfo> lri = pm.queryIntentActivityOptions(caller, specifics, intent, 0);
        final int N = lri != null ? lri.size() : 0;

        if ((flags & FLAG_APPEND_TO_GROUP) == 0) {
            removeGroup(groupId);
        }

        for (int i = 0; i < N; i++) {
            final ResolveInfo ri = lri.get(i);
            Intent rintent = new Intent(ri.specificIndex < 0 ? intent : specifics[ri.specificIndex]);
            rintent.setComponent(new ComponentName(ri.activityInfo.applicationInfo.packageName, ri.activityInfo.name));
            final MenuItem item = add(groupId, itemId, order, ri.loadLabel(pm)).setIcon(ri.loadIcon(pm)).setIntent(rintent);

            if (outSpecificItems != null && ri.specificIndex >= 0) {
                outSpecificItems[ri.specificIndex] = item;
            }
        }

        return N;
    }

    public SubMenu addSubMenu(CharSequence title) {
        return null;
    }

    public SubMenu addSubMenu(int titleRes) {
        return null;
    }

    public SubMenu addSubMenu(int groupId, int itemId, int order, CharSequence title) {
        return null;
    }

    public SubMenu addSubMenu(int groupId, int itemId, int order, int titleRes) {
        return null;
    }

    public void clear() {
        mItems.clear();
    }

    public void close() {
    }

    private int findItemIndex(int id) {
        final ArrayList<BottomSheetMenuItem> items = mItems;
        final int itemCount = items.size();
        for (int i = 0; i < itemCount; i++) {
            if (items.get(i).getItemId() == id) {
                return i;
            }
        }

        return -1;
    }

    public MenuItem findItem(int id) {
        return mItems.get(findItemIndex(id));
    }

    public MenuItem getItem(int index) {
        return mItems.get(index);
    }

    public boolean hasVisibleItems() {
        final ArrayList<BottomSheetMenuItem> items = mItems;
        final int itemCount = items.size();

        for (int i = 0; i < itemCount; i++) {
            if (items.get(i).isVisible()) {
                return true;
            }
        }

        return false;
    }

    private BottomSheetMenuItem findItemWithShortcut(int keyCode, KeyEvent event) {
        final boolean qwerty = mIsQwerty;
        final ArrayList<BottomSheetMenuItem> items = mItems;
        final int itemCount = items.size();

        for (int i = 0; i < itemCount; i++) {
            BottomSheetMenuItem item = items.get(i);
            final char shortcut = qwerty ? item.getAlphabeticShortcut() : item.getNumericShortcut();
            if (keyCode == shortcut) {
                return item;
            }
        }

        return null;
    }

    public boolean isShortcutKey(int keyCode, KeyEvent event) {
        return findItemWithShortcut(keyCode, event) != null;
    }

    public boolean performIdentifierAction(int id, int flags) {
        final int index = findItemIndex(id);
        if (index < 0) {
            return false;
        }

        return mItems.get(index).invoke();
    }

    public boolean performShortcut(int keyCode, KeyEvent event, int flags) {
        BottomSheetMenuItem item = findItemWithShortcut(keyCode, event);
        if (item == null) {
            return false;
        }

        return item.invoke();
    }

    public void removeGroup(int groupId) {
        final ArrayList<BottomSheetMenuItem> items = mItems;
        int itemCount = items.size();
        int i = 0;
        while (i < itemCount) {
            if (items.get(i).getGroupId() == groupId) {
                items.remove(i);
                itemCount--;
            } else {
                i++;
            }
        }
    }

    public void removeItem(int id) {
        mItems.remove(findItemIndex(id));
    }

    public void setGroupCheckable(int group, boolean checkable, boolean exclusive) {
        final ArrayList<BottomSheetMenuItem> items = mItems;
        final int itemCount = items.size();

        for (int i = 0; i < itemCount; i++) {
            BottomSheetMenuItem item = items.get(i);
            if (item.getGroupId() == group) {
                item.setCheckable(checkable);
                item.setExclusiveCheckable(exclusive);
            }
        }
    }

    public void setGroupEnabled(int group, boolean enabled) {
        final ArrayList<BottomSheetMenuItem> items = mItems;
        final int itemCount = items.size();

        for (int i = 0; i < itemCount; i++) {
            BottomSheetMenuItem item = items.get(i);
            if (item.getGroupId() == group) {
                item.setEnabled(enabled);
            }
        }
    }

    public void setGroupVisible(int group, boolean visible) {
        final ArrayList<BottomSheetMenuItem> items = mItems;
        final int itemCount = items.size();

        for (int i = 0; i < itemCount; i++) {
            BottomSheetMenuItem item = items.get(i);
            if (item.getGroupId() == group) {
                item.setVisible(visible);
            }
        }
    }

    public void setQwertyMode(boolean isQwerty) {
        mIsQwerty = isQwerty;
    }

    public int size() {
        return mItems.size();
    }
}