package com.github.wzq.wheel.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.view.LayoutInflater;
import android.view.View;

import com.github.wzq.wheel.OnWheelChangedListener;
import com.github.wzq.R;
import com.github.wzq.wheel.WheelView;
import com.github.wzq.wheel.adapters.ArrayWheelAdapter;
import com.github.wzq.wheel.models.Address;
import com.github.wzq.wheel.models.CityModel;
import com.github.wzq.wheel.models.DistrictModel;
import com.github.wzq.wheel.models.ProvinceModel;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by wzq on 15/4/23.
 */
public class WheelHelper implements OnWheelChangedListener {

    public static final int TYPE_PROVINCE = 0;
    public static final int TYPE_CITY = 1;
    public static final int TYPE_DISTRICT = 2;


    private Context context;

    private View view;

    private WheelView mViewProvince;
    private WheelView mViewCity;
    private WheelView mViewDistrict;

    private int visibleItemNum = 7;

    private String data;

    private int wheels[] = {R.id.id_province, R.id.id_city, R.id.id_district};

    /**
     * @param context
     * @param view 视图
     * @param data 数据文件名
     * @param wheels 子视图ID
     */
    public WheelHelper(Context context, View view, String data, int[] wheels) {
        this.context = context;
        this.view = view;
        this.data = data;
        this.wheels = wheels;
        setUpViews();
        setUpListener();
        setUpData();
    }

    /**
     * @param context
     * @param data 数据文件名 只支持位于ASSET下的XM文件
     */
    public WheelHelper(Context context, String data) {
        this.context = context;
        this.view = LayoutInflater.from(context).inflate(R.layout.view_address, null);
        this.data = data;
        setUpViews();
        setUpListener();
        setUpData();
    }

    public View getView(){
        return view;
    }

    public Address getCurrent(){
        Address address = new Address(mCurrentProviceName, mCurrentCityName, mCurrentDistrictName, mCurrentZipCode);
        return address;
    }

    /**
     * @param type
     * @param value
     */
    public void setCurrent(int type, String value){
        switch (type){
            case TYPE_PROVINCE:
                mViewProvince.setCurrentItem(Arrays.asList(mProvinceDatas).indexOf(value));
                break;
            case TYPE_CITY:
                mViewCity.setCurrentItem(Arrays.asList(mCitisDatasMap.get(mCurrentProviceName)).indexOf(value));
                break;
            case TYPE_DISTRICT:
                mViewDistrict.setCurrentItem(Arrays.asList(mDistrictDatasMap.get(mCurrentCityName)).indexOf(value));
                break;
        }
    }


    private void setUpViews() {
        mViewProvince = (WheelView) view.findViewById(wheels[0]);
        mViewCity = (WheelView) view.findViewById(wheels[1]);
        mViewDistrict = (WheelView) view.findViewById(wheels[2]);
    }

    private void setUpListener() {
        // 添加change事件
        mViewProvince.addChangingListener(this);
        // 添加change事件
        mViewCity.addChangingListener(this);
        // 添加change事件
        mViewDistrict.addChangingListener(this);
        // 添加onclick事件
    }

    private void setUpData() {
        initProvinceDatas(data);
        mViewProvince.setViewAdapter(new ArrayWheelAdapter<>(context, mProvinceDatas));
        // 设置可见条目数量
        mViewProvince.setVisibleItems(visibleItemNum);
        mViewCity.setVisibleItems(visibleItemNum);
        mViewDistrict.setVisibleItems(visibleItemNum);
        updateCities();
        updateAreas();
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        // TODO Auto-generated method stub
        if (wheel == mViewProvince) {
            updateCities();
        } else if (wheel == mViewCity) {
            updateAreas();
        } else if (wheel == mViewDistrict) {
            mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
            mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
        }
    }

    /**
     * 根据当前的市，更新区WheelView的信息
     */
    private void updateAreas() {
        int pCurrent = mViewCity.getCurrentItem();
        mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        String[] areas = mDistrictDatasMap.get(mCurrentCityName);

        if (areas == null) {
            areas = new String[] { "" };
        }
        mViewDistrict.setViewAdapter(new ArrayWheelAdapter<String>(context, areas));
        mViewDistrict.setCurrentItem(0);
        mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[0];
        mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = mViewProvince.getCurrentItem();
        mCurrentProviceName = mProvinceDatas[pCurrent];
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);
        if (cities == null) {
            cities = new String[] { "" };
        }
        mViewCity.setViewAdapter(new ArrayWheelAdapter<>(context, cities));
        mViewCity.setCurrentItem(0);
        updateAreas();
    }

    public int getVisibleItemNum() {
        return visibleItemNum;
    }

    public void setVisibleItemNum(int visibleItemNum) {
        this.visibleItemNum = visibleItemNum;
        mViewProvince.setVisibleItems(visibleItemNum);
        mViewCity.setVisibleItems(visibleItemNum);
        mViewDistrict.setVisibleItems(visibleItemNum);
    }

    /**
     * 所有省
     */
    protected String[] mProvinceDatas;
    /**
     * key - 省 value - 市
     */
    protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    /**
     * key - 市 values - 区
     */
    protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();

    /**
     * key - 区 values - 邮编
     */
    protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();

    /**
     * 当前省的名称
     */
    protected String mCurrentProviceName;
    /**
     * 当前市的名称
     */
    protected String mCurrentCityName;
    /**
     * 当前区的名称
     */
    protected String mCurrentDistrictName ="";

    /**
     * 当前区的邮政编码
     */
    protected String mCurrentZipCode ="";

    /**
     * 解析省市区的XML数据
     */

    protected void initProvinceDatas(String fileName) {
        fileName = fileName + ".xml";
        List<ProvinceModel> provinceList = null;
        AssetManager asset = context.getAssets();
        try {
            InputStream input = asset.open(fileName);
            // 创建一个解析xml的工厂对象
            SAXParserFactory spf = SAXParserFactory.newInstance();
            // 解析xml
            SAXParser parser = spf.newSAXParser();
            XmlParserHandler handler = new XmlParserHandler();
            parser.parse(input, handler);
            input.close();
            // 获取解析出来的数据
            provinceList = handler.getDataList();
            //*/ 初始化默认选中的省、市、区
            if (provinceList!= null && !provinceList.isEmpty()) {
                mCurrentProviceName = provinceList.get(0).getName();
                List<CityModel> cityList = provinceList.get(0).getCityList();
                if (cityList!= null && !cityList.isEmpty()) {
                    mCurrentCityName = cityList.get(0).getName();
                    List<DistrictModel> districtList = cityList.get(0).getDistrictList();
                    mCurrentDistrictName = districtList.get(0).getName();
                    mCurrentZipCode = districtList.get(0).getZipcode();
                }
            }
            //*/
            mProvinceDatas = new String[provinceList.size()];
            for (int i=0; i< provinceList.size(); i++) {
                // 遍历所有省的数据
                mProvinceDatas[i] = provinceList.get(i).getName();
                List<CityModel> cityList = provinceList.get(i).getCityList();
                String[] cityNames = new String[cityList.size()];
                for (int j=0; j< cityList.size(); j++) {
                    // 遍历省下面的所有市的数据
                    cityNames[j] = cityList.get(j).getName();
                    List<DistrictModel> districtList = cityList.get(j).getDistrictList();
                    String[] distrinctNameArray = new String[districtList.size()];
                    DistrictModel[] distrinctArray = new DistrictModel[districtList.size()];
                    for (int k=0; k<districtList.size(); k++) {
                        // 遍历市下面所有区/县的数据
                        DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        // 区/县对于的邮编，保存到mZipcodeDatasMap
                        mZipcodeDatasMap.put(districtList.get(k).getName(), districtList.get(k).getZipcode());
                        distrinctArray[k] = districtModel;
                        distrinctNameArray[k] = districtModel.getName();
                    }
                    // 市-区/县的数据，保存到mDistrictDatasMap
                    mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
                }
                // 省-市的数据，保存到mCitisDatasMap
                mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {

        }
    }
}
