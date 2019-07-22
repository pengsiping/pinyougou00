package com.pinyougou.page.service;

public interface ItemPageService {

    public void genItemHtml(Long goodsId) throws Exception;

    public void deleteItemHtml(Long[] ids);
}
