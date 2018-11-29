package io.github.nnkwrik.goodsservice.service.impl;

import com.github.pagehelper.PageHelper;
import io.github.nnkwrik.goodsservice.dao.SearchMapper;
import io.github.nnkwrik.goodsservice.model.po.Goods;
import io.github.nnkwrik.goodsservice.model.po.SearchHistory;
import io.github.nnkwrik.goodsservice.model.vo.inner.GoodsSimpleVo;
import io.github.nnkwrik.goodsservice.service.SearchService;
import io.github.nnkwrik.goodsservice.util.PO2VO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author nnkwrik
 * @date 18/11/21 9:22
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SearchMapper searchMapper;


    @Override
    public List<GoodsSimpleVo> searchByKeyword(List<String> keywordList, int page, int size) {
        PageHelper.startPage(page, size);
        List<Goods> goodsList = searchMapper.findGoodsByKeywords(keywordList);
        return PO2VO.convertList(goodsList,GoodsSimpleVo.class);
    }

    @Override
    public List<String> getUserHistory(String openId) {
        List<SearchHistory> historyPo = searchMapper.findSearchHistory(openId);
        int limit = 10;
        if (historyPo.size() >= limit) {//或者超过一定数量就删除
            searchMapper.deleteOldHistory(openId, historyPo.size() - limit);
        }
        return historyPo.stream()
                .map(SearchHistory::getKeyword)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateUserHistory(String openId, String keyword) {
        if (searchMapper.isExistedHistory(openId, keyword)) {
            searchMapper.updateSearchTime(openId, keyword);
        } else {
            searchMapper.insertHistory(openId, keyword);
        }
    }

    @Override
    public void clearUserHistory(String openId) {
        searchMapper.clearHistory(openId);
    }
}
