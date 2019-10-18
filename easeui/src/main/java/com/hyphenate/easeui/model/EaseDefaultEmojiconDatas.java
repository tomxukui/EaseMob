package com.hyphenate.easeui.model;

import com.hyphenate.easeui.R;
import com.hyphenate.easeui.bean.EaseEmojicon;
import com.hyphenate.easeui.bean.EaseEmojicon.Type;
import com.hyphenate.easeui.utils.EaseSmileUtil;

public class EaseDefaultEmojiconDatas {
    
    private static String[] emojis = new String[]{
        EaseSmileUtil.ee_1,
        EaseSmileUtil.ee_2,
        EaseSmileUtil.ee_3,
        EaseSmileUtil.ee_4,
        EaseSmileUtil.ee_5,
        EaseSmileUtil.ee_6,
        EaseSmileUtil.ee_7,
        EaseSmileUtil.ee_8,
        EaseSmileUtil.ee_9,
        EaseSmileUtil.ee_10,
        EaseSmileUtil.ee_11,
        EaseSmileUtil.ee_12,
        EaseSmileUtil.ee_13,
        EaseSmileUtil.ee_14,
        EaseSmileUtil.ee_15,
        EaseSmileUtil.ee_16,
        EaseSmileUtil.ee_17,
        EaseSmileUtil.ee_18,
        EaseSmileUtil.ee_19,
        EaseSmileUtil.ee_20,
        EaseSmileUtil.ee_21,
        EaseSmileUtil.ee_22,
        EaseSmileUtil.ee_23,
        EaseSmileUtil.ee_24,
        EaseSmileUtil.ee_25,
        EaseSmileUtil.ee_26,
        EaseSmileUtil.ee_27,
        EaseSmileUtil.ee_28,
        EaseSmileUtil.ee_29,
        EaseSmileUtil.ee_30,
        EaseSmileUtil.ee_31,
        EaseSmileUtil.ee_32,
        EaseSmileUtil.ee_33,
        EaseSmileUtil.ee_34,
        EaseSmileUtil.ee_35,
       
    };
    
    private static int[] icons = new int[]{
        R.drawable.ee_1,  
        R.drawable.ee_2,  
        R.drawable.ee_3,  
        R.drawable.ee_4,  
        R.drawable.ee_5,  
        R.drawable.ee_6,  
        R.drawable.ee_7,  
        R.drawable.ee_8,  
        R.drawable.ee_9,  
        R.drawable.ee_10,  
        R.drawable.ee_11,  
        R.drawable.ee_12,  
        R.drawable.ee_13,  
        R.drawable.ee_14,  
        R.drawable.ee_15,  
        R.drawable.ee_16,  
        R.drawable.ee_17,  
        R.drawable.ee_18,  
        R.drawable.ee_19,  
        R.drawable.ee_20,  
        R.drawable.ee_21,  
        R.drawable.ee_22,  
        R.drawable.ee_23,  
        R.drawable.ee_24,  
        R.drawable.ee_25,  
        R.drawable.ee_26,  
        R.drawable.ee_27,  
        R.drawable.ee_28,  
        R.drawable.ee_29,  
        R.drawable.ee_30,  
        R.drawable.ee_31,  
        R.drawable.ee_32,  
        R.drawable.ee_33,  
        R.drawable.ee_34,  
        R.drawable.ee_35,  
    };
    
    
    private static final EaseEmojicon[] DATA = createData();
    
    private static EaseEmojicon[] createData(){
        EaseEmojicon[] datas = new EaseEmojicon[icons.length];
        for(int i = 0; i < icons.length; i++){
            datas[i] = new EaseEmojicon(icons[i], emojis[i], Type.NORMAL);
        }
        return datas;
    }
    
    public static EaseEmojicon[] getData(){
        return DATA;
    }
}
