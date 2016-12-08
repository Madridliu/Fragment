package com.example.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Fragment1 extends Fragment {

	private ListView list;
	private TextView tv;

	public static final int SHOW_RESPONSE = -1;
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_DISTRICT = 2;

	private List<String> data;
	private List<String> data1;
	private List<String> data2;
	private BaseAdapter Adapter;
	private String temp;
	public int currentLevel = 3;
	private String str, str1;
	private String levelPlacePro, levelPlaceCity, levelPlaceDistrict;
	private CallBack mCallBack;
	public static Fragment1 mFragment;

	// 接口回调
	public Fragment1(CallBack mCallBack) {
		this.mCallBack = mCallBack;
	}

	// 单例
	public Fragment1 getInstance(CallBack mCallBack) {
		if (null == mFragment) {
			mFragment = new Fragment1(mCallBack);
		}
		return mFragment;
	}

	// 定义一个接口
	public interface CallBack {
		public void getResult(String result);
	}

	public Fragment1() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.activity_main, container, false);
		list = (ListView) view.findViewById(R.id.list_view);
		tv = (TextView) view.findViewById(R.id.titile_text);
		temp = getResources().getString(R.string.datas); // 拿到本地json数据赋给temp
		try {
			jsonObject = new JSONObject(temp);
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// getJsonData();
		showPlace(str, str1);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (currentLevel == LEVEL_DISTRICT) {
					levelPlaceDistrict = data2.get(position);
					mCallBack.getResult(levelPlaceDistrict);
					currentLevel = 3;
					showPlace(null, null);
				}

				if (currentLevel == LEVEL_CITY) {
					levelPlaceCity = data1.get(position);
					showPlace(str1, levelPlaceCity);
				}

				if (currentLevel == LEVEL_PROVINCE) {
					levelPlacePro = data.get(position); // 存的是字符串，直接就取出对应位置的就是字符串
					showPlace(levelPlacePro, str);
				}
			}
		});
		return view;
	}

	// 点击返回键判断返回省列表或市列表
	public void onBackKey() {
		if (currentLevel == LEVEL_DISTRICT) {
			Adapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, data1);
			list.setAdapter(Adapter);
			tv.setText(levelPlacePro);
			currentLevel = LEVEL_CITY;
		} else if (currentLevel == LEVEL_CITY) {
			Adapter = new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_1, data);
			list.setAdapter(Adapter);
			tv.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {
			mCallBack.getResult("null");
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_RESPONSE:
				if (currentLevel == LEVEL_CITY) {
					String cityT = msg.getData().getString("msgcity");
					showDistrict(cityT);
				}
				if (currentLevel == LEVEL_PROVINCE) {
					String prov = msg.getData().getString("msg");
					showCity(prov);
				}
				if (currentLevel == 3) {
					showProvince();
				}

				break;
			default:
				break;
			}

		};
	};

	private void showPlace(String pro, String citytemp) {

		Message message = new Message();
		message.what = SHOW_RESPONSE;
		Bundle bun = new Bundle();
		bun.putString("msg", pro);
		bun.putString("msgcity", citytemp);
		message.setData(bun);
		handler.sendMessage(message);
	}

	protected void parseWithJson() {
		// TODO Auto-generated method stub
		data = new ArrayList<String>();
		try {
			JSONArray jA = jsonObject.getJSONArray("result");
			for (int i = 1; i < jA.length(); i++) {
				Place place = new Place();
				if (!jA.getJSONObject(i).getString("province")
						.equals(jA.getJSONObject(i - 1).getString("province"))) {
					place.setProvince(jA.getJSONObject(i - 1).getString("province"));
					data.add(place.getProvince());
				}
			}

		} catch (Exception e) {
			Log.e("false", "省份解析失败" + e.getMessage());

		}
	}

	protected void parseWithJson1(String prov) {
		// TODO Auto-generated method stub
		data1 = new ArrayList<String>();
		try {
			JSONArray jA = jsonObject.getJSONArray("result");
			for (int i = 0; i < jA.length(); i++) {
				if (jA.getJSONObject(i).getString("province").equals(prov)) {
					Place place = new Place();
					if (!jA.getJSONObject(i).getString("city")
							.equals(jA.getJSONObject(i + 1).getString("city"))) {
						place.setCity(jA.getJSONObject(i).getString("city"));
						data1.add(place.getCity());
					}
				}
			}

		} catch (Exception e) {
			Log.e("false", "市级解析失败");
		}
	}

	JSONObject jsonObject;

	//
	protected void parseWithJson2(String cityTe) {
		// TODO Auto-generated method stub
		data2 = new ArrayList<String>();
		try {
			JSONArray jA = jsonObject.getJSONArray("result");
			for (int i = 0; i < jA.length(); i++) {
				if (jA.getJSONObject(i).getString("city").equals(cityTe)) {
					Place place = new Place();
					place.setDistrict(jA.getJSONObject(i).getString("district"));
					data2.add(place.getDistrict());
				}
			}

		} catch (Exception e) {
			Log.e("false", "区县级解析失败");
		}
	}

	private void showProvince() {
		currentLevel = LEVEL_PROVINCE;
		parseWithJson();
		Adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, data);
		list.setAdapter(Adapter);
		tv.setText("中国");

	}

	private void showCity(String pro) {
		currentLevel = LEVEL_CITY;
		parseWithJson1(pro);
		Adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, data1);
		list.setAdapter(Adapter);
		tv.setText(pro);
	}

	private void showDistrict(String cityT) {
		currentLevel = LEVEL_DISTRICT;
		parseWithJson2(cityT);
		Adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, data2);
		list.setAdapter(Adapter);
		tv.setText(cityT);

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mFragment = null;
	}
}
