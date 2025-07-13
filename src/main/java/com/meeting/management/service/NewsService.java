package com.meeting.management.service;

import com.meeting.management.entity.News;
import java.util.List;
import java.util.Map;

public interface NewsService {
    
    /**
     * 搜索新闻
     */
    List<News> searchNews(Map<String, Object> params, int pageNum, int pageSize);
    
    /**
     * 创建新闻
     */
    boolean createNews(News news);
    
    /**
     * 更新新闻
     */
    boolean updateNews(News news);
    
    /**
     * 删除新闻
     */
    boolean deleteNews(Long id);
    
    /**
     * 根据ID获取新闻详情
     */
    News getNewsById(Long id);
    
    /**
     * 增加浏览次数
     */
    boolean incrementViewCount(Long id);
} 