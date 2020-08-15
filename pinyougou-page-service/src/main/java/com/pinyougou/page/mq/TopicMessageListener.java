package com.pinyougou.page.mq;

import com.pinyougou.model.Item;
import com.pinyougou.mq.MessageInfo;
import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TopicMessageListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        if (message instanceof ObjectMessage) {
            //消息强转
            ObjectMessage objectMessage = (ObjectMessage) message;
            try {
                //获取消息内容
                MessageInfo messageInfo = (MessageInfo) objectMessage.getObject();
                if (messageInfo.getMethod() == MessageInfo.METHOD_UPDATE) {
                    //商品审核通过，则增加静态页
                    List<Item> items = (List<Item>) messageInfo.getContext();
                    Set<Long> ids = getGoodsIds(items);
                    //循环生成静态页
                    for (Long id : ids) {
                        this.itemPageService.buildHtml(id);
                    }
                }else if(messageInfo.getMethod() == MessageInfo.METHOD_DELETE){
                    //删除静态页
                    List<Long> ids = (List<Long>) messageInfo.getContext();
                    for (Long id : ids) {
                        this.itemPageService.deleteHtml(id);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Set<Long> getGoodsIds(List<Item> items) {
        Set<Long> ids = new HashSet<>();
        for (Item item : items) {
            ids.add(item.getGoodsId());
        }
        return ids;
    }
}
