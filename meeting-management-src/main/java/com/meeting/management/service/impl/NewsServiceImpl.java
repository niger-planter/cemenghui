package com.meeting.management.service.impl;

import com.meeting.management.entity.News;
import com.meeting.management.service.NewsService;
import com.meeting.management.util.JsonFileStorage;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements NewsService {

    private final JsonFileStorage<News> jsonFileStorage;
    private static final String NEWS_FILE = "data/news.json";

    public NewsServiceImpl() {
        this.jsonFileStorage = new JsonFileStorage<>(NEWS_FILE, News.class);
    }

    @Override
    public List<News> searchNews(Map<String, Object> params, int pageNum, int pageSize) {
        List<News> allNews = getAllNews();
        
        // 过滤
        List<News> filteredNews = allNews.stream()
                .filter(news -> {
                    if (params.containsKey("title") && StringUtils.hasText((String) params.get("title"))) {
                        String title = (String) params.get("title");
                        if (!news.getTitle().toLowerCase().contains(title.toLowerCase())) {
                            return false;
                        }
                    }
                    
                    if (params.containsKey("author") && StringUtils.hasText((String) params.get("author"))) {
                        String author = (String) params.get("author");
                        if (!news.getAuthor().toLowerCase().contains(author.toLowerCase())) {
                            return false;
                        }
                    }
                    
                    if (params.containsKey("category") && StringUtils.hasText((String) params.get("category"))) {
                        String category = ((String) params.get("category")).trim().toLowerCase();
                        if (!news.getCategory().trim().toLowerCase().equals(category)) {
                            return false;
                        }
                    }
                    
                    if (params.containsKey("publishDateStart") && params.get("publishDateStart") != null &&
                        params.containsKey("publishDateEnd") && params.get("publishDateEnd") != null) {
                        Date start = (Date) params.get("publishDateStart");
                        Date end = (Date) params.get("publishDateEnd");
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                        String startStr = sdf.format(start);
                        String endStr = sdf.format(end);
                        String newsDateStr = sdf.format(news.getPublishDate());
                        if (newsDateStr.compareTo(startStr) < 0 || newsDateStr.compareTo(endStr) > 0) {
                            return false;
                        }
                    } else if (params.containsKey("publishDate") && params.get("publishDate") != null) {
                        Date publishDate = (Date) params.get("publishDate");
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                        String filterDate = sdf.format(publishDate);
                        String newsDate = sdf.format(news.getPublishDate());
                        if (!filterDate.equals(newsDate)) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .sorted((n1, n2) -> n2.getPublishDate().compareTo(n1.getPublishDate()))
                .collect(Collectors.toList());
        
        // 分页
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, filteredNews.size());
        
        if (start >= filteredNews.size()) {
            return new ArrayList<>();
        }
        
        return filteredNews.subList(start, end);
    }

    @Override
    public boolean createNews(News news) {
        if (news.getTitle() == null || news.getTitle().trim().isEmpty() ||
            news.getAuthor() == null || news.getAuthor().trim().isEmpty() ||
            news.getCategory() == null || news.getCategory().trim().isEmpty() ||
            news.getSummary() == null || news.getSummary().trim().isEmpty() ||
            news.getContent() == null || news.getContent().trim().isEmpty() ||
            news.getPublishDate() == null) {
            return false;
        }
        
        List<News> allNews = getAllNews();
        
        // 设置ID
        Long maxId = allNews.stream()
                .mapToLong(News::getId)
                .max()
                .orElse(0);
        news.setId(maxId + 1);
        
        // 设置时间
        Date now = new Date();
        news.setCreateTime(now);
        news.setUpdateTime(now);
        
        // 设置默认值
        if (news.getStatus() == null) {
            news.setStatus(1); // 默认已发布
        }
        if (news.getViewCount() == null) {
            news.setViewCount(0);
        }
        
        allNews.add(news);
        
        return saveAllNews(allNews);
    }

    @Override
    public boolean updateNews(News news) {
        if (news.getId() == null || news.getTitle() == null || news.getTitle().trim().isEmpty() ||
            news.getAuthor() == null || news.getAuthor().trim().isEmpty() ||
            news.getCategory() == null || news.getCategory().trim().isEmpty() ||
            news.getSummary() == null || news.getSummary().trim().isEmpty() ||
            news.getContent() == null || news.getContent().trim().isEmpty() ||
            news.getPublishDate() == null) {
            return false;
        }
        
        List<News> allNews = getAllNews();
        
        for (int i = 0; i < allNews.size(); i++) {
            if (allNews.get(i).getId().equals(news.getId())) {
                News existingNews = allNews.get(i);
                news.setCreateTime(existingNews.getCreateTime());
                news.setUpdateTime(new Date());
                if (news.getViewCount() == null) {
                    news.setViewCount(existingNews.getViewCount());
                }
                allNews.set(i, news);
                return saveAllNews(allNews);
            }
        }
        
        return false;
    }

    @Override
    public boolean deleteNews(Long id) {
        List<News> allNews = getAllNews();
        
        boolean removed = allNews.removeIf(news -> news.getId().equals(id));
        
        if (removed) {
            return saveAllNews(allNews);
        }
        
        return false;
    }

    @Override
    public News getNewsById(Long id) {
        List<News> allNews = getAllNews();
        
        return allNews.stream()
                .filter(news -> news.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean incrementViewCount(Long id) {
        List<News> allNews = getAllNews();
        
        for (News news : allNews) {
            if (news.getId().equals(id)) {
                news.setViewCount(news.getViewCount() + 1);
                return saveAllNews(allNews);
            }
        }
        
        return false;
    }

    private List<News> getAllNews() {
        try {
            List<News> news = jsonFileStorage.readAll();
            return news != null ? news : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private boolean saveAllNews(List<News> news) {
        try {
            jsonFileStorage.saveAll(news);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
} 