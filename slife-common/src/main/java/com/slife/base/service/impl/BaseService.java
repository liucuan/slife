package com.slife.base.service.impl;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.slife.base.service.IBaseService;
import com.slife.base.vo.DataTable;
import com.slife.constant.SearchParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class BaseService<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements IBaseService<T> {

    protected Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * 是否加载 查询条件
     *
     * @param cnd
     * @param k
     * @param v
     * @return
     */
    private boolean idLoadCnd(String cnd, String k, Object v) {
        return k.startsWith(cnd) && null != v && v.toString().length() > 0;
    }

    /**
     * 加载 搜索条件
     *
     * @param searchParams
     * @param cnd
     */
    private void loadSearchParam(Map<String, Object> searchParams, Condition cnd) {

        if (null != searchParams) {
            searchParams.forEach((k, v) -> {
                if (idLoadCnd(SearchParam.SEARCH_EQ, k, v)) {

                    cnd.eq(k.split(SearchParam.SEARCH_EQ)[1], v);

                } else if (idLoadCnd(SearchParam.SEARCH_LLIKE, k, v)) {

                    cnd.like(k.split(SearchParam.SEARCH_LLIKE)[1], String.valueOf(v), SqlLike.LEFT);

                } else if (idLoadCnd(SearchParam.SEARCH_RLIKE, k, v)) {

                    cnd.like(k.split(SearchParam.SEARCH_RLIKE)[1], String.valueOf(v), SqlLike.RIGHT);

                } else if (idLoadCnd(SearchParam.SEARCH_LIKE, k, v)) {

                    cnd.like(k.split(SearchParam.SEARCH_LIKE)[1], String.valueOf(v));
                }

            });
        }
    }

    /**
     * 加载 排序条件
     */
    private void loadSort(Map<String, String> sorts, Condition cnd) {

        if (null != sorts) {
            StringBuffer stringBuffer = new StringBuffer();
            sorts.forEach((k, v) -> {
                stringBuffer.append(k);
                stringBuffer.append(v.toLowerCase().equals("asc") ? " ASC , " : " DESC ,");
            });
            cnd.orderBy(stringBuffer.toString().trim().substring(0, stringBuffer.length() - 1));
        }
    }

    /**
     * 分页 搜索
     *
     * @param dt
     * @return
     */
    @Override
    public DataTable<T> pageSearch(DataTable<T> dt) {

        Page<T> page = new Page<T>(dt.getPageNumber(), dt.getPageSize());
        Condition cnd = new Condition();

        loadSearchParam(dt.getSearchParams(), cnd);
        loadSort(dt.getSorts(), cnd);

        selectPage(page, cnd);
        dt.setTotal(page.getTotal());
        dt.setRows(page.getRecords());
        return dt;
    }


}
