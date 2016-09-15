package com.cyrus.cyrus_microblog.utils;

import com.cyrus.cyrus_microblog.R;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class EmotionUtils implements Serializable {
	
	public static Map<String, Integer> sEmojiMap;
	
	static {
		sEmojiMap = new HashMap<>();
		sEmojiMap.put("[呵呵]", R.drawable.d_hehe);
		sEmojiMap.put("[嘻嘻]", R.drawable.d_xixi);
		sEmojiMap.put("[哈哈]", R.drawable.d_haha);
		sEmojiMap.put("[爱你]", R.drawable.d_aini);
		sEmojiMap.put("[挖鼻屎]", R.drawable.d_wabishi);
		sEmojiMap.put("[吃惊]", R.drawable.d_chijing);
		sEmojiMap.put("[晕]", R.drawable.d_yun);
		sEmojiMap.put("[泪]", R.drawable.d_lei);
		sEmojiMap.put("[馋嘴]", R.drawable.d_chanzui);
		sEmojiMap.put("[抓狂]", R.drawable.d_zhuakuang);
		sEmojiMap.put("[哼]", R.drawable.d_heng);
		sEmojiMap.put("[可爱]", R.drawable.d_keai);
		sEmojiMap.put("[怒]", R.drawable.d_nu);
		sEmojiMap.put("[汗]", R.drawable.d_han);
		sEmojiMap.put("[害羞]", R.drawable.d_haixiu);
		sEmojiMap.put("[睡觉]", R.drawable.d_shuijiao);
		sEmojiMap.put("[钱]", R.drawable.d_qian);
		sEmojiMap.put("[偷笑]", R.drawable.d_touxiao);
		sEmojiMap.put("[笑cry]", R.drawable.d_xiaoku);
		sEmojiMap.put("[doge]", R.drawable.d_doge);
		sEmojiMap.put("[喵喵]", R.drawable.d_miao);
		sEmojiMap.put("[酷]", R.drawable.d_ku);
		sEmojiMap.put("[衰]", R.drawable.d_shuai);
		sEmojiMap.put("[闭嘴]", R.drawable.d_bizui);
		sEmojiMap.put("[鄙视]", R.drawable.d_bishi);
		sEmojiMap.put("[花心]", R.drawable.d_huaxin);
		sEmojiMap.put("[鼓掌]", R.drawable.d_guzhang);
		sEmojiMap.put("[悲伤]", R.drawable.d_beishang);
		sEmojiMap.put("[思考]", R.drawable.d_sikao);
		sEmojiMap.put("[生病]", R.drawable.d_shengbing);
		sEmojiMap.put("[亲亲]", R.drawable.d_qinqin);
		sEmojiMap.put("[怒骂]", R.drawable.d_numa);
		sEmojiMap.put("[太开心]", R.drawable.d_taikaixin);
		sEmojiMap.put("[懒得理你]", R.drawable.d_landelini);
		sEmojiMap.put("[右哼哼]", R.drawable.d_youhengheng);
		sEmojiMap.put("[左哼哼]", R.drawable.d_zuohengheng);
		sEmojiMap.put("[嘘]", R.drawable.d_xu);
		sEmojiMap.put("[委屈]", R.drawable.d_weiqu);
		sEmojiMap.put("[吐]", R.drawable.d_tu);
		sEmojiMap.put("[可怜]", R.drawable.d_kelian);
		sEmojiMap.put("[打哈气]", R.drawable.d_dahaqi);
		sEmojiMap.put("[挤眼]", R.drawable.d_jiyan);
		sEmojiMap.put("[失望]", R.drawable.d_shiwang);
		sEmojiMap.put("[顶]", R.drawable.d_ding);
		sEmojiMap.put("[疑问]", R.drawable.d_yiwen);
		sEmojiMap.put("[困]", R.drawable.d_kun);
		sEmojiMap.put("[感冒]", R.drawable.d_ganmao);
		sEmojiMap.put("[拜拜]", R.drawable.d_baibai);
		sEmojiMap.put("[黑线]", R.drawable.d_heixian);
		sEmojiMap.put("[阴险]", R.drawable.d_yinxian);
		sEmojiMap.put("[打脸]", R.drawable.d_dalian);
		sEmojiMap.put("[傻眼]", R.drawable.d_shayan);
		sEmojiMap.put("[猪头]", R.drawable.d_zhutou);
		sEmojiMap.put("[熊猫]", R.drawable.d_xiongmao);
		sEmojiMap.put("[兔子]", R.drawable.d_tuzi);
	}
	
	public static int getImgByName(String imgName) {
		Integer integer = sEmojiMap.get(imgName);
		return integer == null ? -1 : integer;
	}
}
