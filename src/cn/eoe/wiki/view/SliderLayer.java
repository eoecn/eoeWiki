/*
 * Copyright (C) 2012 0xlab - http://0xlab.org/
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
 *
 * Authored by Julian Chu <walkingice AT 0xlab.org>
 */

package cn.eoe.wiki.view;

// update the package name to match your app
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import cn.eoe.wiki.utils.L;
/**
 * 定义一个带动画的Slider层。
 * @author <a href="mailto:kris1987@qq.com">Kris.lee</a>
 * @data  2012-8-5
 * @version 1.0.0
 */
public class SliderLayer extends ViewGroup {
    public final static int DURATION = 500;


    protected List<SliderEntity> mListLayers;
    protected Animation mAnimation;
    protected Set<SliderListener> mListeners;

    private int mOpenLayerIndex =0;
    

    public SliderLayer(Context context) {
        this(context, null);
    }

    public SliderLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mListLayers = new ArrayList<SliderEntity>();
        mListeners = new HashSet<SliderListener>();
    }
    public void addLayer(SliderEntity layer)
    {
    	if(layer==null)
    		throw new NullPointerException("Can`t add a null layer");
    	mListLayers.add(layer);
    }
    public int getLayersCount()
    {
    	return mListLayers.size();
    }
    public ViewGroup getCurrentOpeningLayer()
    {
    	return getLayer(mOpenLayerIndex);
    }
    public ViewGroup getLayer(int index)
    {
    	int count = mListLayers.size();
    	if(index>count || index <0)
    	{
    		throw new IllegalArgumentException("Invalid layer index. 0<=index<= "+count);
    	}
    	return mListLayers.get(index).getView();
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
    }
    
    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {
    	 L.d("onLayout left:"+l+";top:"+t+";right:"+r+";bottom:"+b);
    	 L.d("openindex:"+mOpenLayerIndex);
    	 int count = mListLayers.size();
    	 for(int i=0;i<count;i++)
    	 {
    		 SliderEntity layer = mListLayers.get(i);
    		 if(i<=mOpenLayerIndex)
    		 {
    			 layer.view.layout(layer.openX, layer.y, r, b);
    		 }
    		 else
    		 {
    			 layer.view.layout(layer.closeX, layer.y, r+layer.closeX, b);
    		 }
    	 }
    }

    @Override
    public void onMeasure(int w, int h) {
        super.onMeasure(w, h);
        super.measureChildren(w, h);
    }

    public void addSliderListener(SliderListener l) {
    	L.d("add a slider listener");
        mListeners.add(l);
    }
    /**
     * remove the SliderListener if you dont need the SliderListener
     * @param l
     */
    public void removeSliderListener(SliderListener l) {
    	L.d("add a slider listener");
    	if( mListeners.contains(l))
    	{
    		mListeners.remove(l);
    	}
    }

    /* to see if the Sidebar is visible */
    public int openingLayerIndex() {
        return mOpenLayerIndex;
    }
    public void openSidebar(int index) {
    	if(index <= mOpenLayerIndex)
    	{
    		L.e("index must greater than the opening layer index");
    		return;
    	}
    	if(index>mListLayers.size())
    	{
    		L.e("index must less than the max layer index:"+mListLayers.size());
    		return;
    	}
    	SliderEntity layer = mListLayers.get(index);
    	 if (layer.view.getAnimation() != null) {
             return;
         }
//    	 View belowLayer = getBelowLayerView(index);
    	 L.d("try to open:"+index);
         mAnimation = new TranslateAnimation(0, -(layer.closeX-layer.openX), 0, 0);
         mAnimation.setAnimationListener(new OpenListener( layer.view));

         mAnimation.setDuration(DURATION);
         mAnimation.setFillAfter(true);
         mAnimation.setFillEnabled(true);
         
         //No need to lock the mOpenLayerIndex. coz, will not be operated at the same time .
         mOpenLayerIndex = index;
         layer.view.startAnimation(mAnimation);
    }

    public void closeSidebar(int index) {
    	if(index != mOpenLayerIndex)
    	{
    		L.e( "Can`t close the layer which is not opening");
    		return;
    	}
    	SliderEntity layer = mListLayers.get(index);
    	 if (layer.view.getAnimation() != null) {
             return;
         }
        mAnimation = new TranslateAnimation(0, (layer.closeX-layer.openX), 0, 0);
        mAnimation.setAnimationListener(new CloseListener( layer.view));

        mAnimation.setDuration(DURATION);
        mAnimation.setFillAfter(true);
        mAnimation.setFillEnabled(true);
        
        //No need to lock the mOpenLayerIndex. coz, will not be operated at the same time .
        mOpenLayerIndex--;
        layer.view.startAnimation(mAnimation);
    }

    class OpenListener implements Animation.AnimationListener {
        View iContent;

        OpenListener( View content) {
            iContent = content;
        }

        public void onAnimationRepeat(Animation animation) {
        }

        public void onAnimationStart(Animation animation) {
//            iSidebar.setVisibility(View.VISIBLE);
        }

        public void onAnimationEnd(Animation animation) {
            iContent.clearAnimation();
            requestLayout();
            for(SliderListener l:mListeners) {
                l.onSidebarOpened();
            }
        }
    }

    class CloseListener implements Animation.AnimationListener {
        View iContent;

        CloseListener(View content) {
            iContent = content;
        }

        public void onAnimationRepeat(Animation animation) {
        }
        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            iContent.clearAnimation();
            requestLayout();
            for(SliderListener l:mListeners) {
                l.onSidebarClosed();
            }
        }
    }
    public interface SliderListener {
        public void onSidebarOpened();
        public void onSidebarClosed();
        public boolean onContentTouchedWhenOpening();
    }
}
