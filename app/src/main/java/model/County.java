package model;

/**
 * Created by Administrator on 2016/4/25.
 * 定义一个类描述-----县
 */
public class County {

    private int id = 0;
    private String countyName = null;//县名
    private String countyCode = null;//县编码
    private int cityId = 0;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
