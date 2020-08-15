package com.pinyougou.test;

import com.pinyougou.solr.SolrUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class solrImportTest {

    private SolrUtil solrUtil;

    @Before
    public void init() {
        ApplicationContext act = new ClassPathXmlApplicationContext("classpath:spring/spring-solr.xml");
        solrUtil = act.getBean(SolrUtil.class);
    }

    @Test
    public void testAdd() {
        this.solrUtil.batchAdd();
    }

    @Test
    public void testSpec() {
        this.solrUtil.SearchByCondition("网络", "联通2G");
    }
}
