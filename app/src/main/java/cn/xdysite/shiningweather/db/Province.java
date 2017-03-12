package cn.xdysite.shiningweather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/3/7.
 */

public class Province extends DataSupport {
    private int id;            //主键
    private int provinceCode;  //省编号
    private String provinceName;

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
