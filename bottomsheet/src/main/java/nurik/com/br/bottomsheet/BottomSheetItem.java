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

package br.com.nurik.bottomsheet;

import android.graphics.drawable.Drawable;

/**
 * Date: 3 MAR 2018
 * Time: 13:58 MSK
 *
 * @author Michael Bel
 */

@SuppressWarnings("all")
public class BottomSheetItem {

    public Drawable icon;
    public CharSequence text;

    public BottomSheetItem() {}

    public BottomSheetItem(CharSequence text, Drawable icon) {
        this.text = text;
        this.icon = icon;
    }
}